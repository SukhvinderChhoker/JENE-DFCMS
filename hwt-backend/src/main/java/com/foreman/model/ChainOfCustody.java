package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chain_of_custody")
public class ChainOfCustody {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime dateRecorded;
    private LocalDateTime dateOfCustody;

    private boolean checkIn;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private String custodyReceipt;
    private String custodyReceiptLabel;
    private String custodian;

    public ChainOfCustody() {
        this.dateRecorded = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Evidence getEvidence() { return evidence; }
    public void setEvidence(Evidence evidence) { this.evidence = evidence; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getDateRecorded() { return dateRecorded; }
    public void setDateRecorded(LocalDateTime dateRecorded) { this.dateRecorded = dateRecorded; }
    public LocalDateTime getDateOfCustody() { return dateOfCustody; }
    public void setDateOfCustody(LocalDateTime dateOfCustody) { this.dateOfCustody = dateOfCustody; }
    public boolean isCheckIn() { return checkIn; }
    public void setCheckIn(boolean checkIn) { this.checkIn = checkIn; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getCustodyReceipt() { return custodyReceipt; }
    public void setCustodyReceipt(String custodyReceipt) { this.custodyReceipt = custodyReceipt; }
    public String getCustodyReceiptLabel() { return custodyReceiptLabel; }
    public void setCustodyReceiptLabel(String custodyReceiptLabel) { this.custodyReceiptLabel = custodyReceiptLabel; }
    public String getCustodian() { return custodian; }
    public void setCustodian(String custodian) { this.custodian = custodian; }
}
