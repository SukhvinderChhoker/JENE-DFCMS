package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.dto.DashboardDTO;
import com.foreman.model.User;
import com.foreman.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private RoleHelper roleHelper;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard(HttpServletRequest request) {
        User user = roleHelper.getCurrentUser(request);
        if (user == null) throw new RuntimeException("Unauthorized");
        return ResponseEntity.ok(reportService.getDashboardStats());
    }
}
