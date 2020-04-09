package com.picovr.androidcollection.Utils.dev.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
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

import java.util.ArrayList;
import java.util.List;

/**
 * ble客户端，监听连接，收发数据
 * 客户端只能连接一个服务
 */
public class BleClient {
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

    private BluetoothGattCharacteristic mCharacteristic;
    /**
     * 已连接的设备
     */
    private BluetoothDevice mConnectedDevice;

    private BluetoothGatt mBluetoothGatt;

    private List<BluetoothGattService> mBluetoothGattServers = new ArrayList<>();

    private DataBuffer dataBuffer = new DataBuffer(4096);

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


    private BluetoothGattCallback mBluetoothGattServerCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (status == BluetoothGatt.GATT_SUCCESS
                    && gatt.getDevice().getAddress().equals(mBluetoothGatt.getDevice().getAddress())) {
                setConnectedDevice(gatt.getDevice());
                mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                // 查找已连接设备的服务
                mBluetoothGatt.discoverServices();
            }
        }

        /**
         * 找到已连接设备的服务，可能会有多个
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(BleConfig.getServiceUuid());
                if (!mBluetoothGattServers.contains(service)) {
                    mBluetoothGattServers.add(service);
                }
                // TODO 现在这里是特定的UUID
                mCharacteristic = service.getCharacteristic(BleConfig.getCharacteristicUuid());
                mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 读取
                byte[] value = characteristic.getValue();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // 通知处理
            onReceive(characteristic.getValue());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    };


    private void setConnectedDevice(BluetoothDevice device) {
        this.mConnectedDevice = device;
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


    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mConnectedDevice = null;
        }
    }

    /**
     * 发送信息
     * 必须子线程调用
     *
     * @param bytes
     */
    private boolean sendBytes(byte[] bytes) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new UnsupportedOperationException("this method must call on mainThread");
        }
        if (mBluetoothGatt == null || mCharacteristic == null || bytes == null) {
            return false;
        }

        mCharacteristic.setValue(bytes);
        return mBluetoothGatt.writeCharacteristic(mCharacteristic);
    }


    public void send(byte[] src_bytes) {
        try {
            byte[] bytes = DataUtils.getData(src_bytes);
            int all_length = bytes.length;
            DataBuffer dataBuffer = new DataBuffer(all_length);
            dataBuffer.enqueue(bytes, all_length);
            for (int i = 0; i < all_length / 20; i++) {
                byte[] sends = new byte[20];
                dataBuffer.dequeue(sends, 20);
                //此处需要200ms间隔，降低丢包几率
                Thread.sleep(200);
                boolean isSend = sendBytes(sends);
                // todo 进度监听

            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描到的设备
     *
     * @param result
     */
    private void onScanResult(ScanResult result) {

    }


    /**
     * 获取到信息
     *
     * @param value
     */
    private void onReceive(byte[] value) {
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
                    // todo  进度监听

                    onReceive(all);
                } else {
                    byte[] bytes = new byte[16];
                    System.arraycopy(value, 4, bytes, 0, 16);
                    dataBuffer.enqueue(bytes, bytes.length);
                    // todo  进度监听

                }
            } else {
                throw new IllegalArgumentException("Parameter checksum error");
            }
        } else {
            throw new IllegalArgumentException("Parameter byte length error");
        }
    }
}
