package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.model.User;
import com.foreman.service.CaseImportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases/import")
public class CaseImportController {

    @Autowired
    private CaseImportService caseImportService;

    @Autowired
    private RoleHelper roleHelper;

    private User requireAuth(HttpServletRequest request) {
        User user = roleHelper.getCurrentUser(request);
        if (user == null) throw new RuntimeException("Unauthorized");
        return user;
    }

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=JENE-DFCMS_Case_Import_Template.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Cases");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle requiredStyle = workbook.createCellStyle();
        Font requiredFont = workbook.createFont();
        requiredFont.setBold(true);
        requiredFont.setColor(IndexedColors.RED.getIndex());
        requiredStyle.setFont(requiredFont);

        String[] headers = {
            "Reference*", "Case Name*", "Background", "Location*",
            "Classification", "Case Type", "Priority",
            "Deadline (dd/MM/yyyy)", "Justification",
            "CoC Received (Y/N)", "Agency Letter (Y/N)",
            "Authority Letter (Y/N)", "Consent Form (Y/N)",
            "Case History (Y/N)", "Handling Form (Y/N)",
            "Evidence Photos (Y/N)", "Seizure Memo (Y/N)",
            "Witness Statement (Y/N)", "Other Documents (Y/N)"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Row exampleRow = sheet.createRow(1);
        String[] examples = {
            "CF-2026-010", "Data Breach Investigation", "Unauthorized access to corporate network detected on 15 June", "HQ Building, Floor 3",
            "Confidential", "Incident Response", "High",
            "30/07/2026", "Immediate investigation required due to data sensitivity",
            "Y", "Y", "Y", "N", "N", "N", "Y", "Y", "N", "N"
        };
        for (int i = 0; i < examples.length; i++) {
            exampleRow.createCell(i).setCellValue(examples[i]);
        }

        Row exampleRow2 = sheet.createRow(2);
        String[] examples2 = {
            "CF-2026-011", "Insider Threat Analysis", "Suspicious USB activity from engineering department", "Engineering Wing, Room 204",
            "Secret", "Internal Investigation", "Critical",
            "15/07/2026", "Potential IP theft - high priority",
            "Y", "N", "Y", "N", "N", "N", "N", "Y", "Y", "N"
        };
        for (int i = 0; i < examples2.length; i++) {
            exampleRow2.createCell(i).setCellValue(examples2[i]);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, i < 3 ? 6000 : i < 7 ? 4000 : i < 9 ? 5000 : 3500);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> importCases(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canCreateCases(user) && !roleHelper.canManageCases(user)) {
            throw new RuntimeException("Access denied: Cannot import cases");
        }

        List<Map<String, Object>> results = caseImportService.importFromExcel(file, user.getId());

        long successCount = results.stream().filter(r -> (boolean) r.get("success")).count();
        long errorCount = results.size() - successCount;

        return ResponseEntity.ok(Map.of(
            "totalRows", results.size(),
            "successCount", successCount,
            "errorCount", errorCount,
            "results", results
        ));
    }
}
