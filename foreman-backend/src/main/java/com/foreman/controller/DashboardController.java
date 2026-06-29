package com.foreman.controller;

import com.foreman.dto.DashboardDTO;
import com.foreman.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<DashboardDTO> getDashboard() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }
}
