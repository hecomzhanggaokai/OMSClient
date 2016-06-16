package com.hecom.omsclient.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hecom.omsclient.application.OMSClientApplication;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class SharedPreferencesUtils {
    private static SharedPreferences defaultSharedPreferences;

    public static SharedPreferences getDefaultSharedPreferences() {
        if (defaultSharedPreferences == null) {
            defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(OMSClientApplication.getInstance());
        }
        return defaultSharedPreferences;
    }

    public static void set(String key, String values) {
        getDefaultSharedPreferences().edit().putString(key, values).commit();

    }

    public static String get(String key) {
        return getDefaultSharedPreferences().getString(key, "");
    }

}
