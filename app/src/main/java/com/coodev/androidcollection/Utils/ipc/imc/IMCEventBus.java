package com.coodev.androidcollection.Utils.ipc.imc;

import android.util.Log;

import com.coodev.androidcollection.entity.Info;
import com.coodev.androidcollection.entity.LoginInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 使用{@link org.greenrobot.eventbus.EventBus}作为组件间通讯工具
 * <p>
 * 使用如下方式接收通知
 *
 * @Subscribe
 * public void onEvent(Object object) {
 * <p>
 * }
 */
public class IMCEventBus implements InterModuleCommunication.IMC<Object, Object> {

    public IMCEventBus() {
    }

    @Override
    public void register(Object t) {
        EventBus.getDefault().register(t);
    }

    @Override
    public void unRegister(Object t) {
        EventBus.getDefault().unregister(t);
    }

    @Override
    public void notify(Object object) {
        EventBus.getDefault().post(object);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginInfo info) {
        final String userName = info.userName;
    }
}