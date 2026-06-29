package com.foreman.license;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * SpringApplicationRunListener that performs license validation
 * at the earliest point in the Spring Boot lifecycle.
 * This runs BEFORE any beans are created, ensuring the app
 * never starts with an invalid license.
 */
public class LicenseRunListener implements SpringApplicationRunListener {

    private static final Logger log = LoggerFactory.getLogger(LicenseRunListener.class);

    public LicenseRunListener(SpringApplication application, String[] args) {
    }

    @Override
    public void environmentPrepared(ConfigurableApplicationContext context) {
        ConfigurableEnvironment env = context.getEnvironment();
        boolean required = Boolean.parseBoolean(env.getProperty("license.required", "true"));
        if (!required) {
            log.warn("License validation DISABLED via configuration");
            return;
        }
        log.info("License validation is ENABLED - checking license before startup...");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        LicenseValidator validator = null;
        try {
            validator = context.getBean(LicenseValidator.class);
        } catch (Exception e) {
            throw new RuntimeException("LICENSE SYSTEM FAILURE: Cannot initialize license validator. Application cannot start.", e);
        }
        if (!validator.isLicenseValid()) {
            log.error("========================================");
            log.error("  APPLICATION BLOCKED BY LICENSE SYSTEM");
            log.error("========================================");
            log.error("Reason: {}", validator.getLicenseError());
            log.error("Fix: Generate a valid license file");
            log.error("  python generate_license.py --customer 'Name' --type trial");
            log.error("========================================");
            throw new RuntimeException("LICENSE INVALID: " + validator.getLicenseError());
        }
    }

    @Override
    public void ready(ConfigurableApplicationContext context) {
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
    }
}
