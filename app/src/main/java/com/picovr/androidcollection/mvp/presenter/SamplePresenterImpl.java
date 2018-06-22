package com.picovr.androidcollection.mvp.presenter;

import com.picovr.androidcollection.mvp.base.OnLoadListener;
import com.picovr.androidcollection.mvp.manager.ISampleContract;
import com.picovr.androidcollection.mvp.manager.ModelFactory;
import com.picovr.androidcollection.mvp.model.IBaseModel;
import com.picovr.androidcollection.mvp.token.Token;
import com.picovr.androidcollection.mvp.view.IBaseView;

import java.lang.ref.WeakReference;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public class SamplePresenterImpl<T extends IBaseView> implements ISampleContract.Presenter {

    private final IBaseModel baseModelImpl;
    private WeakReference<ISampleContract.View> mWeakView;
    private String param = "1";
    public SamplePresenterImpl(IBaseModel baseModelImpl) {
        this.baseModelImpl = baseModelImpl;
    }

    @Override
    public void attachView(ISampleContract.View view) {
        mWeakView = new WeakReference<ISampleContract.View>(view);
    }

    @Override
    public void detachView() {
        if (mWeakView != null) {
            mWeakView.clear();
            mWeakView = null;
        }
    }

    @Override
    public boolean isViewAttached() {
        return mWeakView != null && mWeakView.get() != null;
    }

    @Override
    public void getData() {
        baseModelImpl.requestData(new OnLoadListener<String>() {
            @Override
            public void onLoadSuccess(String s) {

            }

            @Override
            public void onLoadFailure(String msg) {

            }
        });

        ModelFactory
                .build(Token.API_USER_MODEL)
                .params(param)
                .execute(new OnLoadListener<String>() {
                    @Override
                    public void onLoadSuccess(String s) {

                    }

                    @Override
                    public void onLoadFailure(String msg) {

                    }
                });

    }
}
