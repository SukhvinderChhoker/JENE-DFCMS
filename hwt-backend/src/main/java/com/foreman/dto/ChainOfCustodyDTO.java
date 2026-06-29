package com.foreman.dto;

public class ChainOfCustodyDTO {
    private Long id;
    private Long evidenceId;
    private String userFullname;
    private String custodian;
    private String dateRecorded;
    private String dateOfCustody;
    private boolean checkIn;
    private String comment;

    public ChainOfCustodyDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }
    public String getUserFullname() { return userFullname; }
    public void setUserFullname(String userFullname) { this.userFullname = userFullname; }
    public String getCustodian() { return custodian; }
    public void setCustodian(String custodian) { this.custodian = custodian; }
    public String getDateRecorded() { return dateRecorded; }
    public void setDateRecorded(String dateRecorded) { this.dateRecorded = dateRecorded; }
    public String getDateOfCustody() { return dateOfCustody; }
    public void setDateOfCustody(String dateOfCustody) { this.dateOfCustody = dateOfCustody; }
    public boolean isCheckIn() { return checkIn; }
    public void setCheckIn(boolean checkIn) { this.checkIn = checkIn; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
