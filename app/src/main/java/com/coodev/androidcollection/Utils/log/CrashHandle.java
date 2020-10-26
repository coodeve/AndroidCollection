package com.coodev.androidcollection.Utils.log;

import android.util.Log;

public class CrashHandle implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandle";

    private CrashHandle() {
    }

    public static CrashHandle getInstance() {
        return Holder.sCrashHandle;
    }

    private static class Holder {
        private static CrashHandle sCrashHandle = new CrashHandle();
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.i(TAG, "uncaughtException# thread:" + t.toString() + ",throwable:" + e.toString());
    }
}