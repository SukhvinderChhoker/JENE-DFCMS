package com.foreman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String role;

    private boolean removed = false;

    public Role() {}

    public Role(User user, String role) {
        this.user = user;
        this.role = role;
        this.removed = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isRemoved() { return removed; }
    public void setRemoved(boolean removed) { this.removed = removed; }
}
