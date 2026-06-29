package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_status")
public class EvidenceStatus {

    public static final String INACTIVE = "INACTIVE";
    public static final String ACTIVE = "ACTIVE";
    public static final String ARCHIVED = "ARCHIVED";
    public static final String DESTROYED = "DESTROYED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    private Evidence evidence;

    private LocalDateTime dateTime;

    @Column(nullable = false)
    private String status;

    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public EvidenceStatus() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Evidence getEvidence() { return evidence; }
    public void setEvidence(Evidence evidence) { this.evidence = evidence; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
