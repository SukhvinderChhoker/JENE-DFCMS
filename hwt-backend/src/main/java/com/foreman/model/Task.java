package com.foreman.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String taskName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_type_id")
    private TaskType taskType;

    @Column(columnDefinition = "TEXT")
    private String background;

    private String currentStatus = TaskStatus.CREATED;

    private String location;

    private LocalDateTime creationDate;

    private LocalDateTime deadline;

    private boolean princQA = false;
    private boolean seconQA = false;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<TaskStatus> statuses = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<TaskNotes> notes = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserTaskRole> taskRoles = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private List<TaskHistory> history = new ArrayList<>();

    public Task() {
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public boolean isPrincQA() { return princQA; }
    public void setPrincQA(boolean princQA) { this.princQA = princQA; }
    public boolean isSeconQA() { return seconQA; }
    public void setSeconQA(boolean seconQA) { this.seconQA = seconQA; }
    public List<TaskStatus> getStatuses() { return statuses; }
    public void setStatuses(List<TaskStatus> statuses) { this.statuses = statuses; }
    public List<TaskNotes> getNotes() { return notes; }
    public void setNotes(List<TaskNotes> notes) { this.notes = notes; }
    public List<UserTaskRole> getTaskRoles() { return taskRoles; }
    public void setTaskRoles(List<UserTaskRole> taskRoles) { this.taskRoles = taskRoles; }
    public List<TaskHistory> getHistory() { return history; }
    public void setHistory(List<TaskHistory> history) { this.history = history; }
}
