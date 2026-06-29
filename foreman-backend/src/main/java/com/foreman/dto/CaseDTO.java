package com.foreman.dto;

import java.util.Map;

public class CaseDTO {
    private Long id;
    private String caseName;
    private String reference;
    private String currentStatus;
    private boolean privateCase;
    private String background;
    private String location;
    private String creationDate;
    private String classification;
    private String caseType;
    private String justification;
    private String casePriority;
    private String casePriorityColour;
    private String deadline;
    private int taskCount;
    private int evidenceCount;
    private String principleCaseManager;
    private String secondaryCaseManager;
    private String requester;
    private Map<String, Boolean> documentChecklist;

    public CaseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCaseName() { return caseName; }
    public void setCaseName(String caseName) { this.caseName = caseName; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public boolean isPrivateCase() { return privateCase; }
    public void setPrivateCase(boolean privateCase) { this.privateCase = privateCase; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    public String getCaseType() { return caseType; }
    public void setCaseType(String caseType) { this.caseType = caseType; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public String getCasePriority() { return casePriority; }
    public void setCasePriority(String casePriority) { this.casePriority = casePriority; }
    public String getCasePriorityColour() { return casePriorityColour; }
    public void setCasePriorityColour(String casePriorityColour) { this.casePriorityColour = casePriorityColour; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public int getTaskCount() { return taskCount; }
    public void setTaskCount(int taskCount) { this.taskCount = taskCount; }
    public int getEvidenceCount() { return evidenceCount; }
    public void setEvidenceCount(int evidenceCount) { this.evidenceCount = evidenceCount; }
    public String getPrincipleCaseManager() { return principleCaseManager; }
    public void setPrincipleCaseManager(String principleCaseManager) { this.principleCaseManager = principleCaseManager; }
    public String getSecondaryCaseManager() { return secondaryCaseManager; }
    public void setSecondaryCaseManager(String secondaryCaseManager) { this.secondaryCaseManager = secondaryCaseManager; }
    public String getRequester() { return requester; }
    public void setRequester(String requester) { this.requester = requester; }
    public Map<String, Boolean> getDocumentChecklist() { return documentChecklist; }
    public void setDocumentChecklist(Map<String, Boolean> documentChecklist) { this.documentChecklist = documentChecklist; }
}
