package com.coodev.androidcollection.Utils.system;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

/**
 * @author patrick.ding
 * @since 20/2/17
 */
public class PropertyUtils {

    /**
     * 获取属性值
     *
     * @param propName
     * @return
     */
    public static String getSysProperty(String propName) {
        Class<?> classType = null;
        String buildVersion = "";
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{propName});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    /**
     * 写入属性值
     *
     * @param name
     * @param value
     */
    private static void setSysProperty(String name, String value) {
        Class<?> classType = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method setMethod = classType.getDeclaredMethod("set", new Class<?>[]{String.class, String.class});
            setMethod.invoke(classType, new Object[]{name, value});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adb shell settings get global [key]
     *
     * @param context
     * @param key
     * @return
     */
    public static String getSettingsGlobal(Context context, @NonNull String key) {
        return Settings.Global.getString(context.getContentResolver(), key);
    }

    /**
     * adb shell settings put global [key] [value]
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putSettingsGlobal(Context context, @NonNull String key, @NonNull String value) {
        return Settings.Global.putString(context.getContentResolver(), key, value);
    }

    /**
     * adb shell settings get global [key]
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getSettingsGlobal(Context context, @NonNull String key, int defaultValue) {
        return Settings.Global.getInt(context.getContentResolver(), key, defaultValue);
    }

    /**
     * adb shell settings put global [key] [value]
     *
     * @param context
     * @param key
     * @param value
     */
    public static boolean putSettingsGlobal(Context context, @NonNull String key, int value) {
        return Settings.Global.putInt(context.getContentResolver(), key, value);
    }


}
