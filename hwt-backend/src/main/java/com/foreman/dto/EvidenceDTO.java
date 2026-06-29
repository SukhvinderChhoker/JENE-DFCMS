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
    private String originatorUnit;
    private String evidenceBagNumber;
    private String location;
    private String currentStatus;
    private String dateAdded;
    private String photoUrl;
    private String photoFileName;

    private String dateOfInduction;
    private String makeModelNo;
    private String manufacturerName;
    private String serialNumber;
    private Boolean deviceLocked;
    private String depositorName;
    private String depositorContact;
    private String evidenceDescription;
    private String osType;
    private String storageCapacity;
    private String conditionAtReceipt;
    private Boolean sealedStatus;

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
    public String getOriginatorUnit() { return originatorUnit; }
    public void setOriginatorUnit(String originatorUnit) { this.originatorUnit = originatorUnit; }
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
    public String getDateOfInduction() { return dateOfInduction; }
    public void setDateOfInduction(String dateOfInduction) { this.dateOfInduction = dateOfInduction; }
    public String getMakeModelNo() { return makeModelNo; }
    public void setMakeModelNo(String makeModelNo) { this.makeModelNo = makeModelNo; }
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public Boolean getDeviceLocked() { return deviceLocked; }
    public void setDeviceLocked(Boolean deviceLocked) { this.deviceLocked = deviceLocked; }
    public String getDepositorName() { return depositorName; }
    public void setDepositorName(String depositorName) { this.depositorName = depositorName; }
    public String getDepositorContact() { return depositorContact; }
    public void setDepositorContact(String depositorContact) { this.depositorContact = depositorContact; }
    public String getEvidenceDescription() { return evidenceDescription; }
    public void setEvidenceDescription(String evidenceDescription) { this.evidenceDescription = evidenceDescription; }
    public String getOsType() { return osType; }
    public void setOsType(String osType) { this.osType = osType; }
    public String getStorageCapacity() { return storageCapacity; }
    public void setStorageCapacity(String storageCapacity) { this.storageCapacity = storageCapacity; }
    public String getConditionAtReceipt() { return conditionAtReceipt; }
    public void setConditionAtReceipt(String conditionAtReceipt) { this.conditionAtReceipt = conditionAtReceipt; }
    public Boolean getSealedStatus() { return sealedStatus; }
    public void setSealedStatus(Boolean sealedStatus) { this.sealedStatus = sealedStatus; }
}
