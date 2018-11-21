/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.transfer;

import java.io.Serializable;
import java.util.regex.Pattern;


public class SepaStringSanitizer implements Serializable {

    /**
     * Required for:
     * Debitor Name <Dbtr><Nm>
     * Ultimate Debtor Name <UltmtDbtr><Nm>
     * Creditor Name <Cdtr><Nm>
     * Ultimate Creditor Name <UltmtCdtr><Nm>
     */
    private static final String ALLOWED_CHARS_REGEXP = "[^a-zA-Z0-9 " + ("&*$%/?:().,'+-".replaceAll("(.)", "\\\\$1")) + "]";
//    private static final String ALLOWED_CHARS_REGEXP = "[^a-zA-Z0-9ÄäÖöÜüß " + ("&*$%/?:().,'+-".replaceAll("(.)", "\\\\$1")) + "]";

    private static final Pattern ALLOWED_CHARS_PATTERN = Pattern.compile(ALLOWED_CHARS_REGEXP);

    private static final long serialVersionUID = 1L;

    private final String sepaString;

    private Integer maxLength = null;

    private SepaStringSanitizer(String sepaString) {
        this.sepaString = sepaString;
    }

    public static SepaStringSanitizer of(String sepaString) {
        return new SepaStringSanitizer(sepaString);
    }

    public SepaStringSanitizer withMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    public String sanitze() {
        String result = ALLOWED_CHARS_PATTERN.matcher(sepaString).replaceAll(" ");
        if (maxLength != null) {
            result = result.substring(0, Math.min(result.length(), maxLength));
        }
        return result;
    }

}
