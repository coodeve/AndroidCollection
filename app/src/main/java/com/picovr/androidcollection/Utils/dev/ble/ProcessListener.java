package com.picovr.androidcollection.Utils.dev.ble;

/**
 * 进度监听
 */
public interface ProcessListener {
    /**
     * @param done_byte 传输字节
     * @param all_byte  所有字节
     */
    void onProgress(int done_byte, int all_byte);
}
