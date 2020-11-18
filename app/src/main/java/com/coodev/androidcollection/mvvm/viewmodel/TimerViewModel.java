package com.coodev.androidcollection.mvvm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel中最好不要传入Activity所持有的context
 */
public class TimerViewModel extends ViewModel {

    /**
     * 使用LiveData
     */
    private MutableLiveData<Integer> mLiveData;

    public LiveData<Integer> getLiveData() {
        if (mLiveData == null) {
            mLiveData = new MutableLiveData<>();
        }
        return mLiveData;
    }

    /**
     * 资源清理
     */
    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
