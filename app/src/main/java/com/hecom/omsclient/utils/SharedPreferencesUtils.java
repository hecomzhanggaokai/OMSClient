package com.hecom.omsclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hecom.omsclient.application.OMSClientApplication;

/**
 * Created by zhanggaokai on 16/6/16.
 */
public class SharedPreferencesUtils {
    private static SharedPreferences defaultSharedPreferences;
    private static SharedPreferences webSharePreferences;

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

    //专门用来存储来自jsapi的数据
    public static SharedPreferences getWebSharedPreferences() {
        if (webSharePreferences == null) {
            webSharePreferences = OMSClientApplication.getInstance().getSharedPreferences("webstorage", Context.MODE_PRIVATE);
        }
        return webSharePreferences;
    }

    public static void setByJs(String key, String values) {
        getWebSharedPreferences().edit().putString(key, values).commit();

    }

    public static String getByJs(String key) {
        return getWebSharedPreferences().getString(key, "");
    }

    public static void removeByJs(String key) {
        getWebSharedPreferences().edit().remove(key).commit();
    }


    public static void clearJs() {
        getWebSharedPreferences().edit().clear().commit();
    }
}
