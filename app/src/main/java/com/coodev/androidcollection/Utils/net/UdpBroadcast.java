package com.coodev.androidcollection.Utils.net;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * udp广播
 */
public class UdpBroadcast {

    /**
     * udp发送
     * @param context
     * @param content    内容
     * @param port       规定端口，与接收一致
     * @param times      一次调用发送几次内容
     * @throws IOException
     */
    public static void sendUdp(Context context, String content, int port, int times) throws IOException {
        WifiManager wifiMgr = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

        //获取IP地址，int类型的
        int ip = wifiInfo.getIpAddress();
        //将本机的IP地址转换成xxx.xxx.xxx.255
        int broadCastIP = ip | 0xFF000000;

        DatagramSocket theSocket = null;
        try {
            for (int i = 0; i < times; i++) {
                InetAddress server = InetAddress.getByName(Formatter.formatIpAddress(broadCastIP));
                theSocket = new DatagramSocket();

                DatagramPacket theOutput = new DatagramPacket(content.getBytes(), content.length(), server, port);
                theSocket.send(theOutput);
            }
        } finally {
            if (theSocket != null) {
                theSocket.close();
            }
        }
    }

    /**
     * udp接收
     * @param port       规定端口，与发送一致
     * @param length     发送/接收内容的长度
     * @return
     * @throws IOException
     */
    private static void receive(int port, int length, OnUdpReceiveListener listener) throws IOException {
        byte[] buffer = new byte[length];
        DatagramSocket server = null;
        try {
            server = new DatagramSocket(port);
            DatagramPacket packet = new DatagramPacket(buffer, length);
            while(true){
                server.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength(), "UTF-8");

                if (!TextUtils.isEmpty(data)){
                   if (listener != null){
                       listener.onReceive(data);
                   }
                }
            }
        } finally{
            if(server != null) {
                server.close();
            }
        }
    }


    /**
     * udp接收监听
     */
    public interface OnUdpReceiveListener {

        /**
         * 接收
         * @param receive 接收内容
         */
        void onReceive(String receive);

    }

}
