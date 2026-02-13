package com.riadul.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.riadul.mvvm.databinding.FragmentValidatorBinding;
import com.riadul.mvvm.ui.adapter.ErrorAdapter;
import com.riadul.mvvm.ui.viewmodel.MainViewModel;

public class ValidatorFragment extends Fragment {

    private FragmentValidatorBinding binding;
    private MainViewModel viewModel;
    private ErrorAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentValidatorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Setup RecyclerView
        adapter = new ErrorAdapter();
        binding.recyclerErrors.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerErrors.setAdapter(adapter);

        // Observe JSON changes (from Generator)
        viewModel.getCurrentJson().observe(getViewLifecycleOwner(), json -> {
            if (!json.equals(binding.editJsonPreview.getText().toString())) {
                binding.editJsonPreview.setText(json);
            }
        });

        // Observe Error changes
        viewModel.getValidationErrors().observe(getViewLifecycleOwner(), errors -> {
            adapter.setErrors(errors);
            binding.textErrorCount.setText(errors.size() + " Issues Found");
        });

        // Manual edits trigger re-validation
        binding.editJsonPreview.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            public void afterTextChanged(android.text.Editable s) {
                viewModel.updateJson(s.toString());
            }
        });
    }
}
