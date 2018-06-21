package com.picovr.androidcollection.mvp.view;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.picovr.androidcollection.mvp.manager.ISampleContract;

/**
 * @author patrick.ding
 * @date 18/6/21
 */

public class SampleFragment extends Fragment implements ISampleContract.View{
    private static final String PARAMS = "params";
    private ISampleContract.Presenter presenter;

    public static SampleFragment newInstance(String... params){
        SampleFragment sampleFragment = new SampleFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(PARAMS,params);
        sampleFragment.setArguments(bundle);
        return sampleFragment;
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
    public void setPresenter(ISampleContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean isActive() {
        return this.isActive();
    }

    @Override
    public void detach() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void refreshUI() {

    }

    @Override
    public void showError() {

    }
}
