package com.picovr.androidcollection.mvp.presenter;

import com.picovr.androidcollection.mvp.view.IBaseView;

import java.lang.ref.WeakReference;

/**
 * 此类，为一点基本的通用功能的提取，自己可直接使用接口进行实现。
 * @author patrick.ding
 * @date 18/6/22
 */

public abstract class BasePresenter<T extends IBaseView> implements IBasePresenter<T> {
    private WeakReference<IBaseView> mWeakView;
    @Override
    public void detachView() {
        if (mWeakView != null) {
            mWeakView.clear();
            mWeakView = null;
        }
    }
    @Override
    public void attachView(IBaseView view) {
        mWeakView = new WeakReference<IBaseView>(view);
    }

    @Override
    public boolean isViewAttached() {
        return mWeakView != null && mWeakView.get() != null;
    }

}
