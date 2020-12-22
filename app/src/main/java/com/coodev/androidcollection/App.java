package com.coodev.androidcollection;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.coodev.androidcollection.Utils.ipc.ARouterUtils;
import com.coodev.androidcollection.mvvm.lifecycle.ApplicationObserver;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @author patrick.ding
 * @since 20/2/12
 */
public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        ARouterUtils.init(this);
        initX5();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new ApplicationObserver());
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 腾讯x5内核初始化
     */
    private void initX5() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.i("InnerWebApplication", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
