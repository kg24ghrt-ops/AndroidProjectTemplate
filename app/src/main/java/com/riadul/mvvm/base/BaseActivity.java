package com.riadul.mvvm.base;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.riadul.mvvm.data.api.ApiService;
import com.riadul.mvvm.utils.Constants;

import io.paperdb.Paper;


public class BaseActivity extends AppCompatActivity {

    public BaseApplication mInstance;
    public AppStorage appStorage;
    public ProgressDialog prsDlg;
    public ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = BaseApplication.getInstance();
        AppEngine appEngine = AppEngine.getInstance();
        appStorage = appEngine.appStorage;

        //initialize paperDB
        Paper.init(this);

        //define api interface
        apiService = Constants.getApiService();

        prsDlg = new ProgressDialog(this);

    }

    //show progress dialog
    public void showProgressDialog() {
        prsDlg.setMessage("Please wait...");
        prsDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prsDlg.setIndeterminate(true);
        prsDlg.setCancelable(false);
        prsDlg.show();
    }

    //dismiss progress dialog
    public void dismissProgressDialog() {
        if (prsDlg != null) {
            if (prsDlg.isShowing()) {
                prsDlg.dismiss();
            }
        }

    }

}
