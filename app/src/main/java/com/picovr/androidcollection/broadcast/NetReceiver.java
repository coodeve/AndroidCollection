package com.picovr.androidcollection.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.picovr.androidcollection.Utils.common.Utils;
import com.picovr.androidcollection.Utils.log.Logs;

public class NetReceiver extends BroadcastReceiver {
    private Context mContext;
    private ReceiverCallback mCallback;

    public NetReceiver() {
    }


    public NetReceiver(Context context, ReceiverCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            if (Utils.getNetworkState(mContext)) {
                Logs.i("NestServer", "wifi enable !!!");
                if (mCallback != null) {
                    mCallback.wifiConnected();
                    Logs.i("NestServer", "go to Callback");
                }
            }
        }

    }

    protected void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(this, intentFilter);
    }

    protected void unRegister() {
        mContext.unregisterReceiver(this);
    }

    public interface ReceiverCallback {
        void wifiConnected();
    }

}
