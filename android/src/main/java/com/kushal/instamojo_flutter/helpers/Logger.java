package com.kushal.instamojo_flutter.helpers;

import android.util.Log;

import com.kushal.instamojo_flutter.BuildConfig;

public class Logger {

    /**
     * Logs debug messages if DEBUG mode
     */
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    /**
     * Logs all the errors
     */
    public static void e(String tag, String message) {
        Log.e(tag, message);
    }
}
