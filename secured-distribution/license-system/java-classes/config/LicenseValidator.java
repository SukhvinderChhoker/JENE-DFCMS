package com.foreman.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

/**
 * Core license validation engine.
 * Verifies RSA signature, expiry, app name, and tampering.
 * Runs at application startup; blocks boot if invalid.
 */
@Component
public class LicenseValidator {

    private static final Logger log = LoggerFactory.getLogger(LicenseValidator.class);
    private static final String PUBLIC_KEY_RESOURCE = "/license/public_key.pem";
    private static final String LICENSE_FILE_PROPERTY = "license.file";

    @Value("${license.file:license/jene-dfcms.license}")
    private String licenseFilePath;

    @Value("${license.required:true}")
    private boolean licenseRequired;

    private PublicKey publicKey;
    private LicenseData validLicense;
    private boolean licenseValid = false;
    private String licenseError = "";

    @PostConstruct
    public void validateLicense() {
        log.info("========================================");
        log.info("  JENE-DFCMS License Validation System");
        log.info("========================================");

        if (!licenseRequired) {
            log.warn("LICENSE CHECK BYPASSED (license.required=false). NOT FOR PRODUCTION.");
            licenseValid = true;
            return;
        }

        try {
            publicKey = loadPublicKey();
            log.info("Public key loaded successfully");
        } catch (Exception e) {
            licenseError = "FATAL: Cannot load public key - " + e.getMessage();
            log.error(licenseError);
            throw new RuntimeException(licenseError);
        }

        LicenseFile licenseFile = loadLicenseFile();
        if (licenseFile == null) {
            licenseError = "FATAL: No valid license file found at: " + licenseFilePath;
            log.error(licenseError);
            log.error("Generate a license with: python generate_license.py --customer 'Name' --type trial");
            throw new RuntimeException(licenseError);
        }

        LicenseValidationResult result = validate(licenseFile);
        if (!result.isValid()) {
            licenseError = result.getMessage();
            log.error("LICENSE VALIDATION FAILED: {}", licenseError);
            throw new RuntimeException("LICENSE INVALID: " + licenseError);
        }

        this.validLicense = mapToLicenseData(licenseFile.getLicense());
        this.licenseValid = true;
        log.info("License VALIDATED successfully");
        log.info("  License ID:    {}", validLicense.getLicenseId());
        log.info("  Customer:      {}", validLicense.getCustomerName());
        log.info("  Type:          {}", validLicense.getLicenseType());
        log.info("  Expires:       {}", validLicense.getExpiryDate());
        log.info("  Max Users:     {}", validLicense.getMaxUsers());
        log.info("========================================");
    }

    private PublicKey loadPublicKey() throws Exception {
        InputStream is = getClass().getResourceAsStream(PUBLIC_KEY_RESOURCE);
        if (is == null) {
            throw new RuntimeException("Public key not found in classpath: " + PUBLIC_KEY_RESOURCE);
        }
        String keyPem;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("-----")) {
                    sb.append(line);
                }
            }
            keyPem = sb.toString();
        }
        byte[] keyBytes = Base64.getDecoder().decode(keyPem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private LicenseFile loadLicenseFile() {
        Path path = Paths.get(licenseFilePath);
        if (!Files.exists(path)) {
            log.error("License file not found: {}", path.toAbsolutePath());
            return null;
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(bytes, LicenseFile.class);
        } catch (Exception e) {
            log.error("Failed to parse license file: {}", e.getMessage());
            return null;
        }
    }

    public LicenseValidationResult validate(LicenseFile licenseFile) {
        try {
            Map<String, Object> licenseData = licenseFile.getLicense();
            String signature = licenseFile.getSignature();

            if (licenseData == null || signature == null || signature.isEmpty()) {
                return LicenseValidationResult.invalid("License file is missing data or signature");
            }

            String expectedAppName = (String) licenseData.get("appName");
            if (!"JENE-DFCMS".equals(expectedAppName)) {
                return LicenseValidationResult.invalid("License is for wrong application: " + expectedAppName);
            }

            String expiryDateStr = (String) licenseData.get("expiryDate");
            if (expiryDateStr == null) {
                return LicenseValidationResult.invalid("License has no expiry date");
            }
            LocalDateTime expiryDate = LocalDateTime.parse(expiryDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            if (LocalDateTime.now().isAfter(expiryDate)) {
                return LicenseValidationResult.invalid("License has EXPIRED on " + expiryDateStr);
            }

            String issueDateStr = (String) licenseData.get("issueDate");
            if (issueDateStr != null) {
                LocalDateTime issueDate = LocalDateTime.parse(issueDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                if (LocalDateTime.now().isBefore(issueDate)) {
                    return LicenseValidationResult.invalid("License is not yet valid (starts " + issueDateStr + ")");
                }
            }

            String payloadJson = canonicalJson(licenseData);
            byte[] payloadBytes = payloadJson.getBytes("UTF-8");
            byte[] signatureBytes = Base64.getDecoder().decode(signature);

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(payloadBytes);
            if (!sig.verify(signatureBytes)) {
                return LicenseValidationResult.invalid("License signature verification FAILED - file may be tampered");
            }

            return LicenseValidationResult.valid("License is valid");

        } catch (java.security.SignatureException e) {
            return LicenseValidationResult.invalid("Signature verification error: " + e.getMessage());
        } catch (Exception e) {
            return LicenseValidationResult.invalid("Validation error: " + e.getMessage());
        }
    }

    private String canonicalJson(Map<String, Object> map) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(map);
    }

    private LicenseData mapToLicenseData(Map<String, Object> map) {
        LicenseData data = new LicenseData();
        data.setLicenseId((String) map.get("licenseId"));
        data.setAppName((String) map.get("appName"));
        data.setVersion((String) map.get("version"));
        data.setCustomerName((String) map.get("customerName"));
        data.setLicenseType((String) map.get("licenseType"));
        data.setIssueDate((String) map.get("issueDate"));
        data.setExpiryDate((String) map.get("expiryDate"));
        data.setHardwareId((String) map.get("hardwareId"));
        Object maxUsers = map.get("maxUsers");
        data.setMaxUsers(maxUsers != null ? ((Number) maxUsers).intValue() : 0);
        @SuppressWarnings("unchecked")
        java.util.List<String> features = (java.util.List<String>) map.get("features");
        data.setFeatures(features);
        return data;
    }

    public boolean isLicenseValid() { return licenseValid; }
    public LicenseData getValidLicense() { return validLicense; }
    public String getLicenseError() { return licenseError; }
}
