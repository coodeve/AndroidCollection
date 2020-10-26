package com.coodev.androidcollection.mvp.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coodev.androidcollection.mvp.manager.ModelFactory;
import com.coodev.androidcollection.mvp.presenter.IBasePresenter;
import com.coodev.androidcollection.mvp.presenter.BasePresenterImpl;
import com.coodev.androidcollection.mvp.token.Token;

/**
 * @Author EnvisionBoundary
 * Created at  2018/6/24.
 */

public class FounctionFragment extends BaseFragment {
    private IBasePresenter mbasePresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mbasePresenter = new BasePresenterImpl(ModelFactory.build(Token.API_USER_MODEL));
        mbasePresenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mbasePresenter.detachView();
    }
}
