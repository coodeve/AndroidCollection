package com.coodev.androidcollection.mvvm.viewmodel;

import androidx.lifecycle.ViewModel;

/**
 * ViewModel中最好不要传入Activity所持有的context
 */
public class TimerViewModel extends ViewModel {
    @Override
    protected void onCleared() {
        super.onCleared();
    }

}
