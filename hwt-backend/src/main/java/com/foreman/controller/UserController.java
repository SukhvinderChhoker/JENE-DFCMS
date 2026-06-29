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
        User user = requireAuth(request);
        if (!roleHelper.canManageUsers(user)) {
            throw new RuntimeException("Access denied: Only Admins can list all users");
        }
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
        User user = requireAuth(request);
        if (!roleHelper.canManageUsers(user) && !user.getId().equals(id)) {
            throw new RuntimeException("Access denied: You can only view your own profile");
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO, HttpServletRequest request) {
        User user = requireAuth(request);
        if (!roleHelper.canManageUsers(user) && !user.getId().equals(id)) {
            throw new RuntimeException("Access denied: You can only update your own profile");
        }
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
        if (!user.getId().equals(id) && !roleHelper.isAdmin(user)) {
            throw new RuntimeException("Access denied: You can only change your own password (admins can change any)");
        }
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        if (oldPassword == null || oldPassword.isEmpty()) {
            throw new RuntimeException("Old password is required");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("New password must be at least 8 characters");
        }
        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
