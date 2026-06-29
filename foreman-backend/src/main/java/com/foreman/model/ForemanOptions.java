package com.foreman.model;

import jakarta.persistence.*;

@Entity
@Table(name = "foreman_options")
public class ForemanOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dateFormat;
    private String defaultLocation;
    private String caseNames;
    private String taskNames;
    private String company;
    private String department;

    private boolean evidenceRetention = false;
    private int evidenceRetentionPeriod = 365;
    private int numberLoginsBeforeAccountLockout = 5;

    public ForemanOptions() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
    public String getDefaultLocation() { return defaultLocation; }
    public void setDefaultLocation(String defaultLocation) { this.defaultLocation = defaultLocation; }
    public String getCaseNames() { return caseNames; }
    public void setCaseNames(String caseNames) { this.caseNames = caseNames; }
    public String getTaskNames() { return taskNames; }
    public void setTaskNames(String taskNames) { this.taskNames = taskNames; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isEvidenceRetention() { return evidenceRetention; }
    public void setEvidenceRetention(boolean evidenceRetention) { this.evidenceRetention = evidenceRetention; }
    public int getEvidenceRetentionPeriod() { return evidenceRetentionPeriod; }
    public void setEvidenceRetentionPeriod(int evidenceRetentionPeriod) { this.evidenceRetentionPeriod = evidenceRetentionPeriod; }
    public int getNumberLoginsBeforeAccountLockout() { return numberLoginsBeforeAccountLockout; }
    public void setNumberLoginsBeforeAccountLockout(int numberLoginsBeforeAccountLockout) { this.numberLoginsBeforeAccountLockout = numberLoginsBeforeAccountLockout; }
}
