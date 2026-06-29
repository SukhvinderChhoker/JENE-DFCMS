package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String team;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    public Team() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
