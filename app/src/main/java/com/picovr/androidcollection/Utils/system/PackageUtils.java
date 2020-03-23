package com.picovr.androidcollection.Utils.system;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PackageUtils {

    public static final String TAG = PackageUtils.class.getSimpleName();

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
}
