package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime dateTime;

    private String taskName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_type_id")
    private TaskType taskType;

    private String background;
    private String location;
    private LocalDateTime deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case caseEntity;

    public TaskHistory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
}
