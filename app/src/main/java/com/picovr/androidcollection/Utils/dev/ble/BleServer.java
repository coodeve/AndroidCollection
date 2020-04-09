package com.picovr.androidcollection.Utils.dev.ble;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.companion.BluetoothDeviceFilter;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * ble服务端，监听连接，收发数据
 * ble的服务端，和普通服务端理解不太一样。
 * 可以从主从方式理解，ble服务端即从端
 * 从会发送ble的广播，主端（即客户端）会受到广播进而可以连接
 * <p>
 * 服务可以接受多个设备
 */
public class BleServer {
    public static final String TAG = BleServer.class.getSimpleName();
    private Context mContext;

    private final BluetoothManager mBluetoothManager;

    private final BluetoothAdapter mAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    // 用户配置
    private AbsAdvertiseSettings mAbsAdvertiseSettings;

    private List<BluetoothDevice> mBindBluetoothDevices = new ArrayList<>();


    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    private BluetoothGattServer mBluetoothGattServer;

    private DataBuffer dataBuffer = new DataBuffer(4096);
    /**
     * 广播创建的回调
     */
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i(TAG, "onStartSuccess: ");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.i(TAG, "onStartFailure: " + errorCode);
        }
    };

    private BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (!mBindBluetoothDevices.contains(device)) {
                    mBindBluetoothDevices.add(device);
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mBindBluetoothDevices.contains(device)) {
                    mBindBluetoothDevices.remove(device);
                }
            }
            super.onConnectionStateChange(device, status, newState);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            handleClientResponse(device, requestId, characteristic, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
        }

    };


    public BleServer(Context context, AbsAdvertiseSettings absAdvertiseSettings) {
        mContext = context;
        mAbsAdvertiseSettings = absAdvertiseSettings == null ? AbsAdvertiseSettings.DEFAULT : absAdvertiseSettings;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            throw new UnsupportedOperationException("this devices not support ble");
        } else {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mAdapter = mBluetoothManager.getAdapter();
        }

        init();
    }

    private void init() {
        mBluetoothGattServer = mBluetoothManager.openGattServer(mContext, mBluetoothGattServerCallback);
        mBluetoothGattCharacteristic = new BluetoothGattCharacteristic(BleConfig.getCharacteristicUuid(),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        BluetoothGattService bluetoothGattService = new BluetoothGattService(BleConfig.getServiceUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        bluetoothGattService.addCharacteristic(mBluetoothGattCharacteristic);
        // 可添加多个服务
        mBluetoothGattServer.addService(bluetoothGattService);
    }


    public void startBleAdvertise() {
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mAdapter.getBluetoothLeAdvertiser();
        }

        mBluetoothLeAdvertiser.startAdvertising(
                mAbsAdvertiseSettings.buildSettings(),
                mAbsAdvertiseSettings.buildData(),
                mAdvertiseCallback);

    }

    public void stopBleAdvertise() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBluetoothLeAdvertiser = null;
        }
    }

    /**
     * 发送数据
     *
     * @param device
     * @param bytes
     * @return
     */
    public boolean sendBytes(BluetoothDevice device, byte[] bytes) {
        if (mBluetoothGattCharacteristic == null) {
            Log.w(TAG, "sendBytes: BluetoothGattCharacteristic is null");
            return false;
        }

        mBluetoothGattCharacteristic.setValue(bytes);
        return mBluetoothGattServer.notifyCharacteristicChanged(device, mBluetoothGattCharacteristic, false);
    }


    private boolean sendBytes(byte[] bytes) {
        if (mBindBluetoothDevices != null) {
            // todo 需要修改
            return sendBytes(null, bytes);
        } else {
            return false;
        }
    }

    /**
     * 发送字符串
     * 注意: 1.不能连接上后马上发送,最好第一次发送做个延迟
     * 2.一定要在子线程里调用
     * 3.注意字节数不能超过4096，理论此发送速率 800 Bytes/S
     *
     * @param _bytes
     */
    public boolean send(byte[] _bytes) {
        try {
            byte[] bytes = DataUtils.getData(_bytes);
            int all_length = bytes.length;
            DataBuffer dataBuffer = new DataBuffer(all_length);
            dataBuffer.enqueue(bytes, all_length);
            boolean result = true;
            for (int i = 0; i < all_length / 20; i++) {
                byte[] sends = new byte[20];
                dataBuffer.dequeue(sends, 20);
                //兼容IOS的情况下20ms间隔，安卓为7.5ms间隔
                Thread.sleep(20);
                boolean isSend = sendBytes(sends);
                if (isSend) {
                    // todo  进度监听
                } else {
                    result = false;
                    break;
                }
            }
            return result;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 接收数据
     *
     * @param device
     * @param requestId
     * @param characteristic
     * @param value
     */
    private void handleClientResponse(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, byte[] value) {

    }

    /**
     * 收到的字节数组
     *
     * @param value
     */
    private void onReceiveBytes(byte[] value) {
        if (value.length == 20) {
            int bytes_sum = DataUtils.checkCode(value, 1, value.length);
            if (bytes_sum == value[0]) {
                int package_count = value[1];
                int package_current = value[2];
                int valid_data = value[3];

                if (package_current == package_count) {
                    byte[] bytes = new byte[valid_data];
                    System.arraycopy(value, 4, bytes, 0, valid_data);
                    dataBuffer.enqueue(bytes, bytes.length);

                    byte[] all = new byte[(package_count - 1) * 16 + valid_data];
                    dataBuffer.dequeue(all, all.length);
//                    if (onReceiveProgressListener != null){
//                        onReceiveProgressListener.onProgress(package_current*20,package_count*20);
//                    }
//                    onReceive(all);
                } else {
                    byte[] bytes = new byte[16];
                    System.arraycopy(value, 4, bytes, 0, 16);
                    dataBuffer.enqueue(bytes, bytes.length);
//                    if (onReceiveProgressListener != null){
//                        onReceiveProgressListener.onProgress(package_current*20,package_count*20);
//                    }
                }
            } else {
                throw new IllegalArgumentException("Parameter checksum error");
            }
        } else {
            throw new IllegalArgumentException("Parameter byte length error");
        }
    }

}
