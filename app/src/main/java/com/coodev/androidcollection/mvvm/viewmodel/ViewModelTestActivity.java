package com.coodev.androidcollection.mvvm.viewmodel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.coodev.androidcollection.R;

public class ViewModelTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_model_test);
        initViewModel();
    }

    private void initViewModel() {
        // 获取viewmodel实例，同一个activity应该只有一个
        final TimerViewModel timerViewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        // TODO 调用timerviewmode的方法
    }
}
