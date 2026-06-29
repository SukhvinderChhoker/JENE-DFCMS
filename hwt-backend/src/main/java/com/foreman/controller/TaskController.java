package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.dto.TaskDTO;
import com.foreman.model.TaskHistory;
import com.foreman.model.TaskNotes;
import com.foreman.model.User;
import com.foreman.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RoleHelper roleHelper;

    private User requireAuth(HttpServletRequest request) {
        User user = roleHelper.getCurrentUser(request);
        if (user == null) throw new RuntimeException("Unauthorized");
        return user;
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<TaskDTO>> getTasksByCase(@PathVariable Long caseId, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(taskService.getTasksByCase(caseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping("/case/{caseId}")
    public ResponseEntity<TaskDTO> createTask(@PathVariable Long caseId, @RequestBody TaskDTO taskDTO, HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageTasks(user)) {
            throw new RuntimeException("Access denied: Only Case Managers and Admins can create tasks");
        }
        return ResponseEntity.ok(taskService.createTask(caseId, taskDTO, user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id, @RequestBody TaskDTO taskDTO, HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageTasks(user)) {
            throw new RuntimeException("Access denied: Only Case Managers and Admins can update tasks");
        }
        return ResponseEntity.ok(taskService.updateTask(id, taskDTO, user.getId()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDTO> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canWorkOnTasks(user)) {
            throw new RuntimeException("Access denied: Insufficient permissions to change task status");
        }
        String status = request.get("status");
        String note = request.get("note");
        return ResponseEntity.ok(taskService.changeStatus(id, status, note, user.getId()));
    }

    @PutMapping("/{id}/assign-investigator")
    public ResponseEntity<Map<String, String>> assignInvestigator(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canManageTasks(user)) {
            throw new RuntimeException("Access denied: Only Case Managers and Admins can assign investigators");
        }
        Long userId = Long.parseLong(request.get("userId").toString());
        boolean principle = (boolean) request.get("principle");
        taskService.assignInvestigator(id, userId, principle, user.getId());
        return ResponseEntity.ok(Map.of("message", "Investigator assigned successfully"));
    }

    @PutMapping("/{id}/assign-qa")
    public ResponseEntity<Map<String, String>> assignQA(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canManageTasks(user)) {
            throw new RuntimeException("Access denied: Only Case Managers and Admins can assign QA");
        }
        Long userId = Long.parseLong(request.get("userId").toString());
        boolean principle = (boolean) request.get("principle");
        taskService.assignQA(id, userId, principle, user.getId());
        return ResponseEntity.ok(Map.of("message", "QA assigned successfully"));
    }

    @GetMapping("/{id}/notes")
    public ResponseEntity<List<TaskNotes>> getTaskNotes(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(taskService.getTaskNotes(id));
    }

    @PostMapping("/{id}/notes")
    public ResponseEntity<TaskNotes> addNote(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        if (!roleHelper.canWorkOnTasks(user)) {
            throw new RuntimeException("Access denied: Insufficient permissions to add task notes");
        }
        String note = request.get("note");
        return ResponseEntity.ok(taskService.addNote(id, note, user.getId()));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> getTaskHistory(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(taskService.getTaskHistory(id));
    }
}
