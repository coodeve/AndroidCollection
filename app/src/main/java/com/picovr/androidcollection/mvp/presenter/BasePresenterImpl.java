package com.picovr.androidcollection.mvp.presenter;

import com.picovr.androidcollection.mvp.base.CallBack;
import com.picovr.androidcollection.mvp.model.IBaseModel;
import com.picovr.androidcollection.mvp.view.IBaseView;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public class BasePresenterImpl extends BasePresenter<IBaseView> {

    private final IBaseModel mBaseModelImpl;
    public BasePresenterImpl(IBaseModel baseModelImpl) {
        this.mBaseModelImpl = baseModelImpl;
    }


    @Override
    public void getData() {
        mBaseModelImpl.execute(new CallBack<String>() {
            @Override
            public void onLoadSuccess(String s) {

            }

            @Override
            public void onLoadFailure(String msg) {

            }
        });
    }
}
