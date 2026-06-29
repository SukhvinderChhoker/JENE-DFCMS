package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "case_type")
public class CaseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String caseType;

    public CaseType() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
}
