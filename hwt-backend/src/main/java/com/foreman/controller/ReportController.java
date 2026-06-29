package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.dto.DashboardDTO;
import com.foreman.model.User;
import com.foreman.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private RoleHelper roleHelper;

    private User requireAuth(HttpServletRequest request) {
        User user = roleHelper.getCurrentUser(request);
        if (user == null) throw new RuntimeException("Unauthorized");
        return user;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(reportService.getDashboardStats());
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<Map<String, Object>> getCaseReport(@PathVariable Long caseId, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(reportService.generateCaseReport(caseId));
    }

    @GetMapping("/cases/{caseId}")
    public ResponseEntity<Map<String, Object>> getCaseReportAlt(@PathVariable Long caseId, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(reportService.generateCaseReport(caseId));
    }

    @GetMapping("/case/{caseId}/activity")
    public ResponseEntity<List<Map<String, Object>>> getCaseActivityReport(@PathVariable Long caseId, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(reportService.getCaseActivityReport(caseId));
    }

    @GetMapping("/cases/{caseId}/activity")
    public ResponseEntity<List<Map<String, Object>>> getCaseActivityReportAlt(@PathVariable Long caseId, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(reportService.getCaseActivityReport(caseId));
    }
}
