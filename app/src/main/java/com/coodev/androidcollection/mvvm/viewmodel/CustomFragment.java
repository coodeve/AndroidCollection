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
    private TimerViewModel mTimerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        // 可用于fragment间通信
        mTimerViewModel = new ViewModelProvider(getActivity()).get(TimerViewModel.class);
        mLiveData = (MutableLiveData<Integer>) mTimerViewModel.getLiveData();
        // 相当于监听通信
        mLiveData.observe(this, integer -> {

        });
    }

    /**
     * 相当于发送信息
     *
     * @param value
     */
    private void setValue(int value) {
        mLiveData.setValue(value);
    }

    @Override
    public void onDestroyView() {
        mLiveData.removeObservers(this);
        mTimerViewModel.onCleared();
        super.onDestroyView();
    }
}
