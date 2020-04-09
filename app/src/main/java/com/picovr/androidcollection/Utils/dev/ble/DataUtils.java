package com.picovr.androidcollection.Utils.dev.ble;


public class DataUtils {

    /**
     * 获取字符串转出的带协议的byte数组
     * @param _bytes    想要发送的字节（byte字节数小于4096）
     * @return
     */
    public static byte[] getData(byte[] _bytes){
        if (_bytes.length > 4096){
            throw new IllegalArgumentException("Parameter exceeds 4096 bytes");
        }
        int c = (_bytes.length)/16;
        int y = (_bytes.length)%16;
        byte[] result = new byte[20*(c+1)];
        for (int i = 0; i < c+1; i++) {
            byte[] bytes = new byte[20];
            bytes[1] = int2Bytes(c+1);
            bytes[2] = int2Bytes(i+1);
            if (i == c){
                bytes[3] = int2Bytes(y);
                System.arraycopy(_bytes,16*i,bytes,4,y);
            }else {
                bytes[3] = 0x10;
                System.arraycopy(_bytes,16*i,bytes,4,16);
            }
            bytes[0] = checkCode(bytes,1,bytes.length);
            System.arraycopy(bytes,0,result,20*i,20);
        }
        return result;
    }

    /**
     * 计算校验和(算前不算后)
     * @param b        接收到的字节数组
     * @param start    起始index
     * @param end      截止index
     * @return
     */
    public static byte checkCode(byte[] b,int start,int end){
        short s=0;
        for (int i = start; i < end; i++) {
            s+=b[i];
        }
        byte bt=(byte)(s & 0xFF);
        return bt;
    }

    /**
     * int值转byte(0～255范围)
     * @param value
     * @return
     */
    private static byte int2Bytes(int value){
        byte b = (byte) value;
        if (b < 0){
            b = (byte) (b +256);
        }
        return b;
    }


}
