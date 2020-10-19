package com.picovr.androidcollection.Utils.net;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.picovr.androidcollection.Utils.common.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * @author patrick.ding
 * @since 19/11/15
 */
public class MacUtil {
    public static final String TAG = "MacUtil";

    private static final String MAC_KEY = "mac";

    private static final String MAC_DEFAULT = "02:00:00:00:00:00";
    private static final String MAC_DEFAULT_0 = "00:00:00:00:00:00";

    public static String getMac(Context context) {

        // 缓存
        if (context != null) {
            String mac = Utils.getSPvalue(context, MAC_KEY, "");
            if (macIsAvailable(mac)) {
                return mac;
            }
        }

        // 获取
        String mac = getMacNow(context);

        Log.i(TAG, "getMac# " + mac);

        if (context != null && macIsAvailable(mac)) {
            Utils.setSPvalue(context, MAC_KEY, mac);
        }
        return mac;
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
