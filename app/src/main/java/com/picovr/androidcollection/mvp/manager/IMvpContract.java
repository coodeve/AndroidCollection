package com.picovr.androidcollection.mvp.manager;

import com.picovr.androidcollection.mvp.presenter.IBasePresenter;
import com.picovr.androidcollection.mvp.view.IBaseView;

/**
 * 契约类，综合管理View和Presenter
 * @author patrick.ding
 * @date 18/6/21
 */

public interface IMvpContract {

    interface Presenter extends IBasePresenter<IBaseView> {
        // 获取数据
        void getUserInfo();
    }

    interface View extends IBaseView {
        // 刷新界面
        void refreshUI(String data);
    }
}
