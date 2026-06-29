package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "case_priority")
public class CasePriority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String casePriority;

    private String colour;

    private boolean isDefault = false;

    public CasePriority() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCasePriority() { return casePriority; }
    public void setCasePriority(String casePriority) { this.casePriority = casePriority; }
    public String getColour() { return colour; }
    public void setColour(String colour) { this.colour = colour; }
    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
