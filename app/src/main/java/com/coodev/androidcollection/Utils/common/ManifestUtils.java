package com.coodev.androidcollection.Utils.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author patrick.ding
 * @since 20/2/17
 */
public class ManifestUtils {

    /**
     * 获取AndroidManifest.xml中的meta-data数据
     *
     * @param context
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getMetaData(Context context, String key) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return (T) applicationInfo.metaData.get("key");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取当前应用版本号
     *
     * @param context
     * @return
     */
    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }


    /**
     * checkPermissions
     *
     * @param context
     * @param permission
     * @return true or false
     */
    public static boolean checkPermissions(Context context, String permission) {
        if (context == null || permission == null || permission.equals("")) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        return pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

}
