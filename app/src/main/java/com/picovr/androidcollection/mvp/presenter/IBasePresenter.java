package com.picovr.androidcollection.mvp.presenter;

import com.picovr.androidcollection.mvp.view.IBaseView;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public interface  IBasePresenter<T extends IBaseView> {
    // onResume时进行调用，用于获取初始数据
    void detachView();
    void attachView(T view);
    boolean isViewAttached();
}
