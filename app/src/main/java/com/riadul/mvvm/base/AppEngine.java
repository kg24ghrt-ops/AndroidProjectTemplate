package com.riadul.mvvm.base;

/**
 * Created by Team Webxzen.
 * Disclaimer: IMPORTANT:  This software is supplied to you by Webxzen Labs
 * in consideration of your agreement and terms of your use,
 * installation of this software constitutes your acceptance
 * of the terms and conditions of the agreement. You are not
 * permitted for any modification or redistribution of this
 * software without prior written consent.
 * Copyright (C) 2018 Webxzen Labs. All Rights Reserved.
 */

public class AppEngine {
    public AppStorage appStorage;

    // Singleton Stuff
    private static AppEngine ourInstance;

    public static AppEngine getInstance() {

        if (ourInstance == null) {
            synchronized (AppEngine.class) {
                if (ourInstance == null) {
                    ourInstance = new AppEngine();
                }
            }
        }
        return ourInstance;
    }

    private AppEngine() {
        appStorage = new AppStorage();
    }
}
