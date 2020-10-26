package com.coodev.androidcollection.Utils.av.record;

import android.app.Activity;


public interface Recorder {

    /**
     * 创建
     * @param activity
     * @param textureView
     */
    void create(Activity activity, AutoFitTextureView textureView);

    /**
     * 销毁
     */
    void destroy();

    /**
     * 开始录制
     * @param path
     */
    void startRecord(String path);

    /**
     * 停止录制
     */
    void stopRecord();

    /**
     * 当前是否正在录制
     * @return
     */
    boolean isRecording();

    /**
     * 设置监听
     * @param callback
     */
    void setRecordCallback(IRecordCallback callback);

}
