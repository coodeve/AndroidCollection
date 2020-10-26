package com.coodev.androidcollection.Utils.dev.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;

/**
 * ble客户端，监听连接，收发数据
 * <p>
 * 主设备，即进行搜索和配对
 * <p>
 * 一个主设备理论上可以同时连接7个从设备
 * <p>
 * 客户端只能连接一个服务
 */
public class BleClient implements BleProtocol.IBleSend {
    public static final String TAG = BleClient.class.getSimpleName();
    private final BluetoothLeScanner mBluetoothLeScanner;
    private Context mContext;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    // 是否正在扫描
    private boolean isScanning = false;

    private int reconnect;

    private BluetoothGattCharacteristic mCharacteristic;
    /**
     * 已连接的设备
     */
    private BluetoothDevice mConnectedDevice;

    private BluetoothGatt mBluetoothGatt;

    /**
     * 扫描回调
     */
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BleClient.this.onScanResult(result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i(TAG, "onScanFailed: errorCode = " + errorCode);
            isScanning = false;
        }
    };

    /**
     * 连接读写回调
     */
    private BluetoothGattCallback mBluetoothGattServerCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    setConnectedDevice(gatt.getDevice());
                    gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                    // 查找已连接设备的服务
                    gatt.discoverServices();
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.close();
                        mBluetoothGatt = null;
                    }
                    if (mConnectedDevice != null) {
                        if (reconnect <= BleConfig.MAX_RECONNECT) {
                            reconnect++;
                            connect(mConnectedDevice);
                        }
                    }
                }

                onConnectStatus(newState);

            }
        }

        /**
         * 找到已连接设备的服务，可能会有多个
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered: ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 处理特定UUID的service
                BluetoothGattService service = gatt.getService(BleConfig.getServiceUuid());
                mCharacteristic = service.getCharacteristic(BleConfig.getCharacteristicUuid());
                mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead: ");
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicWrite: ");
        }

        /**
         * 开启notify后，接受服务发来的数据
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 通知处理
            read(characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {

        }

    };

    /**
     * BLE自定义协议
     */
    private BleProtocol bleProtocol = new BleProtocol();


    private void setConnectedDevice(BluetoothDevice device) {
        this.mConnectedDevice = device;
        reconnect = 0;
    }


    public BleClient(Context context) {
        mContext = context;
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    public void scan() {
        scan(SCAN_PERIOD, null);
    }

    public void scan(long time) {
        scan(time, null);
    }


    /**
     * 扫描设备
     *
     * @param time 扫描时长
     * @param list 特定设备类型
     */
    public void scan(long time, List<ScanFilter> list) {
        if (!isScanning) {
            isScanning = true;
            mBluetoothLeScanner.startScan(list, new ScanSettings.Builder().build(), mScanCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            }, time);
            return;
        }

        Log.w(TAG, "scan: bluetooth scan is running.");

    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        if (isScanning) {
            isScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
            return;
        }

        Log.w(TAG, "stopScan: no bluetooth scan is running");
    }

    /**
     * 连接设备
     *
     * @param bluetoothDevice
     */
    public void connect(BluetoothDevice bluetoothDevice) {
        mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mBluetoothGattServerCallback);
    }


    public void connect(String address) {
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(address);
        connect(remoteDevice);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mConnectedDevice = null;
        }
    }


    public void setBleProtocol(BleProtocol bleProtocol) {
        this.bleProtocol = bleProtocol;
    }

    public void setBleCallback(BleCallback bleCallback) {
        this.bleProtocol.setBleCallback(bleCallback);
    }

    /**
     * 写数据
     *
     * @param data
     * @return
     */
    public boolean write(String data) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new UnsupportedOperationException("write must be mainthread");
        }
        return bleProtocol.write(data);
    }


    /**
     * 读数据
     *
     * @param bytes
     */
    private void read(byte[] bytes) {
        bleProtocol.read(bytes);
    }

    public boolean sendBytes(byte[] bytes) {
        if (mBluetoothGatt == null || mCharacteristic == null || bytes == null) {
            return false;
        }
        mCharacteristic.setValue(bytes);
        return mBluetoothGatt.writeCharacteristic(mCharacteristic);
    }


    /**
     * 连接状态
     *
     * @param newState
     */
    private void onConnectStatus(int newState) {

    }


    /**
     * 扫描到的设备
     *
     * @param result
     */
    private void onScanResult(ScanResult result) {

    }

}
