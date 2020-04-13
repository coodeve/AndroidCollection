package com.picovr.androidcollection.Utils.system;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Map<String, String> infos = new HashMap();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
    private static final String TAG = getInstance().getClass().getSimpleName();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        LogUtils.e(TAG, "crash handler没有捕获,由默认handler处理异常");
        if (!this.handleException(ex) && this.mDefaultHandler != null) {
            this.mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
        }

    }

    private boolean handleException(Throwable ex) {
        LogUtils.e(TAG, "crash handler捕获并处理异常");
        if (ex == null) {
            return false;
        } else {
            final String crashReport = this.getCrashReport(this.mContext, ex);
            LogUtils.e(crashReport);
            /*(new Thread() {
                public void run() {
                    Looper.prepare();
                    Log.e(CrashHandler.TAG, crashReport);
                    Looper.loop();
                }
            }).start();*/
            return true;
        }
    }

    protected File saveCrashInfo2File(String crashReport) {
        String fileName = "crash-" + this.formatter.format(new Date()) + ".log";
        if ("mounted".equals(Environment.getExternalStorageState())) {
            try {
                File e = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash");
                if (!e.exists()) {
                    e.mkdir();
                }

                File file = new File(e, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(crashReport.toString().getBytes());
                fos.close();
                return file;
            } catch (Exception var6) {
                Log.e(TAG, "an error occured while writing file...");
            }
        }

        return null;
    }

    private String getCrashReport(Context context, Throwable ex) {
        this.collectDeviceInfo(context);
        return this.collectException(ex);
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager fields = ctx.getPackageManager();
            PackageInfo attr = fields.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (attr != null) {
                String len$ = attr.versionName == null ? "null" : attr.versionName;
                String i$ = attr.versionCode + "";
                this.infos.put("versionName", len$);
                this.infos.put("versionCode", i$);
            }
        } catch (PackageManager.NameNotFoundException var9) {
            Log.e(TAG, "an error occured when collect package info");
        }

        Field[] var10 = Build.class.getDeclaredFields();
        Field[] var11 = var10;
        int var12 = var10.length;

        for (int var13 = 0; var13 < var12; ++var13) {
            Field field = var11[var13];

            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get(null).toString());
            } catch (Exception var8) {
                Log.e(TAG, "an error occured when collect crash info");
            }
        }

    }

    private String collectException(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        Iterator writer = this.infos.entrySet().iterator();

        String result;
        while (writer.hasNext()) {
            Map.Entry printWriter = (Map.Entry) writer.next();
            String cause = (String) printWriter.getKey();
            result = (String) printWriter.getValue();
            sb.append(cause + "=" + result + "\n");
        }

        StringWriter writer1 = new StringWriter();
        PrintWriter printWriter1 = new PrintWriter(writer1);
        ex.printStackTrace(printWriter1);

        for (Throwable cause1 = ex.getCause(); cause1 != null; cause1 = cause1.getCause()) {
            cause1.printStackTrace(printWriter1);
        }

        printWriter1.close();
        result = writer1.toString();
        sb.append(result);
        return sb.toString();
    }
}