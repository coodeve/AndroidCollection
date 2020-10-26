package com.coodev.androidcollection.Utils.dev.ble;

public interface IBleProtocol {
    boolean write(String data);

    void read(byte[] readBytes);

    boolean sendBytes(byte[] bytes);

    void onReceive(byte[] readBuffer, int start, int end);
}
