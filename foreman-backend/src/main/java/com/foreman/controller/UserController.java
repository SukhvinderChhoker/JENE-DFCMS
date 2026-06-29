package com.foreman.controller;

import com.foreman.config.RoleHelper;
import com.foreman.dto.UserDTO;
import com.foreman.model.User;
import com.foreman.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleHelper roleHelper;

    private User requireAuth(HttpServletRequest request) {
        User user = roleHelper.getCurrentUser(request);
        if (user == null) throw new RuntimeException("Unauthorized");
        return user;
    }

    private void requireAdmin(HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.isAdmin(user)) {
            throw new RuntimeException("Access denied: Only Admins can manage users");
        }
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) String role, HttpServletRequest request) {
        requireAuth(request);
        if (role != null && !role.isEmpty()) {
            return ResponseEntity.ok(userService.getUsersByRole(role));
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        requireAdmin(request);
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO, HttpServletRequest request) {
        requireAdmin(request);
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role, HttpServletRequest request) {
        requireAuth(request);
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable Long id, @RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        User user = requireAuth(httpRequest);
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        if (newPassword == null) newPassword = request.get("password");
        if (oldPassword == null) oldPassword = "admin123";
        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
