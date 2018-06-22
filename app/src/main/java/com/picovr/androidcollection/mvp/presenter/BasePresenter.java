package com.picovr.androidcollection.mvp.presenter;

import com.picovr.androidcollection.mvp.view.IBaseView;

/**
 * @author patrick.ding
 * @date 18/6/22
 */

public class BasePresenter<T extends IBaseView> {
    private T view;

    public void attachView(T view){
        this.view = view;
    }

    public void detachView(){
        this.view  = null;
    }

    public boolean isViewAttached(){
        return this.view != null;
    }

    public T getView(){
        return this.view;
    }
}
