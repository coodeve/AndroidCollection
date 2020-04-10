package com.picovr.androidcollection.Utils.dev.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.UUID;

public class BleUtil {
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

    //是否支持
    public static boolean isSupportBle(Context context) {
        if (context == null || !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return manager.getAdapter() != null;
    }

    //是否开启
    public static boolean isBleEnable(Context context) {
        if (!isSupportBle(context)) {
            return false;
        }
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        return manager.getAdapter().isEnabled();
    }

    //开启蓝牙
    public static void enableBluetooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取目标的service
     *
     * @param bluetoothGattServer
     * @param serviceUUID
     * @return
     */
    public static BluetoothGattService getBluetoothGattService(BluetoothGattServer bluetoothGattServer, UUID serviceUUID) {
        return bluetoothGattServer.getService(serviceUUID);
    }

    /**
     * 获取目标的Characteristic
     *
     * @param bluetoothGattServer
     * @param characteristicUUID
     * @return
     */
    public static BluetoothGattCharacteristic getBluetoothGattCharacteristic(BluetoothGattServer bluetoothGattServer, UUID characteristicUUID) {
        List<BluetoothGattService> services = bluetoothGattServer.getServices();
        for (BluetoothGattService bluetoothGattService : services) {
            BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(characteristicUUID);
            if (characteristic != null) {
                return characteristic;
            }
        }

        return null;
    }

    /**
     * byte转int
     *
     * @param b
     * @param c
     * @return
     */
    public static int byteToInt(byte b, byte c) {//计算总包长，两个字节表示的
        short s = 0;
        int ret;
        short s0 = (short) (c & 0xff);// 最低位
        short s1 = (short) (b & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        ret = s;
        return ret;
    }

    /**
     * int转byte
     *
     * @param res
     * @return
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[2];
        targets[1] = (byte) (res & 0xff);// 最低位
        targets[0] = (byte) ((res >> 8) & 0xff);// 次低位
        return targets;
    }

    /**
     * 16位字符串转byte
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 字符转byte
     *
     * @param c
     * @return
     */
    public static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * byte转16位
     *
     * @param buffer
     * @return
     */
    public static String byte2hex(byte[] buffer) {
        String h = "";
        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + temp;
        }
        return h;
    }
}
