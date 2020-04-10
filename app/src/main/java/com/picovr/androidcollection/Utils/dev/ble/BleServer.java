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
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ble服务端，监听连接，收发数据
 * <p>
 * 服务端即为从设备，发送广播和数据
 * <p>
 * ble的连接时GATT连接，一旦连接意味这独占，所以连接设备后就不会发出广播
 * 断开连接后会再次发出广播
 * <p>
 * profile->server->Characteristic->descriptor
 * <p>
 * 其中server，Characteristic，descriptor都通过UUID进行识别
 * 官方的UUID为16bit，需要购买
 * 自定的UUDI为128bit
 * <p>
 * BLE的特征一次读写最大长度20字节。
 *
 * <p>
 * 服务可以接受多个设备
 */
public class BleServer implements BleProtocol.IBleSend {
    public static final String TAG = BleServer.class.getSimpleName();
    private Context mContext;

    private final BluetoothManager mBluetoothManager;

    private final BluetoothAdapter mAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    // 用户配置
    private AbsAdvertiseSettings mAbsAdvertiseSettings;

    /**
     * 绑定设备列表
     */
    private List<BluetoothDevice> mBindBluetoothDevices = new ArrayList<>();

    private BluetoothGattServer mBluetoothGattServer;

    /**
     * BLE自定义协议
     */
    private BleProtocol bleProtocol = new BleProtocol();

    public void setBleProtocol(BleProtocol bleProtocol) {
        this.bleProtocol = bleProtocol;
    }

    public void setBleCallback(BleCallback bleCallback) {
        this.bleProtocol.setBleCallback(bleCallback);
    }

    /**
     * 广播创建的回调
     */
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "onStartSuccess: ");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.i(TAG, "onStartFailure: " + errorCode);
        }
    };

    /**
     * 服务监听回调
     */
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
            onConnectStatus(device, newState);
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

            if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                Log.d(TAG, "Subscribe device to notifications: " + device);
                // 将对应订阅此特征的设备加入列表
            } else if (Arrays.equals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, value)) {
                Log.d(TAG, "Unsubscribe device from notifications: " + device);
            }
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
        // 启动 GATT server，添加回调
        mBluetoothGattServer = mBluetoothManager.openGattServer(mContext, mBluetoothGattServerCallback);
        BluetoothGattService bluetoothGattService = generateDefaultService();
        mBluetoothGattServer.addService(bluetoothGattService);
    }

    /**
     * 创建一个默认的service
     *
     * @return
     */
    private BluetoothGattService generateDefaultService() {
        return AbsGattService.DEFALUT.getBluetoothGattService();
    }

    /**
     * 添加服务
     *
     * @param absGattService
     */
    public void addService(AbsGattService absGattService) {
        mBluetoothGattServer.addService(absGattService.getBluetoothGattService());
    }

    /**
     * 开始发送广播
     */
    public void startBleAdvertise() {
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mAdapter.getBluetoothLeAdvertiser();
        }

        mBluetoothLeAdvertiser.startAdvertising(
                mAbsAdvertiseSettings.buildSettings(),
                mAbsAdvertiseSettings.buildData(),
                mAdvertiseCallback);

    }

    /**
     * 停止广播
     */
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
    @Override
    public boolean sendBytes(byte[] bytes) {
        AbsGattService.DEFALUT.getBluetoothGattCharacteristic().setValue(bytes);
        return mBluetoothGattServer.notifyCharacteristicChanged(mBindBluetoothDevices.get(0), AbsGattService.DEFALUT.getBluetoothGattCharacteristic(), false);
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
        bleProtocol.read(value);
    }


    /**
     * 连接状态
     *
     * @param device
     * @param newState
     */
    private void onConnectStatus(BluetoothDevice device, int newState) {

    }

}
