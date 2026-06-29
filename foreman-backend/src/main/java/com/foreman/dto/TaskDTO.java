package com.foreman.dto;

public class TaskDTO {
    private Long id;
    private String taskName;
    private Long caseId;
    private String caseName;
    private String taskType;
    private String background;
    private String currentStatus;
    private String location;
    private String creationDate;
    private String deadline;
    private String principleInvestigator;
    private String secondaryInvestigator;
    private String principleQA;
    private String secondaryQA;

    public TaskDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public String getCaseName() { return caseName; }
    public void setCaseName(String caseName) { this.caseName = caseName; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getPrincipleInvestigator() { return principleInvestigator; }
    public void setPrincipleInvestigator(String principleInvestigator) { this.principleInvestigator = principleInvestigator; }
    public String getSecondaryInvestigator() { return secondaryInvestigator; }
    public void setSecondaryInvestigator(String secondaryInvestigator) { this.secondaryInvestigator = secondaryInvestigator; }
    public String getPrincipleQA() { return principleQA; }
    public void setPrincipleQA(String principleQA) { this.principleQA = principleQA; }
    public String getSecondaryQA() { return secondaryQA; }
    public void setSecondaryQA(String secondaryQA) { this.secondaryQA = secondaryQA; }
}
