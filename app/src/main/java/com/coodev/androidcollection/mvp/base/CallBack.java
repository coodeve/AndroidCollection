package com.coodev.androidcollection.mvp.base;

public interface CallBack<T> {
    /**
     * 加载成功
     * @param t
     */
    void onLoadSuccess(T t);

    /**
     * 连接到，但是接口失败；未连接到；异常状态；等一定会调用，可以隐藏加载中的控件
     * @param msg
     */
    void onLoadFailure(String msg);
}