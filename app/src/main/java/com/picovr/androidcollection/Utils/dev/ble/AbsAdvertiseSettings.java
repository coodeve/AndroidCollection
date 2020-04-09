package com.picovr.androidcollection.Utils.dev.ble;

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

    public static class DefaultAbsAdvertiseSettings extends AbsAdvertiseSettings {

        private String advertiseData = "hello from servser";

        @Override
        protected AdvertiseSettings buildSettings() {
            return new AdvertiseSettings.Builder()
                    /*
                    ADVERTISE_MODE_LOW_LATENCY 100ms
                    ADVERTISE_MODE_LOW_POWER 1s(默认)
                    ADVERTISE_MODE_BALANCED  250ms
                    */
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    /*
                    ADVERTISE_TX_POWER_ULTRA_LOW
                    ADVERTISE_TX_POWER_LOW
                    ADVERTISE_TX_POWER_MEDIUM(默认)
                    ADVERTISE_TX_POWER_HIGH
                    */
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .build();
        }

        @Override
        protected AdvertiseData buildData() {
            AdvertiseData.Builder builder = new AdvertiseData.Builder();
            builder.addServiceUuid(new ParcelUuid(BleConfig.getServiceUuid()));
            builder.setIncludeDeviceName(true);
            builder.addServiceData(new ParcelUuid(BleConfig.getServiceUuid()), advertiseData.getBytes());
            return builder.build();
        }
    }


    public final static AbsAdvertiseSettings DEFAULT = new DefaultAbsAdvertiseSettings();


}
