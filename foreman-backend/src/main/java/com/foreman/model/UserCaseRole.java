package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_case_role")
public class UserCaseRole {

    public static final String PRINCIPLE_CASE_MANAGER = "PRINCIPLE_CASE_MANAGER";
    public static final String SECONDARY_CASE_MANAGER = "SECONDARY_CASE_MANAGER";
    public static final String REQUESTER = "REQUESTER";
    public static final String AUTHORISER = "AUTHORISER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Column(nullable = false)
    private String role;

    public UserCaseRole() {}

    public UserCaseRole(User user, Case caseEntity, String role) {
        this.user = user;
        this.caseEntity = caseEntity;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
