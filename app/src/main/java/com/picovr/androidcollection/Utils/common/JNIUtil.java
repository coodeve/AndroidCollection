package com.picovr.androidcollection.Utils.common;

public class JNIUtil {
    static {
        System.loadLibrary("coo");
    }


    public static native void init();

    public native void hello();
}
