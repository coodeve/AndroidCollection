package com.picovr.androidcollection.mvp.presenter;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public interface IBasePresenter {
    // onResume时进行调用，用于获取初始数据
    void start();
}
