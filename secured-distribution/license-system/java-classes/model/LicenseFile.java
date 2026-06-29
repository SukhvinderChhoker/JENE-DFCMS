package com.foreman.license;

import java.util.Map;

/**
 * Top-level license file structure.
 * Contains the license payload and RSA signature.
 */
public class LicenseFile {

    private Map<String, Object> license;
    private String signature;

    public LicenseFile() {}

    public Map<String, Object> getLicense() { return license; }
    public void setLicense(Map<String, Object> license) { this.license = license; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
}
