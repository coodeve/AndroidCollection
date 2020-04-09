package com.picovr.androidcollection.Utils.dev.ble;

import java.util.UUID;

public class BleConfig {
    /**
     * service uuid
     */
    public static UUID UUID_SERVICE;

    /**
     * characteristic uuid
     */
    public static UUID UUID_CHARACTERISTIC;


    public static int MAX_RECONNECT_COUNT = -1;

    /**
     * 修改默认值
     * @param uuidService
     * @param uuidCharacteristic
     * @param reconnectCount
     */
    public static void init(UUID uuidService, UUID uuidCharacteristic, int reconnectCount) {
        UUID_SERVICE = uuidService;
        UUID_CHARACTERISTIC = uuidCharacteristic;
        MAX_RECONNECT_COUNT = reconnectCount;
    }

    public static UUID getServiceUuid(){
        if (UUID_SERVICE == null){
            return UUID.fromString("00001000-0000-1000-8000-00805f9b34fb");
        }else {
            return UUID_SERVICE;
        }
    }

    public static UUID getCharacteristicUuid(){
        if (UUID_CHARACTERISTIC == null){
            return UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        }else {
            return UUID_CHARACTERISTIC;
        }
    }

    public static int getMaxReconnectCount(){
        if (MAX_RECONNECT_COUNT == -1){
            return Integer.MAX_VALUE;
        }else {
            return MAX_RECONNECT_COUNT;
        }
    }

}
