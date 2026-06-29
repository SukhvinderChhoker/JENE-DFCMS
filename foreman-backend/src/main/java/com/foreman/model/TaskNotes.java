package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_notes")
public class TaskNotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private User author;

    private LocalDateTime dateTime;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String hash;

    public TaskNotes() {
        this.dateTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
}
