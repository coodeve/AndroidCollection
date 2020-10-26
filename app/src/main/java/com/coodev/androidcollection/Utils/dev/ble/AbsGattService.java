package com.coodev.androidcollection.Utils.dev.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public abstract class AbsGattService {

    protected BluetoothGattService mBluetoothGattService;

    protected BluetoothGattCharacteristic mBluetoothGattCharacteristic;

    /**
     * 服务的uuid
     *
     * @return
     */
    protected abstract UUID getServiceUUID();

    /**
     * Characteristic的uuid
     *
     * @return
     */
    protected abstract UUID getCharacteristicUUID();

    /**
     * 服务的实例
     *
     * @return
     */
    protected abstract BluetoothGattService createBluetoothGattService();


    public BluetoothGattService getBluetoothGattService() {
        return mBluetoothGattService;
    }

    public BluetoothGattCharacteristic getBluetoothGattCharacteristic() {
        return mBluetoothGattCharacteristic;
    }


    /**
     * 默认值
     */
    public final static AbsGattService DEFALUT = new DefalutGattService();

    public static class DefalutGattService extends AbsGattService {

        @Override
        protected UUID getServiceUUID() {
            return BleConfig.getServiceUuid();
        }

        @Override
        protected UUID getCharacteristicUUID() {
            return BleConfig.getCharacteristicUuid();
        }

        @Override
        protected BluetoothGattService createBluetoothGattService() {
            // 创建一个service
            mBluetoothGattService = new BluetoothGattService(BleConfig.getServiceUuid(), BluetoothGattService.SERVICE_TYPE_PRIMARY);
            // 创建Characteristic，此处不需要descriptor
            mBluetoothGattCharacteristic = new BluetoothGattCharacteristic(BleConfig.getCharacteristicUuid(),
                    BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
            // 组合
            mBluetoothGattService.addCharacteristic(mBluetoothGattCharacteristic);
            return mBluetoothGattService;
        }
    }

}
