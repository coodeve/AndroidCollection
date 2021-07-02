package com.coodev.androidcollection.Utils.log;


import android.content.Context;

import com.coodev.androidcollection.BuildConfig;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

import java.io.File;

/**
 * 使用腾讯mars中的xlog模块
 */
public class MarsXLog {
    static {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
    }

    public static final String LOG_PATH = "xlog";

    public void init(Context context) {
        final String SDCARD = context.getExternalFilesDir(null).getAbsolutePath();
        final String logPath = SDCARD + File.separator + LOG_PATH;
        // this is necessary, or may crash for SIGBUS
        final String cachePath = context.getFilesDir() + File.separator + LOG_PATH;

        if (BuildConfig.DEBUG) {
            Xlog.open(true, Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync,
                    cachePath, logPath, "MarsLog", "");
            Log.setConsoleLogOpen(true);
        } else {
            Xlog.open(true, Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync,
                    cachePath, logPath, "MarsLog", "");
            Log.setConsoleLogOpen(false);
        }


        Log.setLogImp(new Xlog());
    }

    /**
     * 程序退出时需要关闭
     */
    public void close() {
        Log.appenderClose();
    }
}
