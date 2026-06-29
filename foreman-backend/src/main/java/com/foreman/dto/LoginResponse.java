package com.foreman.dto;

import java.util.List;

public class LoginResponse {
    private String token;
    private String username;
    private List<String> roles;
    private Long userId;

    public LoginResponse() {}

    public LoginResponse(String token, String username, List<String> roles, Long userId) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
