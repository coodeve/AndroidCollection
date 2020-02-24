package com.picovr.androidcollection.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author patrick.ding
 * @since 20/2/24
 */
public abstract class BaseFragemnt extends Fragment {

    protected Context mContext;


    /**
     * fragment根视图
     */
    private View mRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(attachLayoutRes(), container, false);
        initViews(mRootView);
        return mRootView;
    }

    protected <T extends View> T findViewById(int resId) {
        if (mRootView == null) {
            return null;
        }

        return mRootView.findViewById(resId);
    }

    protected abstract int attachLayoutRes();

    protected abstract void initViews(View rootView);

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
