package com.picovr.androidcollection.Utils.crash;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.picovr.androidcollection.Utils.system.PermissionUtils;
import com.pvr.breakpad.BreakpadInit;

import java.io.File;

public class NativeCrash {
    /**
     * 初始化breakpad
     *
     * @param context
     */
    public static void init(Context context) {
        File crashDump = null;
        crashDump = new File(context.getExternalFilesDir(null), "crashDump");
        if (!crashDump.exists()) {
            crashDump.mkdirs();
        }

        BreakpadInit.initBreakpad(crashDump.getAbsolutePath());

    }


    public native static void nativeCrashTest();
}
