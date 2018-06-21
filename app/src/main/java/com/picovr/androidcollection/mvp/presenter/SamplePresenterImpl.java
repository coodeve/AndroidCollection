package com.picovr.androidcollection.mvp.presenter;

import com.picovr.androidcollection.mvp.manager.ISampleContract;
import com.picovr.androidcollection.mvp.model.IBaseModel;

import java.lang.ref.WeakReference;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public class SamplePresenterImpl implements ISampleContract.Presenter {

    private final IBaseModel iBaseModel;
    private WeakReference<ISampleContract.View> weakView;
    public SamplePresenterImpl(ISampleContract.View view, IBaseModel iBaseModel) {
        weakView = new WeakReference<ISampleContract.View>(view);
        weakView.get().setPresenter(this);
        this.iBaseModel = iBaseModel;
    }

    @Override
    public void start() {

    }

    @Override
    public void getData() {

    }

    @Override
    public void checkData() {

    }

    @Override
    public void deleteMsg() {

    }
}
