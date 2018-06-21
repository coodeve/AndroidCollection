package com.picovr.androidcollection.mvp.view;

import com.picovr.androidcollection.mvp.presenter.IBasePresenter;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public interface IBaseView<T extends IBasePresenter>  {
    // 注入presenter对象
    void setPresenter(T presenter);
    // 判断View是否可用
    boolean isActive();
    // 主动断开,用来处理延迟操作取消/对Activity的应用
    void detach();
}
