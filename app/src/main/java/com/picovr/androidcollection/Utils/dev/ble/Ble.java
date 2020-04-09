package com.picovr.androidcollection.Utils.dev.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

public class Ble {
    /**
     * 检查蓝牙是否可用
     *
     * @param activity
     * @param requestCode
     * @param bluetoothAdapter
     */
    public static void checkBleEnable(Activity activity, int requestCode, BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, requestCode);
        }
    }
}
