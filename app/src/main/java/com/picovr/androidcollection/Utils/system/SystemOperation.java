package com.picovr.androidcollection.Utils.system;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author patrick.ding
 * @since 20/2/18
 */
public class SystemOperation {

    public static final String TAG = SystemOperation.class.getSimpleName();

    /**
     * 重启系统
     * 需要权限：{@link android.Manifest.permission#REBOOT}
     * 需要成为系统级应用：
     * 1.android:sharedUserId="android.uid.system"
     * 2.系统级的签名
     * <p>
     * 注意：此方法可能在某些机型上造成关机而不开机
     *
     * @param context
     */
    public static void reboot(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        powerManager.reboot("restart");
    }

    /**
     * 重启系统
     * 需要权限：{@link android.Manifest.permission#REBOOT}
     * 需要成为系统级应用：
     * 1.android:sharedUserId="android.uid.system"
     * 2.系统级的签名
     * <p>
     * 目前已测试机型上正常重启
     *
     * @param context
     */
    public static void rebootByBroadcast(Context context) {
        Intent intent = new Intent(Intent.ACTION_REBOOT);
        intent.putExtra("nowait", 1);
        intent.putExtra("interval", 1);
        intent.putExtra("window", 0);
        context.sendBroadcast(intent);
    }

    /**
     * 关机
     * 需要权限：<uses-permission android:name="android.permission.SHUTDOWN"/>
     * 需要成为系统级应用：
     * 1.android:sharedUserId="android.uid.system"
     * 2.系统级的签名
     * <p>
     * 目前已测试机型上正常重启
     *
     * @param context
     */
    public static void shutDown(Context context) {
        String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
        String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";
        Intent i = new Intent(ACTION_REQUEST_SHUTDOWN);
        //其中false换成true,会弹出是否关机的确认窗口
        i.putExtra(EXTRA_KEY_CONFIRM, false);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


    /**
     * 恢复出厂设置
     */
    public static void resetFactory(Context context) {
        Log.i(TAG, "reset# ");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.i(TAG, "reset# <8.0");
            Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
            intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
            context.sendBroadcast(intent);
        } else {
            Log.i(TAG, "reset# >=8.0");
            Intent intent = new Intent("android.intent.action.FACTORY_RESET");
            intent.setPackage("android");
            intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.putExtra("android.intent.extra.REASON", "MasterClearConfirm");
            intent.putExtra("android.intent.extra.WIPE_EXTERNAL_STORAGE", true);
            intent.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", true);
            context.sendBroadcast(intent);
        }

    }

    /**
     * 屏幕唤醒，需要权限
     * @param context
     */
    public static void wakeUp(Context context) {
        try {
            PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            Method wakeUp = PowerManager.class.getMethod("wakeUp", long.class);
            wakeUp.invoke(mPowerManager, SystemClock.uptimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 屏幕休眠，需要权限
     * @param context
     */
    public static void sleep(Context context) {
        try {
            PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            Method goToSleep = PowerManager.class.getMethod("goToSleep", long.class);
            goToSleep.invoke(mPowerManager, SystemClock.uptimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
