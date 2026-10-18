package com.codeplatform.code_executor.executor;

public class OutputNormalizer {

    /**
     * Compares actual output with expected output using a normalization policy:
     * - Trims leading/trailing whitespace and normalizes line endings (\r\n -> \n)
     * - Matches case-insensitively for booleans ("true", "false")
     * - Strips formatting whitespace around array brackets, braces, commas, and colons
     * - Standardizes character/string quotes (' -> ")
     * - Performs trimmed line-by-line fallback comparison
     */
    public static boolean matches(String actual, String expected) {
        if (actual == null && expected == null) return true;
        if (actual == null || expected == null) return false;

        // Support multiple alternative expected outputs delimited by '|'
        if (expected.contains("|")) {
            String[] alternatives = expected.split("\\|");
            for (String alt : alternatives) {
                if (matchesSingle(actual, alt.trim())) {
                    return true;
                }
            }
            return false;
        }

        return matchesSingle(actual, expected);
    }

    private static boolean matchesSingle(String actual, String expected) {
        String a = actual.trim().replace("\r\n", "\n").replace("\r", "\n");
        String e = expected.trim().replace("\r\n", "\n").replace("\r", "\n");

        if (a.equals(e)) return true;

        // Case-insensitive boolean match
        if (("true".equalsIgnoreCase(e) || "false".equalsIgnoreCase(e))
                && a.equalsIgnoreCase(e)) {
            return true;
        }

        // Array and collection normalization: strip whitespace around brackets, commas, quotes
        String aNorm = normalizeWhitespaceAndQuotes(a);
        String eNorm = normalizeWhitespaceAndQuotes(e);
        if (aNorm.equals(eNorm)) return true;

        // Unordered 2-element integer array match (e.g. [3,4] vs [4,3] for Two Sum "any order")
        if (matchesUnorderedIntPair(aNorm, eNorm)) return true;

        // Fallback line-by-line trimmed comparison
        String[] aLines = a.split("\n");
        String[] eLines = e.split("\n");
        if (aLines.length == eLines.length) {
            boolean allMatch = true;
            for (int i = 0; i < aLines.length; i++) {
                if (!normalizeWhitespaceAndQuotes(aLines[i]).equals(normalizeWhitespaceAndQuotes(eLines[i]))) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) return true;
        }

        return false;
    }

    private static boolean matchesUnorderedIntPair(String a, String e) {
        if (!a.startsWith("[") || !a.endsWith("]") || !e.startsWith("[") || !e.endsWith("]")) {
            return false;
        }
        String aContent = a.substring(1, a.length() - 1).trim();
        String eContent = e.substring(1, e.length() - 1).trim();
        String[] aParts = aContent.split(",");
        String[] eParts = eContent.split(",");
        if (aParts.length != 2 || eParts.length != 2) {
            return false;
        }
        try {
            int a1 = Integer.parseInt(aParts[0].trim());
            int a2 = Integer.parseInt(aParts[1].trim());
            int e1 = Integer.parseInt(eParts[0].trim());
            int e2 = Integer.parseInt(eParts[1].trim());
            return (a1 == e1 && a2 == e2) || (a1 == e2 && a2 == e1);
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static String normalizeWhitespaceAndQuotes(String str) {
        if (str == null) return "";
        // Replace single quotes with double quotes for characters/strings
        String s = str.replace('\'', '"');
        // Remove spaces after comma or colon
        s = s.replaceAll(",\\s+", ",");
        s = s.replaceAll(":\\s+", ":");
        // Remove spaces inside brackets
        s = s.replaceAll("\\[\\s+", "[");
        s = s.replaceAll("\\s+\\]", "]");
        s = s.replaceAll("\\{\\s+", "{");
        s = s.replaceAll("\\s+\\}", "}");
        return s.trim();
    }
}
