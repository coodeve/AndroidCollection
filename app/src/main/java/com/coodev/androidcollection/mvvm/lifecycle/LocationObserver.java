package com.coodev.androidcollection.mvvm.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class LocationObserver implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startGetLocation() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopGetLocation() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void startGetLocationInService(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void stopGetLocationInService(){

    }
}
