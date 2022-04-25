package com.nurnobishanto.infoalert;

import android.app.Application;

import com.onesignal.OneSignal;

public class MainApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "1e459b5a-8eaf-4adf-a9bb-d177b762d8ea";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}