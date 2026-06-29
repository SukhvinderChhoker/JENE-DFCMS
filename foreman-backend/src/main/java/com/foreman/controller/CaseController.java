package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.dto.CaseDTO;
import com.foreman.dto.EvidenceDTO;
import com.foreman.dto.TaskDTO;
import com.foreman.model.CaseHistory;
import com.foreman.model.CaseStatus;
import com.foreman.model.Evidence;
import com.foreman.model.Task;
import com.foreman.model.User;
import com.foreman.service.CaseService;
import com.foreman.service.EvidenceService;
import com.foreman.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    @Autowired
    private CaseService caseService;

    @Autowired
    private TaskService taskService;

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
    public ResponseEntity<List<CaseDTO>> getAllCases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageCases(user) && !roleHelper.canCreateCases(user)) {
            if (userId == null) userId = user.getId();
        }
        return ResponseEntity.ok(caseService.getAllCases(status, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseDTO> getCaseById(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @PostMapping
    public ResponseEntity<CaseDTO> createCase(@RequestBody CaseDTO caseDTO, @RequestParam Long userId, HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canCreateCases(user)) {
            throw new RuntimeException("Access denied: Cannot create cases");
        }
        return ResponseEntity.ok(caseService.createCase(caseDTO, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseDTO> updateCase(@PathVariable Long id, @RequestBody CaseDTO caseDTO, @RequestParam Long userId, HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageCases(user)) {
            throw new RuntimeException("Access denied: Cannot update cases");
        }
        return ResponseEntity.ok(caseService.updateCase(id, caseDTO, userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CaseDTO> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        String reason = request.get("reason");
        Long userId = Long.parseLong(request.get("userId"));
        return ResponseEntity.ok(caseService.changeStatus(id, status, reason, userId));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<CaseDTO> closeCase(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        Long userId = Long.parseLong(request.get("userId"));
        return ResponseEntity.ok(caseService.closeCase(id, reason, userId));
    }

    @PutMapping("/{id}/authorize")
    public ResponseEntity<CaseDTO> authorizeCase(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canAuthorizeCases(user)) {
            throw new RuntimeException("Access denied: Only Authorisers and Admins can authorize cases");
        }
        boolean authorize = (boolean) request.get("authorize");
        String reason = (String) request.get("reason");
        Long userId = Long.parseLong(request.get("userId").toString());
        return ResponseEntity.ok(caseService.authorizeCase(id, authorize, reason, userId));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<CaseHistory>> getCaseHistory(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(caseService.getCaseHistory(id));
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> getCaseTasks(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(caseService.getCaseTasks(id));
    }

    @GetMapping("/{id}/evidence")
    public ResponseEntity<List<Evidence>> getCaseEvidence(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(caseService.getCaseEvidence(id));
    }

    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskDTO> createTaskForCase(
            @PathVariable Long id,
            @RequestBody TaskDTO taskDTO,
            @RequestParam(defaultValue = "1") Long userId,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageTasks(user)) {
            throw new RuntimeException("Access denied: Cannot create tasks");
        }
        return ResponseEntity.ok(taskService.createTask(id, taskDTO, userId));
    }

    @PostMapping("/{id}/evidence")
    public ResponseEntity<EvidenceDTO> createEvidenceForCase(
            @PathVariable Long id,
            @RequestBody EvidenceDTO evidenceDTO,
            @RequestParam(defaultValue = "1") Long userId,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageEvidence(user)) {
            throw new RuntimeException("Access denied: Cannot create evidence");
        }
        return ResponseEntity.ok(evidenceService.createEvidence(id, evidenceDTO, userId));
    }

    @PostMapping("/{id}/link")
    public ResponseEntity<Map<String, String>> linkCases(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canManageCases(user)) {
            throw new RuntimeException("Access denied: Cannot link cases");
        }
        Long linkedCaseId = Long.parseLong(request.get("linkedCaseId").toString());
        String reason = (String) request.get("reason");
        Long userId = Long.parseLong(request.get("userId").toString());
        caseService.linkCases(id, linkedCaseId, reason, userId);
        return ResponseEntity.ok(Map.of("message", "Cases linked successfully"));
    }

    @DeleteMapping("/{id}/link/{linkedId}")
    public ResponseEntity<Map<String, String>> unlinkCases(
            @PathVariable Long id,
            @PathVariable Long linkedId,
            @RequestParam Long userId,
            @RequestParam(required = false) String reason,
            HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageCases(user)) {
            throw new RuntimeException("Access denied: Cannot unlink cases");
        }
        caseService.unlinkCases(id, linkedId, reason, userId);
        return ResponseEntity.ok(Map.of("message", "Cases unlinked successfully"));
    }
}
