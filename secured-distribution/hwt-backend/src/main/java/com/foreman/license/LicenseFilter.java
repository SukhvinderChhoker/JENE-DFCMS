package com.foreman.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP filter that blocks ALL requests if the license is invalid.
 * Runs before Spring Security filters (highest priority).
 * Returns HTTP 503 Service Unavailable with JSON error body.
 */
@Component
@Order(1)
public class LicenseFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LicenseFilter.class);
    private final LicenseValidator licenseValidator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LicenseFilter(LicenseValidator licenseValidator) {
        this.licenseValidator = licenseValidator;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (licenseValidator.isLicenseValid()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, String> error = Map.of(
            "error", "SERVICE_UNAVAILABLE",
            "message", "JENE-DFCMS license is invalid or missing.",
            "details", licenseValidator.getLicenseError(),
            "action", "Contact your system administrator to obtain a valid license."
        );

        String json = objectMapper.writeValueAsString(error);
        httpResponse.getWriter().write(json);
        httpResponse.getWriter().flush();

        log.warn("Request blocked: {}", licenseValidator.getLicenseError());
    }
}
