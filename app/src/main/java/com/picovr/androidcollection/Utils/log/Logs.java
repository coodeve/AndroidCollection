package com.picovr.androidcollection.Utils.log;

import android.text.TextUtils;
import android.util.Log;

import com.picovr.androidcollection.BuildConfig;
import com.picovr.androidcollection.Utils.common.Utils;

/**
 * @author patrick.ding
 * @date 2017/10/12
 */

public class Logs {
    private static boolean isDebug = BuildConfig.DEBUG;// Debug版本进行log输出
    private static final String TAG = Logs.class.getSimpleName();
    static void initLogDebug() {
        String commandLine = Utils.getProperties();
        if(!TextUtils.isEmpty(commandLine)){
            Log.i(TAG,"isDebug has modify by adb shell , show log now ! ");
            Logs.setIsDebug(true);
        }
    }
    public static void setIsDebug(boolean setDebug){
        isDebug = setDebug;
    }
    // 下面四个是默认tag的函数
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }

    public static void w(String msg)
    {
        if (isDebug)
            Log.w(TAG, msg);
    }
    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.v(tag, msg);
    }

    public static void w(String tag, String msg)
    {
        if (isDebug)
            Log.w(tag, msg);
    }
}
