package com.coo.alloctrack;

public class AllocTracker {
    static {
        System.loadLibrary("alloc-lib");
    }

    /**
     * 数据保存位置
     *
     * @param dir
     */
    public native void setSaveDataDirectory(String dir);

    /**
     * 输出内存dump到日志
     */
    public native void dumpAllocationDataInLog();

    /**
     * 开始
     */
    public native void startAllocationTracker();

    /**
     * 停止
     */
    public native void stopAllocationTracker();

    /**
     * 初始化
     *
     * @param apiLevel
     * @param allocRecordMax
     * @return
     */
    public native int initForArt(int apiLevel, int allocRecordMax);
}
