package com.picovr.androidcollection.Utils.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PackageUtil {
    public static final String TAG = PackageUtil.class.getSimpleName();

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


    public static String[] getTopActivity(Context context) {
        String[] topArgs = new String[2];
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(10);
        ComponentName componentName;
        if (runningTasks != null && runningTasks.size() > 0) {
            for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
                componentName = runningTaskInfo.topActivity;
                if (context.getPackageName().equals(componentName.getPackageName())) {
                    continue;
                }
                topArgs[0] = componentName.getClassName();
                topArgs[1] = componentName.getPackageName();
                Log.i(TAG, "getTopActivity# " + topArgs[0] + "," + topArgs[1]);
                break;
            }
        }

        return topArgs;
    }


    public static boolean isHome(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        Log.e(TAG, "getPackageName=" + rti.get(0).topActivity.getPackageName());
        return getHomes(context).contains(rti.get(0).topActivity.getPackageName());
    }

    private static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
            Log.e(TAG, "name=" + ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断给定包名的应用是否已经安装
     *
     * @param packageName
     * @return 0未安装，1已经安装
     */
    public static int isInstall(Context context, String packageName) {

        PackageInfo packageInfo = null;
        try {
            packageInfo = context
                    .getPackageManager()
                    .getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo != null) {//已经安装
            return 1;
        }


        return 0;
    }


    /**
     * 判断给定包名的应用是否已经安装
     *
     * @param packageName
     * @return -1未安装，0已经安装且没有更新，1安装且有更新
     */
    public static int isInstall(Context context, String packageName, int versionCode) {

        PackageInfo packageInfo = null;
        try {
            packageInfo = context
                    .getPackageManager()
                    .getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        if (packageInfo != null) {//已经安装
            if (packageInfo.versionCode >= versionCode) {
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }


    public static int isInstall(Context context, String packageName, String versionCode) {
        return isInstall(context, packageName, Integer.valueOf(versionCode));
    }
}
