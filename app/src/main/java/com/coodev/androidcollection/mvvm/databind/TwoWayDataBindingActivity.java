package com.coodev.androidcollection.mvvm.databind;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.coodev.androidcollection.R;
import com.coodev.androidcollection.databinding.ActivityTwoWayDatabindingBinding;

/**
 * 双向绑定
 */
public class TwoWayDataBindingActivity extends AppCompatActivity {
    private ActivityTwoWayDatabindingBinding mViewDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_two_way_databinding);
        mViewDataBinding.setCustomViewModel(new TwoWayViewModel());
    }

    /**
     * 使用{@link TwoWayDataBinding}
     */
    private void init() {
        mViewDataBinding.setCustomObserver(new TwoWayDataBinding());
    }
}
