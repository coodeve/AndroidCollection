package com.coodev.androidcollection.Utils.dev.ble;

import android.os.SystemClock;
import android.util.Log;

/**
 * Ble 单次发送最好不超过20字节
 * 设计已数据帧进行发送，软件进行分包
 * <p>
 * 一帧内容：
 * 第0个字节：校验值
 * 第1个字节：总包数
 * 第2个字节：当前第几包
 * 第3个字节：数据长度
 * 第4~20个字节：数据填充，不满20个用0补齐
 * <p>
 * 以上协议，意味着一个数据，最大是255*16 个字节大小，即4096个字节
 */
public class BleProtocol {

    public static final String TAG = BleProtocol.class.getSimpleName();

    public static interface IBleSend {
        boolean sendBytes(byte[] bytes);
    }

    /**
     * 发送总的最大数据量
     */
    private static final int MAX_SIZE = 4096;

    /**
     * 数据帧最大有效数据容量
     */
    private static final int DATA_CAPACITY = 16;

    private static final int FRAME_CAPACITY = 20;

    private static final int HEAD_CHECK = 0;

    private static final int HEAD_PACKAGES = 1;

    private static final int HEAD_INDEX = 2;

    private static final int HEAD_LENGTH = 3;

    private static final int DATA_HEAD = 4;
    /**
     * 读取数据的缓冲区
     */
    private byte[] readBuffer = new byte[MAX_SIZE];

    private BleCallback mBleCallback;

    private IBleSend mIBleSend;

    /**
     * 设置回调监听
     *
     * @param bleCallback
     */
    public void setBleCallback(BleCallback bleCallback) {
        this.mBleCallback = bleCallback;
    }

    protected void setIBleSend(IBleSend iBleSend) {
        this.mIBleSend = iBleSend;
    }

    public boolean write(String data) {
        if (null == data || "".equals(data)) {
            Log.w(TAG, "write: data is empty");
            return false;
        }

        byte[] sourceBytes = data.getBytes();

        if (sourceBytes.length > MAX_SIZE) {
            Log.w(TAG, "write: data capacity over 4096 size");
            return false;
        }

        byte[] buffer = new byte[FRAME_CAPACITY];

        if (sourceBytes.length <= DATA_CAPACITY) {
            buffer[HEAD_PACKAGES] = int2Bytes(1);
            buffer[HEAD_INDEX] = int2Bytes(0);
            buffer[HEAD_LENGTH] = int2Bytes(sourceBytes.length);
            System.arraycopy(sourceBytes, 0, buffer, 0, sourceBytes.length);
            if (!mIBleSend.sendBytes(sourceBytes) && mBleCallback != null) {
                mBleCallback.error(BleCallback.TYPE_WRITE, 0);
            }
            return true;
        }

        int frames = 0;
        int lastFrameDataLength = sourceBytes.length % DATA_CAPACITY;
        frames = sourceBytes.length / DATA_CAPACITY + (lastFrameDataLength == 0 ? 0 : 1);


        int validDataLength = 0;
        int sendDataSum = 0;
        for (int i = 0; i < frames; i++) {
            validDataLength = (sourceBytes.length - i * DATA_CAPACITY) >= DATA_CAPACITY ? DATA_CAPACITY : lastFrameDataLength;
            buffer[HEAD_PACKAGES] = int2Bytes(frames);
            buffer[HEAD_INDEX] = int2Bytes(i);
            buffer[HEAD_LENGTH] = int2Bytes(validDataLength);
            System.arraycopy(sourceBytes, i * DATA_CAPACITY, buffer, 0, validDataLength);
            buffer[HEAD_CHECK] = checkCode(buffer, 1, buffer.length);
            // 发送并回调
            if (!mIBleSend.sendBytes(sourceBytes) && mBleCallback != null) {
                mBleCallback.error(BleCallback.TYPE_WRITE, 0);
                break;
            }
            sendDataSum += validDataLength;
            if (mBleCallback != null) {
                mBleCallback.process(BleCallback.TYPE_WRITE, sendDataSum, sourceBytes.length);
            }
            // 减少丢包
            SystemClock.sleep(100);

        }

        buffer = null;
        sourceBytes = null;

        return true;
    }


    public void read(byte[] readBytes) {

        if (readBytes.length != FRAME_CAPACITY) {
            Log.w(TAG, "read: invalid data , frame size voer 20 bytes!");
            return;
        }

        int checkCode = checkCode(readBytes, 1, readBytes.length);
        if (checkCode != readBytes[HEAD_CHECK]) {
            Log.w(TAG, "read: invalid data , the data is corrupted");
            return;
        }

        int totalFrame = readBytes[HEAD_PACKAGES];
        int currFrame = readBytes[HEAD_INDEX];
        int dataLength = readBytes[HEAD_LENGTH];

        System.arraycopy(readBytes, DATA_HEAD, readBuffer, currFrame * DATA_CAPACITY, dataLength);
        if (mBleCallback != null) {
            mBleCallback.process(BleCallback.TYPE_READ, currFrame * DATA_CAPACITY, totalFrame * DATA_CAPACITY);
        }
        if (totalFrame - 1 == currFrame) {
            // 最后的数据帧
            int validDataLength = (totalFrame - 1) * DATA_CAPACITY + dataLength;
            if (mBleCallback != null) {
                mBleCallback.compelete(BleCallback.TYPE_READ, readBuffer, 0, validDataLength);
            }
        }

    }


    /**
     * 计算校验和(算前不算后)
     *
     * @param b     接收到的字节数组
     * @param start 起始index
     * @param end   截止index
     * @return
     */
    public static byte checkCode(byte[] b, int start, int end) {
        short s = 0;
        for (int i = start; i < end; i++) {
            s += b[i];
        }
        byte bt = (byte) (s & 0xFF);
        return bt;
    }

    /**
     * int值转byte(0～255范围)
     *
     * @param value
     * @return
     */
    private static byte int2Bytes(int value) {
        return (byte) (value & 0xFF);
    }

    private static int byte2int(byte value) {
        return value & 0xFFFF;
    }
}
