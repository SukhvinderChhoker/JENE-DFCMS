package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "case_classification")
public class CaseClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String classification;

    public CaseClassification() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
}
