package com.picovr.androidcollection.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by PICO-USER Dragon on 2018/3/6.
 */

public class WifiUtils {

    public static String getWiFiIp(Context context) {
        String ip = null;
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            ip = (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                    + "." + (i >> 24 & 0xFF);
        }
        return ip;
    }

    private static WifiManager getWifiManager(Context context) {
        Context contextRef = new WeakReference<Context>(context).get();
        return (WifiManager) contextRef.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static String getIPAddress(Context context) {
        WifiManager wifiManager = getWifiManager(context);
        if (isWifiOn(context)) {
            int ipAsInt = wifiManager.getConnectionInfo().getIpAddress();
            if (ipAsInt == 0) {
                return null;
            } else {
                InetAddress inetAddress = intToInet(ipAsInt);
                return (inetAddress == null) ? null : inetAddress.getHostAddress();
            }
        } else {
            return null;
        }
    }




    public static boolean isWifiOn(Context context) {
        WifiManager wifiMnger = getWifiManager(context);
        int wifiState = wifiMnger.getWifiState();
        Context ctx = new WeakReference<>(context).get();
        ConnectivityManager connMngr = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            NetworkInfo.State state = connMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (NetworkInfo.State.CONNECTED == state) {
                return true;
            }
        }
        return false;
    }

    private static InetAddress intToInet(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = byteOfInt(value, i);
        }
        try {
            return InetAddress.getByAddress(bytes);
        } catch (UnknownHostException e) {
            // This only happens if the byte array has a bad length
            return null;
        }
    }

    private static byte byteOfInt(int value, int which) {
        int shift = which * 8;
        return (byte) (value >> shift);
    }

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

    public static Boolean isWifiApEnabled(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isWiFiOpen(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
