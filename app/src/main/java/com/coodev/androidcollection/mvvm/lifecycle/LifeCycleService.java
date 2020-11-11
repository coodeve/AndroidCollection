package com.coodev.androidcollection.mvvm.lifecycle;

import androidx.lifecycle.LifecycleService;

public class LifeCycleService extends LifecycleService {
    public LifeCycleService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        getLifecycle().addObserver(new LocationObserver());

    }
}
