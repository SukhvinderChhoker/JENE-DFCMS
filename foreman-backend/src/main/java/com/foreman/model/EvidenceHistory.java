package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_history")
public class EvidenceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case caseEntity;

    private LocalDateTime dateTime;

    private String reference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evidence_type_id")
    private EvidenceType type;

    private boolean qrCode;
    private String qrCodeText;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private String originator;
    private String evidenceBagNumber;
    private String location;

    public EvidenceHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Evidence getEvidence() { return evidence; }
    public void setEvidence(Evidence evidence) { this.evidence = evidence; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public EvidenceType getType() { return type; }
    public void setType(EvidenceType type) { this.type = type; }
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
}
