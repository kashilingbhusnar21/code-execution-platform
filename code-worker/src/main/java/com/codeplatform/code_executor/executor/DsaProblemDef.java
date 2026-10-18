package com.codeplatform.code_executor.executor;

/**
 * Metadata definition for a judgeable DSA problem.
 */
public class DsaProblemDef {

    private final String canonicalTitle;
    private final String methodName;
    private final DsaArchetype archetype;
    private final boolean returnsLength;

    public DsaProblemDef(String canonicalTitle, String methodName, DsaArchetype archetype) {
        this(canonicalTitle, methodName, archetype, false);
    }

    public DsaProblemDef(String canonicalTitle, String methodName, DsaArchetype archetype, boolean returnsLength) {
        this.canonicalTitle = canonicalTitle;
        this.methodName = methodName;
        this.archetype = archetype;
        this.returnsLength = returnsLength;
    }

    public String getCanonicalTitle() {
        return canonicalTitle;
    }

    public String getMethodName() {
        return methodName;
    }

    public DsaArchetype getArchetype() {
        return archetype;
    }

    public boolean isReturnsLength() {
        return returnsLength;
    }

    @Override
    public String toString() {
        return "DsaProblemDef{" +
                "title='" + canonicalTitle + '\'' +
                ", method='" + methodName + '\'' +
                ", archetype=" + archetype +
                '}';
    }
}
