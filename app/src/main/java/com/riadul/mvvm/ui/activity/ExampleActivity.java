package com.riadul.mvvm.ui.activity;

// ADD THIS LINE BELOW:
import com.riadul.mvvm.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class ExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
    }
}
