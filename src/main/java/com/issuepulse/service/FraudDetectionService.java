package com.issuepulse.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * FraudDetectionService — Detects suspicious or spammy complaints.
 *
 * Checks:
 *  1. Abusive / profanity keywords
 *  2. Nonsense / gibberish patterns (very short titles, all-caps spam)
 *  3. Repeated punctuation / symbols
 *  4. Empty or useless descriptions
 *
 * Duplicate detection (same title by same user) is handled in ComplaintService
 * using a repository query.
 */
@Service
public class FraudDetectionService {

    // Simple list of keywords that indicate spam/abuse
    private static final List<String> ABUSIVE_KEYWORDS = List.of(
        "spam", "test123", "asdf", "qwerty", "fake", "dummy",
        "abuse", "stupid", "idiot", "nonsense", "junk", "garbage"
    );

    // Regex: string that is mostly repeated characters (e.g., "aaaaaaa", "!!!!!!")
    private static final Pattern REPEATED_CHARS = Pattern.compile("(.)\\1{4,}");

    // Regex: all caps + mostly symbols
    private static final Pattern ALL_CAPS_SHORT = Pattern.compile("^[A-Z\\s!@#$%^&*]{1,10}$");

    /**
     * Analyse a complaint for fraud signals.
     * @return FraudResult with a flag and reason string (null if clean).
     */
    public FraudResult analyse(String title, String description) {
        // Null-safe: fields are now optional
        String safeTitle = (title != null) ? title : "";
        String safeDesc  = (description != null) ? description : "";
        String combined  = (safeTitle + " " + safeDesc).toLowerCase().trim();

        // Skip all checks if both fields are empty — not fraud, just optional
        if (combined.isBlank()) {
            return new FraudResult(false, null);
        }

        // 1. Abusive keyword check
        for (String kw : ABUSIVE_KEYWORDS) {
            if (combined.contains(kw)) {
                return new FraudResult(true, "Contains suspicious keyword: '" + kw + "'");
            }
        }

        // 2. Repeated character pattern
        if (REPEATED_CHARS.matcher(combined).find()) {
            return new FraudResult(true, "Contains repeated characters — possible spam");
        }

        // 3. All-caps short title (only if title is present)
        if (!safeTitle.isBlank() && ALL_CAPS_SHORT.matcher(safeTitle.trim()).matches()) {
            return new FraudResult(true, "Title appears to be spam (all caps / symbols)");
        }

        return new FraudResult(false, null);
    }

    /** Simple result record returned by the fraud analyser. */
    public record FraudResult(boolean flagged, String reason) {}
}
