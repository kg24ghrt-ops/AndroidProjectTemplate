package com.riadul.mvvm.engine;

import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generates valid Minecraft Bedrock manifest.json files.
 * Optimized with Java 23 features like Records and Text Blocks.
 */
public class ManifestGenerator {

    // Record is perfect for returning multiple immutable values (Java 16+)
    public record ManifestResult(String json, String headerUuid, String moduleUuid) {}

    /**
     * Generates a manifest based on pack type.
     * @param name Pack name
     * @param description Pack description
     * @param version Array of 3 ints e.g., {1, 0, 0}
     * @param isResourcePack true for RP, false for BP (Behavior Pack)
     */
    public static ManifestResult generate(String name, String description, int[] version, boolean isResourcePack) {
        final String uuidHeader = UUID.randomUUID().toString();
        final String uuidModule = UUID.randomUUID().toString();
        
        // Use Java 23 Pattern Matching for switch (simplified logic for pack type)
        String moduleType = switch (isResourcePack ? "resources" : "data") {
            case "resources" -> "resources";
            case "data" -> "data";
            default -> "javascript"; // Placeholder for future Scripting API support
        };

        // Java 15+ Text Blocks make JSON templates readable without messy escape characters
        String jsonTemplate = """
        {
            "format_version": 2,
            "header": {
                "name": "%s",
                "description": "%s",
                "uuid": "%s",
                "version": [%d, %d, %d],
                "min_engine_version": [1, 20, 0]
            },
            "modules": [
                {
                    "description": "%s",
                    "type": "%s",
                    "uuid": "%s",
                    "version": [%d, %d, %d]
                }
            ]
        }
        """.formatted(
            name, description, uuidHeader, 
            version[0], version[1], version[2],
            description, moduleType, uuidModule,
            version[0], version[1], version[2]
        );

        // We use GSON here to ensure the string is pretty-printed and valid
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Object jsonObject = gson.fromJson(jsonTemplate, Object.class);
        String prettyJson = gson.toJson(jsonObject);

        return new ManifestResult(prettyJson, uuidHeader, uuidModule);
    }
}
