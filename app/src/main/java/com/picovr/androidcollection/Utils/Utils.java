package com.picovr.androidcollection.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author patrick.ding
 * @date 2017/9/28
 */

public class Utils {
    private static final long END_TIME = System.currentTimeMillis();
    private static final long TIME_INTERVAL = 7 * 24 * 60 * 60 * 1000L;
    private static final long START_TIME = END_TIME - TIME_INTERVAL;
    private static final String TAG = "Utils";
    private static final String PREFERENCES_NAME = "app";
    private static final String COMMAND = "dumpsys activity a | sed -n -e \"/Stack #/p\" -e \"/Running activities/,/Run #0/p\"";

    static String getTopAppName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            String appName = applicationInfo.loadLabel(packageManager).toString();
            return appName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    static String execCommand(String[] cmd, String workdirectory) {
        String result = "";
        ShellUtils.CommandResult commandResult = ShellUtils.execCommand(COMMAND, false);
        result = commandResult.successMsg;
        Log.i(TAG, "getCurrentPackName# " + result);
        return result;

    }

    static String getDeviceType() {
        return Build.MODEL;
    }


    public static String getSPvalue(Context context, String key, String defValue) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getString(key, defValue);
    }

    public static void setSPvalue(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static void setSPvalue(Context context, String key, long value) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    static long getSPvalue(Context context, String key, long defValue) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getLong(key, defValue);
    }

    static boolean getNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

            Logs.i(TAG, activeNetworkInfo.getType() + "");
            return true;
        } else {
            return false;
        }
    }


    /**
     * 通过命令获取属性参数 , adb shell setprop <key> <value>,设置属性参数
     */
    static String getProperties() {
        String commandLine = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            // 输入属性名称为pico，即可在release版本中实时看见log
            Process exec = runtime.exec("getprop pico");
            InputStream in = exec.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                commandLine = line;
                Log.i(TAG, "getprop pico: " + line);
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commandLine;
    }

}
