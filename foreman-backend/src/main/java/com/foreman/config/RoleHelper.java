package com.foreman.config;

import com.foreman.model.User;
import com.foreman.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleHelper {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        try {
            String username = extractUsernameFromToken(token);
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private String extractUsernameFromToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new RuntimeException("Invalid token");
        String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
        int subStart = payload.indexOf("\"sub\":\"") + 7;
        int subEnd = payload.indexOf("\"", subStart);
        return payload.substring(subStart, subEnd);
    }

    public boolean hasRole(User user, String role) {
        if (user == null) return false;
        return user.getRoles().stream()
                .anyMatch(r -> r.getRole().equalsIgnoreCase(role) && !r.isRemoved());
    }

    public boolean isAdmin(User user) {
        return hasRole(user, "ADMIN");
    }

    public boolean isCaseManager(User user) {
        return hasRole(user, "CASE_MANAGER");
    }

    public boolean isInvestigator(User user) {
        return hasRole(user, "INVESTIGATOR");
    }

    public boolean isQA(User user) {
        return hasRole(user, "QA");
    }

    public boolean isRequester(User user) {
        return hasRole(user, "REQUESTER");
    }

    public boolean isAuthoriser(User user) {
        return hasRole(user, "AUTHORISER");
    }

    public boolean canManageCases(User user) {
        return isAdmin(user) || isCaseManager(user);
    }

    public boolean canCreateCases(User user) {
        return isAdmin(user) || isCaseManager(user) || isRequester(user);
    }

    public boolean canAuthorizeCases(User user) {
        return isAdmin(user) || isAuthoriser(user);
    }

    public boolean canManageTasks(User user) {
        return isAdmin(user) || isCaseManager(user);
    }

    public boolean canWorkOnTasks(User user) {
        return isAdmin(user) || isCaseManager(user) || isInvestigator(user) || isQA(user);
    }

    public boolean canManageEvidence(User user) {
        return isAdmin(user) || isCaseManager(user) || isInvestigator(user);
    }

    public boolean canManageUsers(User user) {
        return isAdmin(user);
    }

    public boolean canViewReports(User user) {
        return user != null;
    }
}
