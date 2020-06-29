package com.picovr.androidcollection.Utils.system;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     *
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
     *
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

    /**
     * 获取当前运行环境是ART还是Davilk
     * 获取到的版本值大于等于2，即为ART
     *
     * @return 返回true则为art环境
     */
    public static boolean isArtofRuntime() {
        StringBuilder stringBuilder = new StringBuilder();
        String version = PropertyUtils.getSysProperty("java.vm.version");
        boolean isART = false;
        if (version != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(version);
            if (matcher.matches()) {
                try {
                    int major = Integer.parseInt(matcher.group(1));
                    int minor = Integer.parseInt(matcher.group(2));
                    isART = major > 2 || major == 2 && minor >= 1;
                } catch (NumberFormatException var5) {
                }
            }
        }

        Log.i(TAG, "VM with version " + version + (isART ? " has ART support" : " does not have ART support"));
        return isART;
    }


    public int getIntResult(Context context) {
        int screenMode;
        int screenBrightness = 255 / 2;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            Log.i(TAG, "当前亮度模式 ： " + screenMode);
            screenBrightness = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            Log.i(TAG, "当前亮度为~ ： " + screenBrightness);

            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                setScreenMode(context, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    private void setScreenMode(Context context, int screenBrightnessModeManual) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                screenBrightnessModeManual);
    }

    /**
     * 设置亮度 0~255
     *
     * @param value
     */
    public void setScreenBrightness(Context context, float value) {

        int screenMode;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (screenMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                setScreenMode(context, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (value > 255)
            value = 255;
        else if (value < 0)
            value = 0;
        Window mWindow = ((Activity) context).getWindow();
        WindowManager.LayoutParams mParams = mWindow.getAttributes();
        float f = value / 255.0F;
        mParams.screenBrightness = f;
        mWindow.setAttributes(mParams);

        // 保存设置的屏幕亮度值
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) value);

    }

    /**
     * 设置系统语言
     *
     * @param locale
     */
    public void updateLanguageNew(Locale locale) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.app.LocalePicker");
            Method method = clazz.getDeclaredMethod("updateLocale", Locale.class);
            method.invoke(null, locale);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * app内切换语言
     *
     * @param locale
     */
    public void updateAppLanguage(Context context,Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        configuration.locale = locale;
        resources.updateConfiguration(configuration, dm);
        Log.i("Common", "updateAPPlanguage");
    }

}
