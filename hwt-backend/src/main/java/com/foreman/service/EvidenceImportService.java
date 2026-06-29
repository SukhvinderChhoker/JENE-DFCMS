package com.foreman.service;

import com.foreman.dto.EvidenceDTO;
import com.foreman.model.*;
import com.foreman.repository.*;
import com.foreman.util.InputSanitizeUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class EvidenceImportService {

    private static final DateTimeFormatter EXCEL_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired private EvidenceRepository evidenceRepository;
    @Autowired private CaseRepository caseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EvidenceTypeRepository evidenceTypeRepository;
    @Autowired private InputSanitizeUtil sanitizeUtil;

    public void generateTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=JENE-DFCMS_Evidence_Import_Template.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Evidence");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle requiredStyle = workbook.createCellStyle();
        Font requiredFont = workbook.createFont();
        requiredFont.setBold(true);
        requiredFont.setColor(IndexedColors.DARK_RED.getIndex());
        requiredStyle.setFont(requiredFont);

        String[] headers = {
            "Reference*", "Case Reference*", "Type*",
            "Description of Artefact", "Date of Induction (dd/MM/yyyy)",
            "Manufacturer Name", "Make / Model No", "Serial Number",
            "OS Type", "Storage Capacity", "Device Locked (Yes/No)",
            "Condition at Receipt", "Sealed (Yes/No)",
            "Originator (Army/Navy/AirForce/HQ IDS/Others)", "Originator Unit (if Others)",
            "Depositor Name", "Depositor Contact",
            "Storage Location*", "Evidence Bag Number",
            "Comments / Observations"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        String[][] dummyData = {
            // Row 1 - PC
            {"EVD-001", "CF-2026-001", "PC",
             "Dell Optiplex 7090 desktop from suspect workstation, Room 412", "15/06/2026",
             "Dell", "OptiPlex 7090", "DL7090-2026-XYZ",
             "Windows 11 Pro", "512GB SSD", "Yes",
             "Good", "Yes",
             "Army", "",
             "Subedar Rajvir Singh", "+91-9876543210",
             "Evidence Locker A", "BAG-001",
             "Seized during raid on premises. HDD cloned on-site."},
            // Row 2 - Laptop
            {"EVD-002", "CF-2026-001", "Laptop",
             "Lenovo ThinkPad X1 Carbon - used for data staging", "16/06/2026",
             "Lenovo", "ThinkPad X1 Carbon Gen 11", "LT-X1C-2025-9876",
             "Windows 10 Pro", "1TB SSD", "Yes",
             "Good", "Yes",
             "Navy", "",
             "Lieutenant Commander A. Verma", "+91-9812345678",
             "Evidence Locker A", "BAG-002",
             "Secondary device found in hidden compartment."},
            // Row 3 - Mobile
            {"EVD-003", "CF-2026-004", "Mobile",
             "Samsung Galaxy S24 Ultra - company issued phone", "18/06/2026",
             "Samsung", "Galaxy S24 Ultra", "SM-S928B-2026-4567",
             "Android 14", "256GB", "No",
             "Fair", "No",
             "AirForce", "",
             "Warrant Officer Deepak Mehta", "+91-9765432109",
             "Evidence Locker B", "BAG-003",
             "Screen cracked. Found in employee desk drawer."},
            // Row 4 - Ext HDD/SSD
            {"EVD-004", "CF-2026-002", "Ext HDD/SSD",
             "SanDisk Extreme Pro 1TB portable SSD", "20/06/2026",
             "SanDisk", "Extreme Pro Portable SSD", "SD-EP1TB-2026-1122",
             "N/A", "1TB", "No",
             "Good", "No",
             "HQ IDS", "",
             "Inspector Vikram Thapa", "+91-9654321098",
             "Evidence Locker A", "BAG-004",
             "Connected to compromised server. Contains exfiltrated data."},
            // Row 5 - Smart Device
            {"EVD-005", "CF-2026-006", "Smart Device",
             "Dahua IP Camera DH-IPC-HFW5442T - network surveillance", "22/06/2026",
             "Dahua", "IPC-HFW5442T", "DH-5442T-2025-3344",
             "Linux (embedded)", "256GB microSD", "Yes",
             "Good", "Yes",
             "Army", "",
             "Havildar Manpreet Singh", "+91-9543210987",
             "Server Room, Building C", "BAG-005",
             "CCTV camera from server room. May contain footage of suspect movement."},
            // Row 6 - OM
            {"EVD-006", "CF-2026-003", "OM",
             "HP LaserJet Pro M404dn - office printer with network access", "24/06/2026",
             "HP", "LaserJet Pro M404dn", "HP-M404-2024-5566",
             "N/A", "N/A", "N/A",
             "Good", "Yes",
             "Others", "31 Signal Regiment",
             "Naik Tarun Das", "+91-9432109876",
             "Evidence Locker C", "BAG-006",
             "Printer used for printing classified documents. Contains print logs."},
            // Row 7 - PC
            {"EVD-007", "CF-2026-005", "PC",
             "Custom built workstation - engineering department", "25/06/2026",
             "Asus", "ProArt Z790-Creator", "ASUS-Z790-2026-7788",
             "Ubuntu 22.04 LTS", "2TB NVMe SSD", "No",
             "Damaged", "No",
             "Navy", "",
             "Petty Officer Ravi Shankar", "+91-9321098765",
             "Evidence Locker A", "BAG-007",
             "Hard drive removed. Casing shows physical damage."},
            // Row 8 - Laptop
            {"EVD-008", "CF-2026-002", "Laptop",
             "Apple MacBook Pro 16-inch - finance department", "26/06/2026",
             "Apple", "MacBook Pro M3 Max", "MBP16-M3-2025-9900",
             "macOS Sonoma", "1TB SSD", "Yes",
             "Good", "Yes",
             "AirForce", "",
             "Squadron Leader Priya Nair", "+91-9210987654",
             "Evidence Locker B", "BAG-008",
             "Contains fraudulent email client data. T2 security chip enabled."},
            // Row 9 - Mobile
            {"EVD-009", "CF-2026-004", "Mobile",
             "iPhone 15 Pro Max - personal device of suspect", "27/06/2026",
             "Apple", "iPhone 15 Pro Max", "IP15PM-2024-1123",
             "iOS 17.5", "512GB", "Yes",
             "Cracked", "No",
             "HQ IDS", "",
             "Inspector General Meera Joshi", "+91-9109876543",
             "Evidence Locker B", "BAG-009",
             "Screen severely cracked. Found at hotel room 412."},
            // Row 10 - Ext HDD/SSD
            {"EVD-010", "CF-2026-006", "Ext HDD/SSD",
             "Western Digital My Passport 4TB external HDD", "28/06/2026",
             "Western Digital", "My Passport 4TB", "WD-MP4TB-2025-4455",
             "N/A", "4TB", "No",
             "Scratched", "No",
             "Others", "14 Corps Intelligence Unit",
             "Colonel Arjun Mehta", "+91-9098765432",
             "Evidence Locker C", "BAG-010",
             "Recovered from suspect vehicle. Contains backups of deleted files."},
            // Row 11 - Smart Device
            {"EVD-011", "CF-2026-005", "Smart Device",
             "Amazon Echo Show 10 - smart display found at premises", "29/06/2026",
             "Amazon", "Echo Show 10 (3rd Gen)", "AES10-2023-6677",
             "Fire OS", "32GB", "Yes",
             "Good", "Yes",
             "Army", "",
             "Captain Suresh Raina", "+91-9987654321",
             "Evidence Locker A", "BAG-011",
             "Voice recordings may contain relevant conversations."},
            // Row 12 - PC
            {"EVD-012", "CF-2026-003", "PC",
             "Lenovo ThinkCentre M920t - marketing department server", "30/06/2026",
             "Lenovo", "ThinkCentre M920t", "TC-M920T-2022-8899",
             "Windows Server 2019", "4TB RAID", "Yes",
             "Water Damaged", "No",
             "Navy", "",
             "Commander Sanjay Kulkarni", "+91-9876501234",
             "Evidence Locker A", "BAG-012",
             "Server drives removed. Water damage visible on casing. Drives appear intact."}
        };

        for (int r = 0; r < dummyData.length; r++) {
            Row row = sheet.createRow(r + 1);
            for (int c = 0; c < dummyData[r].length; c++) {
                row.createCell(c).setCellValue(dummyData[r][c]);
            }
        }

        sheet.setColumnWidth(0, 3500);   // Reference
        sheet.setColumnWidth(1, 4500);   // Case Reference
        sheet.setColumnWidth(2, 3500);   // Type
        sheet.setColumnWidth(3, 7000);   // Description
        sheet.setColumnWidth(4, 4500);   // Date of Induction
        sheet.setColumnWidth(5, 4000);   // Manufacturer
        sheet.setColumnWidth(6, 5500);   // Make/Model
        sheet.setColumnWidth(7, 5000);   // Serial Number
        sheet.setColumnWidth(8, 4000);   // OS Type
        sheet.setColumnWidth(9, 3500);   // Storage
        sheet.setColumnWidth(10, 3500);  // Device Locked
        sheet.setColumnWidth(11, 4000);  // Condition
        sheet.setColumnWidth(12, 3000);  // Sealed
        sheet.setColumnWidth(13, 6000);  // Originator
        sheet.setColumnWidth(14, 5500);  // Originator Unit
        sheet.setColumnWidth(15, 5500);  // Depositor Name
        sheet.setColumnWidth(16, 4500);  // Depositor Contact
        sheet.setColumnWidth(17, 5000);  // Storage Location
        sheet.setColumnWidth(18, 4000);  // Bag Number
        sheet.setColumnWidth(19, 8000);  // Comments

        Sheet typeSheet = workbook.createSheet("Valid Types");
        String[] types = {"PC", "Laptop", "Ext HDD/SSD", "Mobile", "Smart Device", "OM"};
        CellStyle typeHeaderStyle = workbook.createCellStyle();
        typeHeaderStyle.setFont(headerFont);
        typeHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        typeHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row typeHeader = typeSheet.createRow(0);
        typeHeader.createCell(0).setCellValue("Valid Evidence Types");
        typeHeader.getCell(0).setCellStyle(typeHeaderStyle);

        for (int i = 0; i < types.length; i++) {
            typeSheet.createRow(i + 1).createCell(0).setCellValue(types[i]);
        }
        typeSheet.setColumnWidth(0, 5000);

        Sheet osSheet = workbook.createSheet("Valid OS Types");
        String[] osTypes = {"Windows", "Linux", "macOS", "Android", "iOS", "Other", "N/A"};
        CellStyle osHeaderStyle = workbook.createCellStyle();
        osHeaderStyle.setFont(headerFont);
        osHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        osHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row osHeader = osSheet.createRow(0);
        osHeader.createCell(0).setCellValue("Valid OS Types");
        osHeader.getCell(0).setCellStyle(osHeaderStyle);

        for (int i = 0; i < osTypes.length; i++) {
            osSheet.createRow(i + 1).createCell(0).setCellValue(osTypes[i]);
        }
        osSheet.setColumnWidth(0, 5000);

        Sheet condSheet = workbook.createSheet("Valid Conditions");
        String[] conditions = {"Good", "Fair", "Poor", "Damaged", "Scratched", "Water Damaged", "Cracked"};
        CellStyle condHeaderStyle = workbook.createCellStyle();
        condHeaderStyle.setFont(headerFont);
        condHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        condHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row condHeader = condSheet.createRow(0);
        condHeader.createCell(0).setCellValue("Valid Conditions");
        condHeader.getCell(0).setCellStyle(condHeaderStyle);

        for (int i = 0; i < conditions.length; i++) {
            condSheet.createRow(i + 1).createCell(0).setCellValue(conditions[i]);
        }
        condSheet.setColumnWidth(0, 5000);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @Transactional
    public List<Map<String, Object>> importFromExcel(MultipartFile file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Map<String, Object>> results = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> result = processRow(row, user);
                results.add(result);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("row", 0);
            error.put("success", false);
            error.put("error", "Failed to read Excel file: " + e.getMessage());
            results.add(error);
        }

        return results;
    }

    private Map<String, Object> processRow(Row row, User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("row", row.getRowNum() + 1);

        try {
            String reference = getCellValue(row, 0);
            String caseRef = getCellValue(row, 1);
            String typeStr = getCellValue(row, 2);

            if (reference == null || reference.trim().isEmpty()) {
                throw new RuntimeException("Reference is required");
            }
            if (caseRef == null || caseRef.trim().isEmpty()) {
                throw new RuntimeException("Case Reference is required");
            }
            if (typeStr == null || typeStr.trim().isEmpty()) {
                throw new RuntimeException("Type is required");
            }

            if (evidenceRepository.findByReference(reference.trim()).isPresent()) {
                throw new RuntimeException("Evidence Reference '" + reference + "' already exists");
            }

            Case caseEntity = caseRepository.findByReference(caseRef.trim())
                    .orElseThrow(() -> new RuntimeException("Case '" + caseRef + "' not found"));

            EvidenceType evidenceType = evidenceTypeRepository.findByEvidenceType(typeStr.trim())
                    .orElseThrow(() -> new RuntimeException("Invalid type '" + typeStr + "'. Valid: PC, Laptop, Ext HDD/SSD, Mobile, Smart Device, OM"));

            Evidence evidence = new Evidence();
            evidence.setReference(reference.trim());
            evidence.setType(evidenceType);
            evidence.setCaseEntity(caseEntity);
            evidence.setUser(user);
            evidence.setCurrentStatus(EvidenceStatus.INACTIVE);
            evidence.setDateAdded(LocalDateTime.now());

            evidence.setEvidenceDescription(sanitizeUtil.sanitize(getCellValue(row, 3)));

            String dateStr = getCellValue(row, 4);
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                try {
                    evidence.setDateOfInduction(LocalDate.parse(dateStr.trim(), EXCEL_DATE));
                } catch (DateTimeParseException e) {
                    try {
                        evidence.setDateOfInduction(LocalDate.parse(dateStr.trim()));
                    } catch (DateTimeParseException e2) {
                        throw new RuntimeException("Invalid Date of Induction format. Use dd/MM/yyyy");
                    }
                }
            }

            evidence.setManufacturerName(sanitizeUtil.sanitize(getCellValue(row, 5)));
            evidence.setMakeModelNo(sanitizeUtil.sanitize(getCellValue(row, 6)));
            evidence.setSerialNumber(sanitizeUtil.sanitize(getCellValue(row, 7)));
            evidence.setOsType(sanitizeUtil.sanitize(getCellValue(row, 8)));
            evidence.setStorageCapacity(sanitizeUtil.sanitize(getCellValue(row, 9)));
            evidence.setDeviceLocked(parseYesNo(getCellValue(row, 10)));
            evidence.setConditionAtReceipt(sanitizeUtil.sanitize(getCellValue(row, 11)));
            evidence.setSealedStatus(parseYesNo(getCellValue(row, 12)));
            evidence.setOriginator(sanitizeUtil.sanitize(getCellValue(row, 13)));
            evidence.setOriginatorUnit(sanitizeUtil.sanitize(getCellValue(row, 14)));
            evidence.setDepositorName(sanitizeUtil.sanitize(getCellValue(row, 15)));
            evidence.setDepositorContact(sanitizeUtil.sanitize(getCellValue(row, 16)));
            evidence.setLocation(sanitizeUtil.sanitize(getCellValue(row, 17)));

            String location = getCellValue(row, 17);
            if (location == null || location.trim().isEmpty()) {
                throw new RuntimeException("Storage Location is required");
            }

            evidence.setEvidenceBagNumber(sanitizeUtil.sanitize(getCellValue(row, 18)));
            evidence.setComment(sanitizeUtil.sanitize(getCellValue(row, 19)));

            evidence = evidenceRepository.save(evidence);

            EvidenceStatus es = new EvidenceStatus();
            es.setEvidence(evidence);
            es.setStatus(EvidenceStatus.INACTIVE);
            es.setDateTime(LocalDateTime.now());
            es.setUser(user);
            evidence.getStatuses().add(es);
            evidenceRepository.save(evidence);

            result.put("success", true);
            result.put("reference", reference);
            result.put("evidenceId", evidence.getId());
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .format(EXCEL_DATE);
                }
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val) && !Double.isInfinite(val)) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try { return cell.getStringCellValue(); } catch (Exception e) {
                    try { return String.valueOf(cell.getNumericCellValue()); } catch (Exception e2) { return null; }
                }
            default:
                return null;
        }
    }

    private Boolean parseYesNo(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        String v = value.trim().toUpperCase();
        if ("Y".equals(v) || "YES".equals(v) || "TRUE".equals(v)) return true;
        if ("N".equals(v) || "NO".equals(v) || "FALSE".equals(v)) return false;
        return null;
    }
}
