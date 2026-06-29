package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task_type")
public class TaskType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String taskType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private TaskCategory category;

    public TaskType() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public TaskCategory getCategory() { return category; }
    public void setCategory(TaskCategory category) { this.category = category; }
}
