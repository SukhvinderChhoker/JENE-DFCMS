package com.foreman.util;

import org.springframework.stereotype.Component;

@Component
public class InputSanitizeUtil {

    public String sanitize(String input) {
        if (input == null) return null;
        input = input.replaceAll("<script[^>]*>.*?</script>", "");
        input = input.replaceAll("<script[^>]*/?>", "");
        input = input.replaceAll("javascript:", "");
        input = input.replaceAll("on\\w+\\s*=", "");
        input = input.replaceAll("data:", "");
        input = input.replaceAll("vbscript:", "");
        input = input.replaceAll("<iframe[^>]*>.*?</iframe>", "");
        input = input.replaceAll("<iframe[^>]*/?>", "");
        input = input.replaceAll("<object[^>]*>.*?</object>", "");
        input = input.replaceAll("<embed[^>]*/?>", "");
        input = input.replaceAll("<form[^>]*>.*?</form>", "");
        input = input.replaceAll("<svg[^>]*>.*?</svg>", "");
        input = input.replaceAll("expression\\s*\\(", "");
        input = input.replaceAll("eval\\s*\\(", "");
        input = input.replaceAll("alert\\s*\\(", "");
        input = input.replaceAll("confirm\\s*\\(", "");
        input = input.replaceAll("prompt\\s*\\(", "");
        return input.trim();
    }

    public String sanitizeHtml(String input) {
        if (input == null) return null;
        input = input.replaceAll("&", "&amp;");
        input = input.replaceAll("<", "&lt;");
        input = input.replaceAll(">", "&gt;");
        input = input.replaceAll("\"", "&quot;");
        input = input.replaceAll("'", "&#x27;");
        return input.trim();
    }
}
