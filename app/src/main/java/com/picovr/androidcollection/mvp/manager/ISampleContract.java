package com.picovr.androidcollection.mvp.manager;

import com.picovr.androidcollection.mvp.presenter.IBasePresenter;
import com.picovr.androidcollection.mvp.view.IBaseView;

/**
 * 契约类，综合管理View和Presenter
 * @author patrick.ding
 * @date 18/6/21
 */

public interface ISampleContract {

    interface Presenter extends IBasePresenter {
        // 获取数据
        void getData();
        // 检查数据是否有效
        void checkData();
        // 删除消息
        void deleteMsg();
    }

    interface View extends IBaseView<Presenter> {
        // 显示loading
        void showLoading();
        // 刷新界面
        void refreshUI();
        // 显示错误界面
        void showError();
    }
}
