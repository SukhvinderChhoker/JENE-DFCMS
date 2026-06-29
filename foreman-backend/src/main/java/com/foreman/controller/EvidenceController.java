package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.dto.ChainOfCustodyDTO;
import com.foreman.dto.EvidenceDTO;
import com.foreman.model.ChainOfCustody;
import com.foreman.model.EvidenceHistory;
import com.foreman.model.User;
import com.foreman.service.EvidenceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    @Autowired
    private EvidenceService evidenceService;

    @Autowired
    private RoleHelper roleHelper;

    private User requireAuth(HttpServletRequest request) {
        User user = roleHelper.getCurrentUser(request);
        if (user == null) throw new RuntimeException("Unauthorized");
        return user;
    }

    @GetMapping
    public ResponseEntity<List<EvidenceDTO>> getAllEvidence(@RequestParam(required = false) Long userId, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(evidenceService.getAllEvidence(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvidenceDTO> getEvidenceById(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(evidenceService.getEvidenceById(id));
    }

    @PostMapping("/case/{caseId}")
    public ResponseEntity<EvidenceDTO> createEvidence(
            @PathVariable Long caseId,
            @RequestBody EvidenceDTO evidenceDTO,
            @RequestParam Long userId,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Only Investigators, Case Managers, and Admins can create evidence");
        }
        return ResponseEntity.ok(evidenceService.createEvidence(caseId, evidenceDTO, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvidenceDTO> updateEvidence(
            @PathVariable Long id,
            @RequestBody EvidenceDTO evidenceDTO,
            @RequestParam Long userId,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Only Investigators, Case Managers, and Admins can update evidence");
        }
        return ResponseEntity.ok(evidenceService.updateEvidence(id, evidenceDTO, userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EvidenceDTO> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Only Investigators, Case Managers, and Admins can change evidence status");
        }
        String status = request.get("status");
        String note = request.get("note");
        Long userId = Long.parseLong(request.get("userId"));
        return ResponseEntity.ok(evidenceService.changeStatus(id, status, note, userId));
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<ChainOfCustodyDTO> checkIn(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Only Investigators, Case Managers, and Admins can check in evidence");
        }
        String custodian = (String) request.get("custodian");
        LocalDateTime date = request.containsKey("date") && request.get("date") != null
                ? LocalDateTime.parse((String) request.get("date")) : LocalDateTime.now();
        String comment = (String) request.get("comment");
        Long userId = request.containsKey("userId") && request.get("userId") != null
                ? Long.parseLong(request.get("userId").toString()) : user.getId();
        return ResponseEntity.ok(evidenceService.checkIn(id, custodian, date, comment, userId));
    }

    @PutMapping("/{id}/check-out")
    public ResponseEntity<ChainOfCustodyDTO> checkOut(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Only Investigators, Case Managers, and Admins can check out evidence");
        }
        String custodian = (String) request.get("custodian");
        LocalDateTime date = request.containsKey("date") && request.get("date") != null
                ? LocalDateTime.parse((String) request.get("date")) : LocalDateTime.now();
        String comment = (String) request.get("comment");
        Long userId = request.containsKey("userId") && request.get("userId") != null
                ? Long.parseLong(request.get("userId").toString()) : user.getId();
        return ResponseEntity.ok(evidenceService.checkOut(id, custodian, date, comment, userId));
    }

    @GetMapping("/{id}/chain-of-custody")
    public ResponseEntity<List<ChainOfCustody>> getChainOfCustody(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(evidenceService.getChainOfCustody(id));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<EvidenceHistory>> getEvidenceHistory(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(evidenceService.getEvidenceHistory(id));
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<Map<String, String>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Only Investigators, Case Managers, and Admins can upload evidence photos");
        }
        String url = evidenceService.uploadPhoto(id, file);
        return ResponseEntity.ok(Map.of("photoUrl", url, "message", "Photo uploaded successfully"));
    }

    @PostMapping("/{id}/document")
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Only Investigators, Case Managers, and Admins can upload evidence documents");
        }
        Map<String, Object> result = evidenceService.uploadDocument(id, file, note, user.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<List<Map<String, Object>>> getDocuments(
            @PathVariable Long id,
            HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(evidenceService.getDocuments(id));
    }
}
