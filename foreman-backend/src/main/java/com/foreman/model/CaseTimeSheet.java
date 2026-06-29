package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "case_timesheet")
public class CaseTimeSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    private LocalDate date;

    private float hours;

    public CaseTimeSheet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public float getHours() { return hours; }
    public void setHours(float hours) { this.hours = hours; }
}
