package com.codeplatform.code_executor.executor;

/**
 * Legacy compatibility enum and facade delegating to DsaProblemRegistry.
 */
public enum DsaProblemType {
    TWO_SUM(new DsaProblemDef("Two Sum", "twoSum", DsaArchetype.INT_ARRAY_TARGET_TO_INT_ARRAY)),
    REVERSE_STRING(new DsaProblemDef("Reverse a String", "reverseString", DsaArchetype.IN_PLACE_CHAR_ARRAY)),
    VALID_PARENTHESES(new DsaProblemDef("Valid Parentheses", "isValid", DsaArchetype.STRING_TO_BOOL)),
    BINARY_SEARCH(new DsaProblemDef("Binary Search", "search", DsaArchetype.INT_ARRAY_TARGET_TO_INT)),
    MAX_SUBARRAY_SUM(new DsaProblemDef("Maximum Subarray Sum", "maxSubArray", DsaArchetype.INT_ARRAY_TO_INT));

    private final DsaProblemDef definition;

    DsaProblemType(DsaProblemDef definition) {
        this.definition = definition;
    }

    public DsaProblemDef getDefinition() {
        return definition;
    }

    public static boolean isProblemMode(String problemTitle, String code) {
        return DsaProblemRegistry.isProblemMode(problemTitle, code);
    }

    public static DsaProblemDef from(String problemTitle, String code) {
        return DsaProblemRegistry.find(problemTitle, code);
    }
}
