package com.vlada.selfie_app;

import android.app.Application;

import com.facebook.soloader.SoLoader;

public class MyApplication extends Application {
    
    /**
     * Called when the application is starting, before any other application objects have been created.
     * Overriding this method is totally optional!
     */
    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);
    }
}
