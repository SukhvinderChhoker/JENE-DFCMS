package com.foreman.dto;

public class EvidenceDTO {
    private Long id;
    private String reference;
    private String type;
    private Long caseId;
    private String caseName;
    private boolean qrCode;
    private String qrCodeText;
    private String comment;
    private String originator;
    private String evidenceBagNumber;
    private String location;
    private String currentStatus;
    private String dateAdded;
    private String photoUrl;
    private String photoFileName;

    public EvidenceDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
    public String getCaseName() { return caseName; }
    public void setCaseName(String caseName) { this.caseName = caseName; }
    public boolean isQrCode() { return qrCode; }
    public void setQrCode(boolean qrCode) { this.qrCode = qrCode; }
    public String getQrCodeText() { return qrCodeText; }
    public void setQrCodeText(String qrCodeText) { this.qrCodeText = qrCodeText; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getOriginator() { return originator; }
    public void setOriginator(String originator) { this.originator = originator; }
    public String getEvidenceBagNumber() { return evidenceBagNumber; }
    public void setEvidenceBagNumber(String evidenceBagNumber) { this.evidenceBagNumber = evidenceBagNumber; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getPhotoFileName() { return photoFileName; }
    public void setPhotoFileName(String photoFileName) { this.photoFileName = photoFileName; }
}
