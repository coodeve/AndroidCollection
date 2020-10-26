package com.coodev.androidcollection.Utils.dev.ble;

public interface BleCallback {

    public static final int TYPE_WRITE = 1;

    public static final int TYPE_READ = 2;

    /**
     *
     * @param readAndWrite {@link #TYPE_READ#TYPE_WRITE}
     * @param curByte
     * @param totalBytes
     */
    void process(int readAndWrite, int curByte, int totalBytes);

    /**
     *
     * @param readAndWrite {@link #TYPE_READ#TYPE_WRITE}
     * @param bytes
     * @param start
     * @param end
     */
    void compelete(int readAndWrite, byte[] bytes, int start, int end);

    /**
     *
     * @param readAndWrite {@link #TYPE_READ#TYPE_WRITE}
     * @param code
     */
    void error(int readAndWrite, int code);

}
