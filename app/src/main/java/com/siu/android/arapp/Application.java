package com.siu.android.arapp;

import android.content.Context;

/**
 * Created by lukas on 7/2/13.
 */
public class Application extends android.app.Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }
}
