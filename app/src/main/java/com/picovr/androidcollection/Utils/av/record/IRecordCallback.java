package com.picovr.androidcollection.Utils.av.record;


public interface IRecordCallback {

    /**
     * 开始录制
     */
    void onStart();

    /**
     * 停止录制
     * @param path 文件输出路径
     */
    void onStop(String path);

    /**
     * 录制过程错误
     * @param eMsg  错误信息
     */
    void onError(String eMsg);

}
