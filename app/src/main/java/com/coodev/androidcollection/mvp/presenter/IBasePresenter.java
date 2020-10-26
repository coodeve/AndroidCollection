package com.coodev.androidcollection.mvp.presenter;

import com.coodev.androidcollection.mvp.view.IBaseView;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public interface  IBasePresenter<T extends IBaseView> {
    // onResume时进行调用，用于获取初始数据
    void detachView();
    void attachView(T view);
    boolean isViewAttached();
    void getData();
}
