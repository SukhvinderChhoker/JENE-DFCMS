package com.foreman.dto;

import java.util.List;

public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String forename;
    private String surname;
    private String middleName;
    private String email;
    private String telephone;
    private String jobTitle;
    private String photo;
    private boolean active;
    private String team;
    private String department;
    private List<String> roles;

    public UserDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getForename() { return forename; }
    public void setForename(String forename) { this.forename = forename; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
