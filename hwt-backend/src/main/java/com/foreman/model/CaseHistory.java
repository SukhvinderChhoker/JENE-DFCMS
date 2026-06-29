package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_history")
public class CaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime dateTime;

    private String caseName;
    private String reference;
    private String background;
    private boolean privateCase;
    private String location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classification_id")
    private CaseClassification classification;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "case_type_id")
    private CaseType caseType;

    private String justification;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "case_priority_id")
    private CasePriority casePriority;

    private LocalDateTime deadline;

    public CaseHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getCaseName() { return caseName; }
    public void setCaseName(String caseName) { this.caseName = caseName; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public boolean isPrivateCase() { return privateCase; }
    public void setPrivateCase(boolean privateCase) { this.privateCase = privateCase; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public CaseClassification getClassification() { return classification; }
    public void setClassification(CaseClassification classification) { this.classification = classification; }
    public CaseType getCaseType() { return caseType; }
    public void setCaseType(CaseType caseType) { this.caseType = caseType; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public CasePriority getCasePriority() { return casePriority; }
    public void setCasePriority(CasePriority casePriority) { this.casePriority = casePriority; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
}
