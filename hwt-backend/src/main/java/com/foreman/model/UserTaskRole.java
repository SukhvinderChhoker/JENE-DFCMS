package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_task_role")
public class UserTaskRole {

    public static final String PRINCIPLE_INVESTIGATOR = "PRINCIPLE_INVESTIGATOR";
    public static final String SECONDARY_INVESTIGATOR = "SECONDARY_INVESTIGATOR";
    public static final String PRINCIPLE_QA = "PRINCIPLE_QA";
    public static final String SECONDARY_QA = "SECONDARY_QA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(nullable = false)
    private String role;

    public UserTaskRole() {}

    public UserTaskRole(User user, Task task, String role) {
        this.user = user;
        this.task = task;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
