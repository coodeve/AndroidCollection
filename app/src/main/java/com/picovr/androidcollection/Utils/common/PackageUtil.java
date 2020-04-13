package com.picovr.androidcollection.Utils.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

public class PackageUtil {

    /**
     * 获取应用名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.applicationInfo.loadLabel(packageManager).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取外部apk的资源
     *
     * @param context
     * @param path
     * @return
     */
    public static Resources getExtrResources(Context context, String path) {
        AssetManager am = createAssetManager(path);
        return new Resources(am, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
    }


    /**
     * 创建assestmanager，并添加资源（apk或者jar）到目录中
     *
     * @param apkPath
     * @return
     */
    public static AssetManager createAssetManager(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            AssetManager.class.getDeclaredMethod("addAssetPath", String.class).invoke(
                    assetManager, apkPath);
            return assetManager;
        } catch (Throwable th) {
            th.printStackTrace();
        }
        return null;
    }
}
