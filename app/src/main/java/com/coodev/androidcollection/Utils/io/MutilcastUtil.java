package com.coodev.androidcollection.Utils.io;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 组播
 * ipv4组播地址：224.0.0.0~239.255.255.255
 * ipv6组播地址：ff00::/8
 * <p>
 * 查看你的网络是否连接这一个具有组播功能的组播路由器
 * ping all-routers.mcast.net
 * <p>
 * 使用：
 * 需要创建一个组播组：从224.0.0.0~239.255.255.255中随机选择一个并向它发送数据
 * <p>
 * 组播的TTL默认是1，也就是说不会传到子网之外
 */
public class MutilcastUtil {

    /**
     * 组播
     * 收发方式和UDP是一样的
     *
     * @param host 必须是224.0.0.0~239.255.255.255中的一个，否则异常
     * @param port
     */
    public static void createMutilcast(String host, int port) {
        InetAddress byName = null;
        MulticastSocket ms = null;
        try {
            byName = InetAddress.getByName(host);
            ms = new MulticastSocket(port);
            ms.joinGroup(byName);// 加入组播组

            byte[] buffer = new byte[8192];
            while (true) {
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                ms.receive(datagramPacket);
                System.out.println(new String(buffer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ms != null) {
                try {
                    ms.leaveGroup(byName);// 离开组播组
                    ms.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
