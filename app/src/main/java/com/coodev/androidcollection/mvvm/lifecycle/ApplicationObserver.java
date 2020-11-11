package com.coodev.androidcollection.mvvm.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * 应用程序生命周期监听，用于Application
 */
public class ApplicationObserver implements LifecycleObserver {
    /**
     * 应用程序创建（只调用一次）
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate(){

    }

    /**
     * 应用程序前台
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(){

    }

    /**
     * 应用程序后台
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(){

    }

    /**
     * 应用程序销毁（不会被调用）
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){

    }
}
