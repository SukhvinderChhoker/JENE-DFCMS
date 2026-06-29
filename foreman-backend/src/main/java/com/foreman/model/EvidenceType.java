package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evidence_type")
public class EvidenceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String evidenceType;

    public EvidenceType() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
}
