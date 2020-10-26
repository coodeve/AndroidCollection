package com.coodev.androidcollection.Utils.dev.ble;

import java.util.UUID;

public class BleConfig {

    /* Current Time Service UUID */
    private final static UUID UUID_SERVICE = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb");
    /* Mandatory Current Time Information Characteristic */
    private final static UUID UUID_CHARACTERISTIC = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb");



    /**
     * 默认服务的UUID
     *
     * @return
     */
    public static UUID getServiceUuid() {
        return UUID_SERVICE;
    }

    /**
     * 默认Characteristic的UUID
     *
     * @return
     */
    public static UUID getCharacteristicUuid() {
        return UUID_CHARACTERISTIC;
    }


    public static int MAX_RECONNECT = 2;

}
