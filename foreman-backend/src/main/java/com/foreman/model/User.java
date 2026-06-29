package com.foreman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private String forename;
    private String surname;
    private String middleName;

    @Column(unique = true)
    private String email;

    private String telephone;
    private String altTelephone;
    private String fax;
    private String jobTitle;
    private String photo;

    private boolean active = true;
    private boolean lockedOut = false;
    private int numberLoginFails = 0;
    private boolean validated = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @JsonIgnore
    private User manager;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Role> roles = new ArrayList<>();

    public User() {}

    public User(String username, String password, String forename, String surname, String email) {
        this.username = username;
        this.password = password;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.active = true;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (forename != null && !forename.isEmpty()) sb.append(forename);
        if (middleName != null && !middleName.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(middleName);
        }
        if (surname != null && !surname.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(surname);
        }
        return sb.toString();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String rawPassword) {
        return this.password.equals(rawPassword);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
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
    public String getAltTelephone() { return altTelephone; }
    public void setAltTelephone(String altTelephone) { this.altTelephone = altTelephone; }
    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isLockedOut() { return lockedOut; }
    public void setLockedOut(boolean lockedOut) { this.lockedOut = lockedOut; }
    public int getNumberLoginFails() { return numberLoginFails; }
    public void setNumberLoginFails(int numberLoginFails) { this.numberLoginFails = numberLoginFails; }
    public boolean isValidated() { return validated; }
    public void setValidated(boolean validated) { this.validated = validated; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
}
