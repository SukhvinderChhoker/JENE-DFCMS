package com.foreman.license;

import java.util.List;

/**
 * License data model. Contains all fields stored in the license file.
 */
public class LicenseData {

    private String licenseId;
    private String appName;
    private String version;
    private String customerName;
    private String licenseType;
    private String issueDate;
    private String expiryDate;
    private String hardwareId;
    private int maxUsers;
    private List<String> features;

    public LicenseData() {}

    public String getLicenseId() { return licenseId; }
    public void setLicenseId(String licenseId) { this.licenseId = licenseId; }

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getHardwareId() { return hardwareId; }
    public void setHardwareId(String hardwareId) { this.hardwareId = hardwareId; }

    public int getMaxUsers() { return maxUsers; }
    public void setMaxUsers(int maxUsers) { this.maxUsers = maxUsers; }

    public List<String> getFeatures() { return features; }
    public void setFeatures(List<String> features) { this.features = features; }
}
