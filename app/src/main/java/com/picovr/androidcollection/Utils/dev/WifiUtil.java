package com.picovr.androidcollection.Utils.dev;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.picovr.androidcollection.Utils.net.WifiUtils;

import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class WifiUtil {

    public class WifiBean {
        /**
         * wpa
         */
        public static final int Type_Wpa = 1;
        /**
         * wep
         */
        public static final int Type_Wep = 2;
        /**
         * 无密码
         */
        public static final int Type_None = 3;

        private int type;
        private String ssid;

        public WifiBean(int type, String ssid) {
            this.type = type;
            this.ssid = ssid;
        }

    }

    private WifiManager mWifiManager;

    private volatile static WifiUtil instance = null;

    private String mCurrentSSID;

    private WifiUtil(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
    }

    public static WifiUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiUtils.class) {
                if (instance == null) {
                    instance = new WifiUtil(context);
                }
            }
        }
        return instance;
    }


    /**
     * 获得wifiManager
     *
     * @return
     */
    public WifiManager getWifiManager() {
        return mWifiManager;
    }


    public void getIntentFilter() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // WiFi扫描结束时系统会发送该广播，用户可以监听该广播通过调用WifiManager的getScanResults方法来获取到扫描结果
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        //密码错误的广播
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
    }

    /**
     * Wi-Fi是否有密码
     *
     * @param result
     * @return
     */
    public int wifiHasPsd(ScanResult result) {
        String capabilities = result.capabilities;
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                return WifiBean.Type_Wpa;
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                return WifiBean.Type_Wep;
            } else {
                return WifiBean.Type_None;
            }
        }
        return WifiBean.Type_None;
    }

    /**
     * 连接wifi
     *
     * @param wifiName wifi名称
     * @param wifiPwd  wifi密码
     * @return
     */
    public boolean connectWifi(String wifiName, String wifiPwd) {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        mCurrentSSID = "\"" + wifiName + "\"";
        removeCurrentWifi();

        int type = TextUtils.isEmpty(wifiPwd) ? WifiBean.Type_None : WifiBean.Type_Wpa;
        WifiConfiguration wifiConfig = createWifiInfo(wifiName, wifiPwd, type);
        int networkId = mWifiManager.addNetwork(wifiConfig);

        return connectWifi(wifiConfig, networkId);
    }

    /**
     * 移除wifi
     *
     * @param mSsid
     */
    private void removeWifi(String mSsid) {
        List<WifiConfiguration> wifiConfigs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
            if (ssid.equals(mSsid)) {
                removeWifi(wifiConfig);
            } else {
                mWifiManager.disableNetwork(wifiConfig.networkId);
            }
        }
    }

    /**
     * 移除wifi
     *
     * @param wifiConfig
     */
    private void removeWifi(WifiConfiguration wifiConfig) {
        mWifiManager.disableNetwork(wifiConfig.networkId);
        mWifiManager.disconnect();
        mWifiManager.removeNetwork(wifiConfig.networkId);
        mWifiManager.updateNetwork(wifiConfig);
    }

    /**
     * 移除当前保留wifi记录
     */
    public void removeCurrentWifi() {
        if (TextUtils.isEmpty(mCurrentSSID)) {
            return;
        }
        removeWifi(mCurrentSSID);
    }

    /***
     * 配置要连接的WIFI热点信息
     * @param SSID
     * @param password
     * @param type  加密类型
     * @return
     */
    private WifiConfiguration createWifiInfo(String SSID, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        // 分为三种情况：没有密码   用wep加密  用wpa加密
        if (type == WifiBean.Type_None) {
            //config.wepKeys[0] = "\"" + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex = 0;
        } else if (type == WifiBean.Type_Wep) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WifiBean.Type_Wpa) {
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    /**
     * 连接wifi
     *
     * @param wifiConfig
     * @param networkId
     * @return
     */
    private boolean connectWifi(WifiConfiguration wifiConfig, int networkId) {
        if (networkId == -1) {
            Log.d("WifiUtils", "操作失败,需要您到手机wifi列表中取消对设备连接的保存");
            removeWifi(wifiConfig);
            return false;
        } else {
            return mWifiManager.enableNetwork(networkId, true);
        }
    }

    /**
     * 获取当前wifi名称
     *
     * @return
     */
    public String getWifiName() {
        WifiInfo info = mWifiManager.getConnectionInfo();
        String wifi_ssid = info != null ? info.getSSID() : null;
        if (!TextUtils.isEmpty(wifi_ssid)) {
            String wifiName = wifi_ssid.substring(1, wifi_ssid.length() - 1);
            return wifiName;
        } else {
            return "";
        }
    }

    /**
     * 通过反射得连接wifi的方法
     *
     * @param netId
     * @return
     */
    private Method connectWifiById(int netId) {
        Method connectMethod = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (Method methodSub : mWifiManager.getClass()
                    .getDeclaredMethods()) {
                if ("connect".equalsIgnoreCase(methodSub.getName())) {
                    Class<?>[] types = methodSub.getParameterTypes();
                    if (types != null && types.length > 0) {
                        if ("int".equalsIgnoreCase(types[0].getName())) {
                            connectMethod = methodSub;
                        }
                    }
                }
            }
            if (connectMethod != null) {
                try {
                    connectMethod.invoke(mWifiManager, netId, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        } else {
            return null;
        }
        return connectMethod;
    }

    /**
     * 通过反射得忘记wifi配置的方法
     *
     * @param netId
     */
    private void forgetNetwork(int netId) {
        try {
            Method forget = mWifiManager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (forget != null) {
                forget.setAccessible(true);
                forget.invoke(mWifiManager, netId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描wifi
     */
    public void scanWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.startScan();
        } else {
            mWifiManager.setWifiEnabled(true);
        }
    }
}
