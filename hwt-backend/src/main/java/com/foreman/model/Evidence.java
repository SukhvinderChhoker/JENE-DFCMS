package com.foreman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evidence")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evidence_type_id")
    private EvidenceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean qrCode = false;
    private String qrCodeText;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private String originator;
    private String evidenceBagNumber;
    private String location;
    private String currentStatus = EvidenceStatus.INACTIVE;

    private String photoUrl;
    private String photoFileName;

    private LocalDateTime dateAdded;
    private LocalDateTime retentionStartDate;
    private LocalDateTime retentionDate;
    private boolean retentionReminderSent = false;

    @OneToMany(mappedBy = "evidence", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<EvidenceStatus> statuses = new ArrayList<>();

    @OneToMany(mappedBy = "evidence", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<EvidenceHistory> history = new ArrayList<>();

    @OneToMany(mappedBy = "evidence", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<ChainOfCustody> chainOfCustody = new ArrayList<>();

    public Evidence() {
        this.dateAdded = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public EvidenceType getType() { return type; }
    public void setType(EvidenceType type) { this.type = type; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public boolean isQrCode() { return qrCode; }
    public void setQrCode(boolean qrCode) { this.qrCode = qrCode; }
    public String getQrCodeText() { return qrCodeText; }
    public void setQrCodeText(String qrCodeText) { this.qrCodeText = qrCodeText; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getOriginator() { return originator; }
    public void setOriginator(String originator) { this.originator = originator; }
    public String getEvidenceBagNumber() { return evidenceBagNumber; }
    public void setEvidenceBagNumber(String evidenceBagNumber) { this.evidenceBagNumber = evidenceBagNumber; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
    public LocalDateTime getRetentionStartDate() { return retentionStartDate; }
    public void setRetentionStartDate(LocalDateTime retentionStartDate) { this.retentionStartDate = retentionStartDate; }
    public LocalDateTime getRetentionDate() { return retentionDate; }
    public void setRetentionDate(LocalDateTime retentionDate) { this.retentionDate = retentionDate; }
    public boolean isRetentionReminderSent() { return retentionReminderSent; }
    public void setRetentionReminderSent(boolean retentionReminderSent) { this.retentionReminderSent = retentionReminderSent; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getPhotoFileName() { return photoFileName; }
    public void setPhotoFileName(String photoFileName) { this.photoFileName = photoFileName; }
    public List<EvidenceStatus> getStatuses() { return statuses; }
    public void setStatuses(List<EvidenceStatus> statuses) { this.statuses = statuses; }
    public List<EvidenceHistory> getHistory() { return history; }
    public void setHistory(List<EvidenceHistory> history) { this.history = history; }
    public List<ChainOfCustody> getChainOfCustody() { return chainOfCustody; }
    public void setChainOfCustody(List<ChainOfCustody> chainOfCustody) { this.chainOfCustody = chainOfCustody; }
}
