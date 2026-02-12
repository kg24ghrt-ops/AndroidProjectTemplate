package com.riadul.mvvm.utils;

import com.riadul.mvvm.data.api.ApiClient;
import com.riadul.mvvm.data.api.ApiService;

public class Constants {

    public static final String BASE_URL = "https://www.example.com/";


    public static ApiService getApiService(){
        return ApiClient.getClient(BASE_URL).create(ApiService.class);
    }

}
