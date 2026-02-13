package com.riadul.mvvm.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.riadul.mvvm.engine.JsonValidatorEngine;
import com.riadul.mvvm.engine.ManifestGenerator;
import java.util.List;

/**
 * Shared ViewModel for Minecraft Studio.
 * Manages the state between Generator, Validator, and Builder fragments.
 */
public class MainViewModel extends ViewModel {

    // The current JSON the user is working on
    private final MutableLiveData<String> currentJson = new MutableLiveData<>("");
    
    // The list of validation errors found in the current JSON
    private final MutableLiveData<List<JsonValidatorEngine.ValidationError>> validationErrors = new MutableLiveData<>();

    public LiveData<String> getCurrentJson() {
        return currentJson;
    }

    public LiveData<List<JsonValidatorEngine.ValidationError>> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Updates the current JSON and automatically triggers validation.
     */
    public void updateJson(String json) {
        currentJson.setValue(json);
        validateCurrentJson();
    }

    /**
     * Uses the Engine to validate the current work.
     */
    public void validateCurrentJson() {
        String json = currentJson.getValue();
        if (json != null && !json.isEmpty()) {
            List<JsonValidatorEngine.ValidationError> errors = JsonValidatorEngine.validate(json);
            validationErrors.setValue(errors);
        }
    }

    /**
     * Logic for the "Generator Wizard" to push new code to the editor.
     */
    public void generateNewManifest(String name, String desc, int[] version, boolean isRp) {
        ManifestGenerator.ManifestResult result = ManifestGenerator.generate(name, desc, version, isRp);
        updateJson(result.json());
    }
}
