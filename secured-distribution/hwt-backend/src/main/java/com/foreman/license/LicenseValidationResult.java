package com.foreman.license;

/**
 * Result of license validation.
 */
public class LicenseValidationResult {

    private final boolean valid;
    private final String message;

    private LicenseValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static LicenseValidationResult valid(String message) {
        return new LicenseValidationResult(true, message);
    }

    public static LicenseValidationResult invalid(String message) {
        return new LicenseValidationResult(false, message);
    }

    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
}
