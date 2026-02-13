package com.riadul.mvvm.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.riadul.mvvm.R;
import com.riadul.mvvm.engine.JsonValidatorEngine.ValidationError;
import java.util.ArrayList;
import java.util.List;

public class ErrorAdapter extends RecyclerView.Adapter<ErrorAdapter.ErrorViewHolder> {

    private List<ValidationError> errors = new ArrayList<>();

    public void setErrors(List<ValidationError> newErrors) {
        this.errors = newErrors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ErrorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_validation_error, parent, false);
        return new ErrorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ErrorViewHolder holder, int position) {
        ValidationError error = errors.get(position);
        holder.message.setText(error.message());
        // Simple logic to change icon color if it's just a WARNING
    }

    @Override
    public int getItemCount() {
        return errors.size();
    }

    static class ErrorViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ErrorViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.text_error_message);
        }
    }
}
