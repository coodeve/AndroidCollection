package com.picovr.androidcollection.Utils.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.picovr.androidcollection.Utils.log.Logs;
import com.picovr.androidcollection.Utils.shell.ShellUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author patrick.ding
 * @date 2017/9/28
 */

public class Utils {
    private static final long END_TIME = System.currentTimeMillis();
    private static final long TIME_INTERVAL = 7 * 24 * 60 * 60 * 1000L;
    private static final long START_TIME = END_TIME - TIME_INTERVAL;
    private static final String TAG = "DpPxUtils";
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

    public static boolean getNetworkState(Context context) {
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
    public static String getProperties() {
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

    /**
     * 匹配0.0 ，0，0.00字符串
     *
     * @param total
     * @return
     */
    public static boolean matchZero(String total) {
        if (TextUtils.isEmpty(total)) {
            return false;
        }
        String pattern = "^0$|^0.0+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(total);
        return m.find();
    }

    /**
     * 匹配数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
