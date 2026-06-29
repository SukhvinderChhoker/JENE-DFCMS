package com.foreman.service;

import com.foreman.model.*;
import com.foreman.repository.*;
import com.foreman.util.InputSanitizeUtil;
import org.apache.poi.ss.usermodel.*;
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
public class CaseImportService {

    private static final DateTimeFormatter EXCEL_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired private CaseRepository caseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CaseClassificationRepository caseClassificationRepository;
    @Autowired private CaseTypeRepository caseTypeRepository;
    @Autowired private CasePriorityRepository casePriorityRepository;
    @Autowired private InputSanitizeUtil sanitizeUtil;

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
            String caseName = getCellValue(row, 1);

            if (reference == null || reference.trim().isEmpty()) {
                throw new RuntimeException("Reference is required");
            }
            if (caseName == null || caseName.trim().isEmpty()) {
                throw new RuntimeException("Case Name is required");
            }

            if (caseRepository.findByReference(reference.trim()).isPresent()) {
                throw new RuntimeException("Reference '" + reference + "' already exists");
            }

            String background = getCellValue(row, 2);
            String location = getCellValue(row, 3);
            if (location == null || location.trim().isEmpty()) {
                throw new RuntimeException("Location is required");
            }

            Case caseEntity = new Case();
            caseEntity.setReference(reference.trim());
            caseEntity.setCaseName(caseName.trim());
            caseEntity.setBackground(background != null ? sanitizeUtil.sanitize(background.trim()) : "");
            caseEntity.setLocation(sanitizeUtil.sanitize(location.trim()));

            String classificationStr = getCellValue(row, 4);
            if (classificationStr != null && !classificationStr.trim().isEmpty()) {
                caseClassificationRepository.findByClassification(classificationStr.trim())
                    .ifPresent(caseEntity::setClassification);
            }

            String caseTypeStr = getCellValue(row, 5);
            if (caseTypeStr != null && !caseTypeStr.trim().isEmpty()) {
                caseTypeRepository.findByCaseType(caseTypeStr.trim())
                    .ifPresent(caseEntity::setCaseType);
            }

            String priorityStr = getCellValue(row, 6);
            if (priorityStr != null && !priorityStr.trim().isEmpty()) {
                Optional<CasePriority> cpOpt = casePriorityRepository.findByCasePriority(priorityStr.trim());
                if (cpOpt.isPresent()) {
                    CasePriority cp = cpOpt.get();
                    caseEntity.setCasePriority(cp);
                    caseEntity.setCasePriorityColour(cp.getColour());
                }
            }

            String deadlineStr = getCellValue(row, 7);
            if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
                try {
                    LocalDate deadline = LocalDate.parse(deadlineStr.trim(), EXCEL_DATE);
                    caseEntity.setDeadline(deadline.atTime(23, 59));
                } catch (DateTimeParseException e) {
                    try {
                        LocalDate deadline = LocalDate.parse(deadlineStr.trim());
                        caseEntity.setDeadline(deadline.atTime(23, 59));
                    } catch (DateTimeParseException e2) {
                        throw new RuntimeException("Invalid deadline format. Use dd/MM/yyyy");
                    }
                }
            } else {
                caseEntity.setDeadline(LocalDateTime.now().plusDays(30));
            }

            String justification = getCellValue(row, 8);
            if (justification != null && !justification.trim().isEmpty()) {
                caseEntity.setJustification(sanitizeUtil.sanitize(justification.trim()));
            }

            caseEntity.setCocReceived(parseYesNo(getCellValue(row, 9)));
            caseEntity.setAgencyLetterReceived(parseYesNo(getCellValue(row, 10)));
            caseEntity.setAuthorityLetterReceived(parseYesNo(getCellValue(row, 11)));
            caseEntity.setConsentFormReceived(parseYesNo(getCellValue(row, 12)));
            caseEntity.setCaseHistoryReceived(parseYesNo(getCellValue(row, 13)));
            caseEntity.setHandlingTakingFormReceived(parseYesNo(getCellValue(row, 14)));
            caseEntity.setEvidencePhotosReceived(parseYesNo(getCellValue(row, 15)));
            caseEntity.setSeizureMemoReceived(parseYesNo(getCellValue(row, 16)));
            caseEntity.setWitnessStatementReceived(parseYesNo(getCellValue(row, 17)));
            caseEntity.setOtherDocumentsReceived(parseYesNo(getCellValue(row, 18)));

            caseEntity = caseRepository.save(caseEntity);
            final Case savedCase = caseEntity;
            savedCase.addChange(user);
            caseRepository.save(savedCase);

            savedCase.setStatus(CaseStatus.CREATED, user, "Imported from Excel");
            caseRepository.save(savedCase);

            result.put("success", true);
            result.put("reference", reference);
            result.put("caseName", caseName);
            result.put("caseId", savedCase.getId());
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
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private boolean parseYesNo(String value) {
        if (value == null) return false;
        String v = value.trim().toUpperCase();
        return "Y".equals(v) || "YES".equals(v) || "TRUE".equals(v) || "1".equals(v);
    }
}
