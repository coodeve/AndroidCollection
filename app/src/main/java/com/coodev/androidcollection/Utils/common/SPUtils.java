package com.coodev.androidcollection.Utils.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    private static SharedPreferences SHARED_PREFERENCES = null;

    private static final String FACTORY_TEST = "factory_test";

    public static void setValue(Context context, String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value);
        edit.apply();
    }

    public static int getValue(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getInt(key, defaultValue);
    }


    public static void setValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public static String getValue(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static void setValue(Context context, String key, long value) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong(key, value);
        edit.apply();
    }

    public static long getValue(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getLong(key, defaultValue);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        if (SHARED_PREFERENCES == null) {
            SHARED_PREFERENCES = context.getSharedPreferences(FACTORY_TEST, Context.MODE_PRIVATE);
        }
        return SHARED_PREFERENCES;
    }

    public static void clear(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        edit.apply();
    }
}
