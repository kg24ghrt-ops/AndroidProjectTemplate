package com.riadul.mvvm.engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Advanced JSON Validator using Java 23 Pattern Matching.
 * Detects syntax errors and Minecraft Bedrock specific schema violations.
 */
public class JsonValidatorEngine {

    public record ValidationError(String message, ErrorSeverity severity) {}
    public enum ErrorSeverity { ERROR, WARNING }

    public static List<ValidationError> validate(String jsonContent) {
        List<ValidationError> errors = new ArrayList<>();

        // 1. Syntax Check (Basic JSON validity)
        JsonElement root;
        try {
            root = JsonParser.parseString(jsonContent);
        } catch (JsonSyntaxException e) {
            errors.add(new ValidationError("Invalid JSON syntax: " + e.getMessage(), ErrorSeverity.ERROR));
            return errors; 
        }

        // 2. Structural Validation using Java 23 Pattern Matching
        if (root instanceof JsonObject obj) {
            validateMinecraftStructure(obj, errors);
        } else {
            errors.add(new ValidationError("Root must be a JSON Object (curly braces {})", ErrorSeverity.ERROR));
        }

        return errors;
    }

    private static void validateMinecraftStructure(JsonObject obj, List<ValidationError> errors) {
        // Validate format_version
        if (!obj.has("format_version")) {
            errors.add(new ValidationError("Missing 'format_version' field.", ErrorSeverity.ERROR));
        }

        // Validate Identifiers (namespace:name)
        // Using modern switch patterns to check specific components
        obj.entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonElement val = entry.getValue();

            if (key.equals("header") || key.equals("description")) {
                validateIdentifier(val, errors);
            }
        });
    }

    private static void validateIdentifier(JsonElement element, List<ValidationError> errors) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("identifier")) {
                String id = obj.get("identifier").getAsString();
                // Minecraft requirement: namespace:name
                if (!id.contains(":") || id.startsWith(":") || id.endsWith(":")) {
                    errors.add(new ValidationError(
                        "Invalid identifier '%s'. Use 'namespace:name' format.".formatted(id), 
                        ErrorSeverity.ERROR
                    ));
                }
            }
        }
    }
}
