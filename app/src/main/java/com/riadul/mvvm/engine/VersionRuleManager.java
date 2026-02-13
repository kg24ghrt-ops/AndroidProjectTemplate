package com.riadul.mvvm.engine;

import java.util.List;

/**
 * Manages Minecraft Bedrock version constraints.
 * Uses Java 23 Switch Expressions for clean logic.
 */
public class VersionRuleManager {

    public record VersionSpecs(int[] formatVersion, String minEngineVersion, boolean supportsMolang) {}

    public static VersionSpecs getSpecsForVersion(String versionLabel) {
        // Java 23 Switch Expression: Clean, readable, and exhaustive
        return switch (versionLabel) {
            case "1.20.0" -> new VersionSpecs(new int[]{1, 20, 0}, "1.20.0", true);
            case "1.21.0" -> new VersionSpecs(new int[]{1, 21, 0}, "1.21.0", true);
            default -> new VersionSpecs(new int[]{1, 20, 0}, "1.20.0", true);
        };
    }

    public static List<String> getSupportedVersions() {
        return List.of("1.20.0", "1.21.0", "1.21.30");
    }
}
