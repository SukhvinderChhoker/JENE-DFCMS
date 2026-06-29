package com.foreman.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_upload")
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "deleter_id")
    private User deleter;

    private LocalDateTime dateTime;

    private String fileNote;
    private String fileHash;
    private String fileName;
    private String uploadLocation;
    private String fileTitle;

    private boolean deleted = false;
    private LocalDateTime dateDeleted;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private Case caseEntity;

    private Long taskId;
    private Long evidenceId;

    public FileUpload() {
        this.dateTime = LocalDateTime.now();
    }

    public enum FileType {
        CASE, TASK, EVIDENCE
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUploader() { return uploader; }
    public void setUploader(User uploader) { this.uploader = uploader; }
    public User getDeleter() { return deleter; }
    public void setDeleter(User deleter) { this.deleter = deleter; }
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
    public String getFileNote() { return fileNote; }
    public void setFileNote(String fileNote) { this.fileNote = fileNote; }
    public String getFileHash() { return fileHash; }
    public void setFileHash(String fileHash) { this.fileHash = fileHash; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getUploadLocation() { return uploadLocation; }
    public void setUploadLocation(String uploadLocation) { this.uploadLocation = uploadLocation; }
    public String getFileTitle() { return fileTitle; }
    public void setFileTitle(String fileTitle) { this.fileTitle = fileTitle; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
    public LocalDateTime getDateDeleted() { return dateDeleted; }
    public void setDateDeleted(LocalDateTime dateDeleted) { this.dateDeleted = dateDeleted; }
    public FileType getFileType() { return fileType; }
    public void setFileType(FileType fileType) { this.fileType = fileType; }
    public Case getCaseEntity() { return caseEntity; }
    public void setCaseEntity(Case caseEntity) { this.caseEntity = caseEntity; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }
}
