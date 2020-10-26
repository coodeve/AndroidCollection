package com.coodev.androidcollection.Utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;


/**
 *
 */
public class WifiUtils {


    public static final String TAG = "MacUtil";
    /**
     * 可能获取到的默认值
     */
    private static final String MAC_DEFAULT = "02:00:00:00:00:00";
    /**
     * 可能获取到的0值
     */
    private static final String MAC_DEFAULT_0 = "00:00:00:00:00:00";

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
     *
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


    private static String getMacNow(Context context) {
        String mac = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(context);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            mac = getMacAddress();
        } else {
            mac = getMacFromHardware();
            if (TextUtils.isEmpty(mac)) {
                mac = getMacDefault(context);
            }
        }
        return mac;
    }

    private static boolean macIsAvailable(String mac) {
        return !TextUtils.isEmpty(mac) && !MAC_DEFAULT.equals(mac) && !MAC_DEFAULT_0.equals(mac);
    }


    public static String getMac2(Context context) {
        String mac = "";
        mac = getMacByJavaAPI();
        Log.i(TAG, "getMac2# " + mac);
        return mac;
    }

    /**
     * Android 6.0 之前（不包括6.0）获取mac地址
     * 必须的权限 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     *
     * @param context * @return
     */
    public static String getMacDefault(Context context) {
        String mac = "";
        if (context == null) {
            return mac;
        }
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();

            if (info == null) {
                return null;
            }
            mac = info.getMacAddress();
            if (TextUtils.isEmpty(mac)) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }

    /**
     * Android 6.0-Android 7.0 获取mac地址
     */
    public static String getMacAddress() {
        String macSerial = "";
        String str = "";

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();//去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }

        return macSerial;
    }

    /**
     * Android 7.0之后获取Mac地址
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return
     */
    public static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString().toUpperCase();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    public static String getMacByJavaAPI() {
        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

            NetworkInterface networkInterface;
            do {
                if (!networkInterfaces.hasMoreElements()) {
                    return null;
                }

                networkInterface = (NetworkInterface) networkInterfaces.nextElement();
            }
            while (!"wlan0".equals(networkInterface.getName()) && !"eth0".equals(networkInterface.getName()));

            byte[] hardwareAddress = networkInterface.getHardwareAddress();
            if (hardwareAddress != null && hardwareAddress.length != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < hardwareAddress.length; ++i) {
                    stringBuilder.append(String.format("%02X:", hardwareAddress[i]));
                }

                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }

                return stringBuilder.toString().toLowerCase(Locale.getDefault());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
