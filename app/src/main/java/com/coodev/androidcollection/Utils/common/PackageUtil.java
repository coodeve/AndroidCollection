package com.coodev.androidcollection.Utils.common;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.coodev.androidcollection.App;
import com.coodev.androidcollection.Utils.security.MD5Util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PackageUtil {
    public static final String TAG = PackageUtil.class.getSimpleName();

    public static Context getPackageContext(Context context, String packageName) {
        Context packageNameContext = null;
        try {
            packageNameContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return packageNameContext;
    }

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
    public static Resources getExtResources(Context context, String path) {
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

    /**
     * 获取权限信息
     *
     * @return
     */
    public PermissionInfo[] getPermissionString(Context context, String packageName) {
        if (packageName == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                return packageInfo.permissions;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static class AppInfo {
        public String appSize;
        public String cacheSize;
        public String dataSize;
        public String totalSize;

    }

    public AppInfo getAppSize(Context context, String packageName) {
        AppInfo appInfo = new AppInfo();
        StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        try {
            StorageStats storageStats = storageStatsManager.queryStatsForPackage(StorageManager.UUID_DEFAULT, packageName, Process.myUserHandle());
            appInfo.appSize = Formatter.formatFileSize(context, storageStats.getAppBytes());
            appInfo.cacheSize = Formatter.formatFileSize(context, storageStats.getCacheBytes());
            appInfo.dataSize = Formatter.formatFileSize(context, storageStats.getDataBytes());
            appInfo.totalSize = Formatter.formatFileSize(context, storageStats.getAppBytes() + storageStats.getCacheBytes() + storageStats.getDataBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    /**
     * 是否是系统app
     *
     * @param context
     * @return
     */
    public static boolean isSystemApp(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        if (packageName != null) {
            try {
                PackageInfo info = pm.getPackageInfo(packageName, 0);
                return (info != null) && (info.applicationInfo != null) &&
                        ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取app版本号
     *
     * @param context
     * @param packageName
     * @return
     */
    public int getAppVersionCode(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 更换icon
     *
     * @param launcherComponentClassName 启动组件
     * @param aliasComponentClassName    别名组件
     */
    public static void switchIconTask(String launcherComponentClassName, String aliasComponentClassName) {
        Log.i(TAG, "switchIconTask: " + launcherComponentClassName + "," + aliasComponentClassName);
        final PackageManager packageManager = App.getContext().getPackageManager();
        final ComponentName disableComponent = new ComponentName(App.getContext().getPackageName(), launcherComponentClassName);
        final ComponentName enableComponent = new ComponentName(App.getContext().getPackageName(), aliasComponentClassName);
        if (packageManager.getComponentEnabledSetting(disableComponent) == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                packageManager.getComponentEnabledSetting(enableComponent) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            return;
        }
        packageManager.setComponentEnabledSetting(disableComponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(enableComponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * 获取已安装应用的签名字符串的md5值
     *
     * @param packageName
     */
    public static String[] getSignKeyMD5(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return null;
        }

        try {
            PackageManager packageManager = context.getPackageManager();
            final PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo == null ? null : containSign(packageInfo.signatures);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String[] containSign(Signature[] signatures) {
        int length = signatures.length;
        String[] signArray = new String[length];
        for (int i = 0; i < length; i++) {
            Signature signature = signatures[i];
            signArray[i] = MD5Util.md5(String.valueOf(signature.toByteArray()));
        }
        return signArray;
    }

    /**
     * app是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstalled(Context context, @NonNull String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * app是否启用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isEnabled(Context context, @NonNull String packageName) {
        final int result = context.getPackageManager().getApplicationEnabledSetting(packageName);
        return result == 0 || result == 1;
    }

    /**
     * activity是否启用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isEnabled(Context context, @NonNull String packageName, @NonNull String activityName) {
        final ComponentName componentName = new ComponentName(packageName, activityName);
        final int result = context.getPackageManager().getComponentEnabledSetting(componentName);
        return result == 0 || result == 1;
    }

    /**
     * app是否可用(是否安装并启用)
     * {@link AppUtils#isInstalled(Context, String)}
     * {@link AppUtils#isEnabled(Context, String)}
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isUsable(Context context, @NonNull String packageName) {
        return isInstalled(context, packageName) && isEnabled(context, packageName);
    }

    /**
     * 通过包名获取PackageInfo
     *
     * @param context
     * @param packageName
     * @param flags
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, @NonNull String packageName, int flags) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取app版本名称
     *
     * @return
     */
    public static String getVersionName(Context context) {
        final PackageInfo packageInfo = getPackageInfo(context, context.getPackageName(), 0);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "1.0";
    }

    /**
     * 获取app版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        final PackageInfo packageInfo = getPackageInfo(context, context.getPackageName(), 0);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }


    /**
     * 通过ComponentName启动app
     *
     * @param context
     * @param packageName
     * @param activityName
     */
    public static void launchComponentName(Context context, @NonNull String packageName, @NonNull String activityName) {
        launchIntent(context, componentNameToIntent(packageName, activityName));
    }

    /**
     * 通过ComponentName往指定display上启动app
     *
     * @param context
     * @param packageName
     * @param activityName
     * @param displayId
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void launchComponentName(Context context, @NonNull String packageName, @NonNull String activityName, int displayId) {
        launchIntent(context, componentNameToIntent(packageName, activityName), displayId);
    }

    /**
     * 通过PackageName启动app
     *
     * @param context
     * @param packageName
     */
    public static void launchPackageName(Context context, @NonNull String packageName) {
        launchIntent(context, packageNameToIntent(packageName));
    }

    /**
     * 通过PackageName往指定display上启动app
     *
     * @param context
     * @param packageName
     * @param displayId
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void launchPackageName(Context context, @NonNull String packageName, int displayId) {
        launchIntent(context, packageNameToIntent(packageName), displayId);
    }

    /**
     * 通过Action启动app
     *
     * @param context
     * @param action
     */
    public static void launchAction(Context context, @NonNull String action) {
        launchIntent(context, actionToIntent(action));
    }

    /**
     * 通过Action往指定display上启动app
     *
     * @param context
     * @param action
     * @param displayId
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void launchAction(Context context, @NonNull String action, int displayId) {
        launchIntent(context, actionToIntent(action), displayId);
    }

    /**
     * 通过Intent启动app
     *
     * @param context
     * @param intent
     */
    public static void launchIntent(Context context, @NonNull Intent intent) {
        final List<ResolveInfo> resolveInfoList = getResolveInfoList(context, intent, 0);
        if (resolveInfoList.size() == 0) {
            Log.e(TAG, "Don't find matched app!");
            return;
        }
        final String pkg = resolveInfoList.get(0).activityInfo.packageName;
        final String cls = resolveInfoList.get(0).activityInfo.name;
        Log.i(TAG, "launchIntent{" + pkg + "/" + cls + "}");
        intent.setClassName(pkg, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /**
     * 通过Intent往指定display上启动app
     *
     * @param context
     * @param intent
     * @param displayId
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void launchIntent(Context context, @NonNull Intent intent, int displayId) {
        final List<ResolveInfo> resolveInfoList = getResolveInfoList(context, intent, 0);
        if (resolveInfoList.size() == 0) {
            Log.e(TAG, "Don't find matched app!");
            return;
        }
        final String pkg = resolveInfoList.get(0).activityInfo.packageName;
        final String cls = resolveInfoList.get(0).activityInfo.name;
        Log.i(TAG, "launchIntent{" + pkg + "/" + cls + "/" + displayId + "}");
        intent.setClassName(pkg, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        final ActivityOptions activityOptions = ActivityOptions.makeBasic();
        activityOptions.setLaunchDisplayId(displayId);
        context.startActivity(intent, activityOptions.toBundle());
    }

    /**
     * PackageName转Intent
     *
     * @param packageName
     * @return
     */
    public static Intent packageNameToIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(packageName);
        return intent;
    }

    /**
     * ComponentName转Intent
     *
     * @param packageName
     * @param className
     * @return
     */
    public static Intent componentNameToIntent(String packageName, String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        return intent;
    }

    /**
     * Action转Intent
     *
     * @param action
     * @return
     */
    public static Intent actionToIntent(String action) {
        Intent intent = new Intent(action);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

    /**
     * 根据intent获取的ResolveInfo
     *
     * @param context
     * @param intent
     * @param flags
     * @return
     */
    public static List<ResolveInfo> getResolveInfoList(Context context, @NonNull Intent intent, int flags) {
        return context.getPackageManager().queryIntentActivities(intent, flags);
    }

    /**
     * 获取meta-data中的数值
     *
     * @param context
     * @param packageName
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getMetaData(Context context, @NonNull String packageName, @NonNull String key, int defaultValue) {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(packageName);
        final List<ResolveInfo> resolveInfoList = getResolveInfoList(context, intent, PackageManager.GET_META_DATA);
        if (resolveInfoList.size() == 0) {
            Log.e(TAG, "Don't find matched app!");
            return defaultValue;
        }
        return getMetaData(resolveInfoList.get(0), key, defaultValue);
    }

    /**
     * 获取meta-data中的数值
     *
     * @param context
     * @param intent
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getMetaData(Context context, @NonNull Intent intent, @NonNull String key, int defaultValue) {
        final List<ResolveInfo> resolveInfoList = getResolveInfoList(context, intent, PackageManager.GET_META_DATA);
        if (resolveInfoList.size() == 0) {
            Log.e(TAG, "Don't find matched app!");
            return defaultValue;
        }
        return getMetaData(resolveInfoList.get(0), key, defaultValue);
    }

    /**
     * 获取meta-data中的数值
     *
     * @param resolveInfo
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getMetaData(@NonNull ResolveInfo resolveInfo, @NonNull String key, @NonNull String defaultValue) {
        final ActivityInfo activityInfo = resolveInfo.activityInfo;
        final Bundle metaDataActivity = activityInfo.metaData;
        if (metaDataActivity != null && metaDataActivity.containsKey(key)) {
            return metaDataActivity.getString(key, defaultValue);
        }
        final ApplicationInfo applicationInfo = activityInfo.applicationInfo;
        final Bundle metaDataApplication = applicationInfo.metaData;
        if (metaDataApplication != null && metaDataApplication.containsKey(key)) {
            return metaDataApplication.getString(key, defaultValue);
        }
        return defaultValue;
    }

    /**
     * 获取meta-data中的数值
     *
     * @param resolveInfo
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getMetaData(@NonNull ResolveInfo resolveInfo, @NonNull String key, int defaultValue) {
        final ActivityInfo activityInfo = resolveInfo.activityInfo;
        final Bundle metaDataActivity = activityInfo.metaData;
        if (metaDataActivity != null && metaDataActivity.containsKey(key)) {
            return metaDataActivity.getInt(key, defaultValue);
        }
        final ApplicationInfo applicationInfo = activityInfo.applicationInfo;
        final Bundle metaDataApplication = applicationInfo.metaData;
        if (metaDataApplication != null && metaDataApplication.containsKey(key)) {
            return metaDataApplication.getInt(key, defaultValue);
        }
        return defaultValue;
    }


    /**
     * 卸载
     *
     * @param packageName
     * @param flags
     */
    public void deletePackage(PackageManager packageManager, IPackageDeleteObserver iPackageDeleteObserver, String packageName, int flags) {
        try {
            Class<?>[] deleteTypes = new Class[]{String.class, IPackageDeleteObserver.class, int.class};
            Method deleteMethod = packageManager.getClass().getMethod("deletePackage", deleteTypes);
            deleteMethod.invoke(packageManager, packageName, iPackageDeleteObserver, flags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 安装
     *
     * @param file
     * @param flags
     * @param installerPackageName
     */
    public void installPackage(PackageManager packageManager, IPackageInstallObserver iPackageInstallObserver, File file, int flags, @NonNull String installerPackageName) {
        try {
            Class<?>[] types = new Class[]{Uri.class, IPackageInstallObserver.class, int.class, String.class};
            Method method = packageManager.getClass().getMethod("installPackage", types);
            method.invoke(packageManager, Uri.fromFile(file),
                    iPackageInstallObserver, flags, installerPackageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
