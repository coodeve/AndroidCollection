package com.picovr.androidcollection.Utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;


/**
 *
 */
public class WifiUtils {

    private static WifiManager getWifiManager(Context context) {
        Context contextRef = new WeakReference<Context>(context).get();
        return (WifiManager) contextRef.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static String getWiFiIp(Context context) {
        String ip = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            ip = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                    + "." + (i >> 24 & 0xFF);
        }
        return ip;
    }


    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        Log.d("IP Address", "IP:" + sb.toString());
        return sb.toString();
    }

    /**
     * 获取广播地址
     *
     * @param context
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getBroadcastAddress(Context context) throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (isWifiApEnabled(wifiManager)) {
            return InetAddress.getByName("192.168.43.255");
        }
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        if (dhcp == null) {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    /**
     * wifi是否可用
     *
     * @param wifiManager
     * @return
     */
    public static Boolean isWifiApEnabled(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * wifi是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWiFiOpen(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 获取局域网络的ip地址，如果需要获取公网ip地址，需要访问对应功能网页
     *
     * @return
     */
    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && ip instanceof Inet4Address) {
                        Log.i("IP Address : ", ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            Log.e("IP Address", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;

    }

    public static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivity.getActiveNetworkInfo();
    }

    public static NetworkInfo getNetworkInfo(Context context, int networkType) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(networkType);
    }

    public static int getNetworkType(Context context) {
        if (context != null) {
            NetworkInfo info = getActiveNetworkInfo(context);

            return info == null ? -1 : info.getType();
        }

        return -1;
    }

    /**
     *
     * @param context
     * @return
     */
    public static int getWifiState(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifi == null) {
            return 4;
        }

        return wifi.getWifiState();
    }

    public static NetworkInfo.DetailedState getWifiConnectivityState(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context, 1);
        return networkInfo == null ? NetworkInfo.DetailedState.FAILED : networkInfo.getDetailedState();
    }

    /**
     * 连接某个wifi
     * @param context
     * @param wifiSSID
     * @param password
     * @return
     */
    public static boolean wifiConnection(Context context, String wifiSSID, String password) {
        boolean isConnection = false;
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String strQuotationSSID = "\"" + wifiSSID + "\"";

        WifiInfo wifiInfo = wifi.getConnectionInfo();
        if ((wifiInfo != null) && (
                (wifiSSID.equals(wifiInfo.getSSID())) || (strQuotationSSID.equals(wifiInfo.getSSID())))) {
            isConnection = true;
        } else {
            List scanResults = wifi.getScanResults();
            if ((scanResults != null) && (scanResults.size() != 0)) {
                for (int nAllIndex = scanResults.size() - 1; nAllIndex >= 0; nAllIndex--) {
                    String strScanSSID = ((ScanResult) scanResults.get(nAllIndex)).SSID;
                    if ((wifiSSID.equals(strScanSSID)) || (strQuotationSSID.equals(strScanSSID))) {
                        WifiConfiguration config = new WifiConfiguration();
                        config.SSID = strQuotationSSID;
                        config.preSharedKey = ("\"" + password + "\"");
                        config.status = 2;

                        int nAddWifiId = wifi.addNetwork(config);
                        isConnection = wifi.enableNetwork(nAddWifiId, false);
                        break;
                    }
                }
            }
        }

        return isConnection;
    }
}
