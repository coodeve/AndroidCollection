package com.coodev.androidcollection.Utils.ipc.imc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 使用{@link LocalBroadcastManager}作为组件间通讯工具
 * 使用{@link BroadcastReceiver} 接收通知
 */
public class IMCLocalBroadcast implements InterModuleCommunication.IMC<IMCLocalBroadcast.Receiver, Intent> {

    public static class Receiver {
        BroadcastReceiver mReceiver;
        IntentFilter mIntentFilter;
    }

    private final LocalBroadcastManager mInstance;
    private Context mContext;

    public IMCLocalBroadcast(Context context) {
        mContext = context;
        mInstance = LocalBroadcastManager.getInstance(mContext);
    }


    @Override
    public void register(Receiver receiver) {
        mInstance.registerReceiver(receiver.mReceiver, receiver.mIntentFilter);
    }

    @Override
    public void unRegister(Receiver receiver) {
        mInstance.unregisterReceiver(receiver.mReceiver);
    }

    @Override
    public void notify(Intent intent) {
        mInstance.sendBroadcast(intent);
    }

}