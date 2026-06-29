package com.foreman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cases")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String caseName;

    @Column(unique = true)
    private String reference;

    private String currentStatus = CaseStatus.CREATED;

    private boolean privateCase = false;

    @Column(columnDefinition = "TEXT")
    private String background;

    private String location;

    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classification_id")
    private CaseClassification classification;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "case_type_id")
    private CaseType caseType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "case_priority_id")
    private CasePriority casePriority;

    private String casePriorityColour;

    private LocalDateTime deadline;

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Evidence> evidence = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<CaseStatus> statuses = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserCaseRole> caseRoles = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FileUpload> uploads = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<CaseHistory> history = new ArrayList<>();

    private boolean cocReceived = false;
    private boolean agencyLetterReceived = false;
    private boolean authorityLetterReceived = false;
    private boolean consentFormReceived = false;
    private boolean caseHistoryReceived = false;
    private boolean handlingTakingFormReceived = false;
    private boolean evidencePhotosReceived = false;
    private boolean seizureMemoReceived = false;
    private boolean witnessStatementReceived = false;
    private boolean otherDocumentsReceived = false;

    public Case() {
        this.creationDate = LocalDateTime.now();
    }

    public void setStatus(String status, User user, String reason) {
        this.currentStatus = status;
        CaseStatus caseStatus = new CaseStatus();
        caseStatus.setCaseEntity(this);
        caseStatus.setStatus(status);
        caseStatus.setUser(user);
        caseStatus.setDateTime(LocalDateTime.now());
        if (reason != null) {
            caseStatus.setReason(reason);
        }
        this.statuses.add(caseStatus);
    }

    public String getStatus() {
        return this.currentStatus;
    }

    public void addChange(User user) {
        CaseHistory hist = new CaseHistory();
        hist.setCaseEntity(this);
        hist.setUser(user);
        hist.setDateTime(LocalDateTime.now());
        hist.setCaseName(this.caseName);
        hist.setReference(this.reference);
        hist.setBackground(this.background);
        hist.setPrivateCase(this.privateCase);
        hist.setLocation(this.location);
        hist.setClassification(this.classification);
        hist.setCaseType(this.caseType);
        hist.setJustification(this.justification);
        hist.setCasePriority(this.casePriority);
        hist.setDeadline(this.deadline);
        this.history.add(hist);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCaseName() { return caseName; }
    public void setCaseName(String caseName) { this.caseName = caseName; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public boolean isPrivateCase() { return privateCase; }
    public void setPrivateCase(boolean privateCase) { this.privateCase = privateCase; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public CaseClassification getClassification() { return classification; }
    public void setClassification(CaseClassification classification) { this.classification = classification; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public CaseType getCaseType() { return caseType; }
    public void setCaseType(CaseType caseType) { this.caseType = caseType; }
    public CasePriority getCasePriority() { return casePriority; }
    public void setCasePriority(CasePriority casePriority) { this.casePriority = casePriority; }
    public String getCasePriorityColour() { return casePriorityColour; }
    public void setCasePriorityColour(String casePriorityColour) { this.casePriorityColour = casePriorityColour; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public List<Task> getTasks() { return tasks; }
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
    public List<Evidence> getEvidence() { return evidence; }
    public void setEvidence(List<Evidence> evidence) { this.evidence = evidence; }
    public List<CaseStatus> getStatuses() { return statuses; }
    public void setStatuses(List<CaseStatus> statuses) { this.statuses = statuses; }
    public List<UserCaseRole> getCaseRoles() { return caseRoles; }
    public void setCaseRoles(List<UserCaseRole> caseRoles) { this.caseRoles = caseRoles; }
    public List<FileUpload> getUploads() { return uploads; }
    public void setUploads(List<FileUpload> uploads) { this.uploads = uploads; }
    public List<CaseHistory> getHistory() { return history; }
    public void setHistory(List<CaseHistory> history) { this.history = history; }

    public boolean isCocReceived() { return cocReceived; }
    public void setCocReceived(boolean cocReceived) { this.cocReceived = cocReceived; }
    public boolean isAgencyLetterReceived() { return agencyLetterReceived; }
    public void setAgencyLetterReceived(boolean agencyLetterReceived) { this.agencyLetterReceived = agencyLetterReceived; }
    public boolean isAuthorityLetterReceived() { return authorityLetterReceived; }
    public void setAuthorityLetterReceived(boolean authorityLetterReceived) { this.authorityLetterReceived = authorityLetterReceived; }
    public boolean isConsentFormReceived() { return consentFormReceived; }
    public void setConsentFormReceived(boolean consentFormReceived) { this.consentFormReceived = consentFormReceived; }
    public boolean isCaseHistoryReceived() { return caseHistoryReceived; }
    public void setCaseHistoryReceived(boolean caseHistoryReceived) { this.caseHistoryReceived = caseHistoryReceived; }
    public boolean isHandlingTakingFormReceived() { return handlingTakingFormReceived; }
    public void setHandlingTakingFormReceived(boolean handlingTakingFormReceived) { this.handlingTakingFormReceived = handlingTakingFormReceived; }
    public boolean isEvidencePhotosReceived() { return evidencePhotosReceived; }
    public void setEvidencePhotosReceived(boolean evidencePhotosReceived) { this.evidencePhotosReceived = evidencePhotosReceived; }
    public boolean isSeizureMemoReceived() { return seizureMemoReceived; }
    public void setSeizureMemoReceived(boolean seizureMemoReceived) { this.seizureMemoReceived = seizureMemoReceived; }
    public boolean isWitnessStatementReceived() { return witnessStatementReceived; }
    public void setWitnessStatementReceived(boolean witnessStatementReceived) { this.witnessStatementReceived = witnessStatementReceived; }
    public boolean isOtherDocumentsReceived() { return otherDocumentsReceived; }
    public void setOtherDocumentsReceived(boolean otherDocumentsReceived) { this.otherDocumentsReceived = otherDocumentsReceived; }
}
