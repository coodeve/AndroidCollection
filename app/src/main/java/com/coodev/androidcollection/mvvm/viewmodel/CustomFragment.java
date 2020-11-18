package com.coodev.androidcollection.mvvm.viewmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class CustomFragment extends Fragment {

    private MutableLiveData<Integer> mLiveData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        // 可用于fragment间通信
        final TimerViewModel timerViewModel = new ViewModelProvider(getActivity()).get(TimerViewModel.class);
        mLiveData = (MutableLiveData<Integer>) timerViewModel.getLiveData();
        mLiveData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        });
    }


    private void setValue(int value) {
        mLiveData.setValue(value);
    }

}
