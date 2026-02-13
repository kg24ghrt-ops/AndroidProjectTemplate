package com.riadul.mvvm.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.riadul.mvvm.databinding.FragmentPackBuilderBinding;
import com.riadul.mvvm.engine.PackBuilder;
import com.riadul.mvvm.ui.viewmodel.MainViewModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PackBuilderFragment extends Fragment {

    private FragmentPackBuilderBinding binding;
    private MainViewModel viewModel;

    // Launcher for the System File Picker
    private final ActivityResultLauncher<Intent> saveFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    exportToUri(result.getData().getData());
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPackBuilderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.btnExport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");
            intent.putExtra(Intent.EXTRA_TITLE, "MyAwesomeAddon.mcaddon");
            saveFileLauncher.launch(intent);
        });
    }

    private void exportToUri(Uri uri) {
        // 1. Create temporary directory and save the manifest.json there
        File tempDir = new File(requireContext().getCacheDir(), "temp_pack");
        if (!tempDir.exists()) tempDir.mkdirs();
        
        File manifestFile = new File(tempDir, "manifest.json");
        try (FileWriter writer = new FileWriter(manifestFile)) {
            writer.write(viewModel.getCurrentJson().getValue());
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to create manifest", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Use our Engine to Zip it
        // Note: For SAF, we'd typically stream directly to the URI's OutputStream.
        // For now, we'll notify the user it's ready for the next level logic.
        Toast.makeText(getContext(), "Add-on ready for export!", Toast.LENGTH_LONG).show();
    }
}
