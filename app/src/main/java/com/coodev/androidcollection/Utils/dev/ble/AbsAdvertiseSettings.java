package com.coodev.androidcollection.Utils.dev.ble;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.os.ParcelUuid;

/**
 * 用户广播配置
 */
public abstract class AbsAdvertiseSettings {
    /**
     * 广播设置
     *
     * @return
     */
    protected abstract AdvertiseSettings buildSettings();

    /**
     * 广播数据
     *
     * @return
     */
    protected abstract AdvertiseData buildData();


    /**
     * 默认
     */
    public final static AbsAdvertiseSettings DEFAULT = new DefaultAbsAdvertiseSettings();

    public static class DefaultAbsAdvertiseSettings extends AbsAdvertiseSettings {
        /**
         * 自定义广播数据
         */
        private String advertiseData = "Hello from BleServser";

        @Override
        protected AdvertiseSettings buildSettings() {
            return new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .build();
        }

        @Override
        protected AdvertiseData buildData() {
            AdvertiseData.Builder builder = new AdvertiseData.Builder()
                    .addServiceUuid(new ParcelUuid(BleConfig.getServiceUuid()))
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(false)
                    .addServiceData(new ParcelUuid(BleConfig.getServiceUuid()), advertiseData.getBytes());
            return builder.build();
        }
    }


}
