package com.coodev.androidcollection.Utils.io;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPUtils {
    /**
     * 简单的udp客户端
     * UDP理论数据最大长度是65507字节，实际往往是8192字节
     * UDP数据包应尽量保持为512字节或者更少，子防止数据被截取
     */
    public static class UDPClientSample {
        private InetAddress host;

        private int port;

        private int timeout;

        private int bufferSize;


        public UDPClientSample(InetAddress host, int port, int bufferSize, int timeout) {
            this.host = host;
            this.port = port;
            this.bufferSize = bufferSize;
            this.timeout = timeout;
            if (port < 2014 || port > 65535) {
                throw new IllegalArgumentException("Port out of range");
            }
        }

        public UDPClientSample(InetAddress host, int port, int bufferSize) {
            this(host, port, bufferSize, 30000);
        }

        public UDPClientSample(InetAddress host, int port) {
            this(host, port, 8092, 300000);
        }

        public byte[] poke() {
            try {
                DatagramSocket datagramSocket = new DatagramSocket(0);
                DatagramPacket datagramPacket = new DatagramPacket(new byte[1], 1, host, port);
                // connect不是真正意义上的连接，它指定了DatagramPacket只对远程主机和端口收发数据包
                // disconnect 也只是终端DatagramScoket的连接，从而可以再次正常收发任何主机和端口
                datagramSocket.connect(host, port);
                datagramSocket.setSoTimeout(timeout);
                datagramSocket.send(datagramPacket);


                DatagramPacket receivePacket = new DatagramPacket(new byte[bufferSize], bufferSize);
                datagramSocket.receive(receivePacket);

                byte[] bytes = new byte[receivePacket.getLength()];
                System.arraycopy(receivePacket.getData(), 0, bytes, 0, receivePacket.getLength());
                return bytes;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
