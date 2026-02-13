package com.riadul.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.riadul.mvvm.databinding.FragmentGeneratorBinding;
import com.riadul.mvvm.ui.viewmodel.MainViewModel;

public class GeneratorFragment extends Fragment {

    private FragmentGeneratorBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGeneratorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Use the activity scope so the ViewModel is shared with other fragments
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.btnGenerate.setOnClickListener(v -> {
            String name = binding.editName.getText().toString();
            String desc = binding.editDesc.getText().toString();
            boolean isRp = binding.switchPackType.isChecked();

            if (name.isEmpty() || desc.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call our engine through the ViewModel
            viewModel.generateNewManifest(name, desc, new int[]{1, 0, 0}, isRp);
            
            Toast.makeText(getContext(), "Manifest Generated!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
