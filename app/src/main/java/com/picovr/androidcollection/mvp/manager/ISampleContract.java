package com.picovr.androidcollection.mvp.manager;

import com.picovr.androidcollection.mvp.presenter.IBasePresenter;
import com.picovr.androidcollection.mvp.view.IBaseView;

/**
 * 契约类，综合管理View和Presenter
 * @author patrick.ding
 * @date 18/6/21
 */

public interface ISampleContract {

    interface Presenter extends IBasePresenter<View> {
        // 获取数据
        void getData();
    }

    interface View extends IBaseView {
        // 刷新界面
        void refreshUI(String data);
    }
}
