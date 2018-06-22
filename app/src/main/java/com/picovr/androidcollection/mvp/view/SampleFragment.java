package com.picovr.androidcollection.mvp.view;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picovr.androidcollection.mvp.manager.ISampleContract;
import com.picovr.androidcollection.mvp.model.LoadDataModeImpl;
import com.picovr.androidcollection.mvp.presenter.SamplePresenterImpl;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public class SampleFragment extends Fragment implements ISampleContract.View{
    private static final String PARAMS = "params";
    private ISampleContract.Presenter presenter;
    private SamplePresenterImpl mSamplePresenter;

    public static SampleFragment newInstance(String... params){
        SampleFragment sampleFragment = new SampleFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(PARAMS,params);
        sampleFragment.setArguments(bundle);
        return sampleFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 创建Present对象
        mSamplePresenter = new SamplePresenterImpl(new LoadDataModeImpl());
        mSamplePresenter.attachView(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            Bundle bundle  = getArguments();
            String[] params = bundle.getStringArray(PARAMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void refreshUI(String data) {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public Context getContexts() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSamplePresenter.detachView();
    }
}
