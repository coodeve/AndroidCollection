package com.coodev.androidcollection.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * 监听电量和充电状态
 */
public class BatteryBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = BatteryBroadcastReceiver.class.getSimpleName();
    public static final String ACTION_BATTERY = Intent.ACTION_BATTERY_CHANGED;

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);// 整数
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int levelPercent = (int) (((float) level / scale) * 100);
        Log.i(TAG, "onReceive: level:" + level + ",scale:" + scale + "," + levelPercent + "%");

    }


}
