package com.picovr.androidcollection.Utils.crash;

import android.content.Context;

//import com.pvr.breakpad.BreakpadInit;

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

//        BreakpadInit.initBreakpad(crashDump.getAbsolutePath());

    }


    public native static void nativeCrashTest();
}
