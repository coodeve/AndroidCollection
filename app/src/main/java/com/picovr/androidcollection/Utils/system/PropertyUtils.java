package com.picovr.androidcollection.Utils.system;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;

/**
 * @author patrick.ding
 * @since 20/2/17
 */
public class PropertyUtils {

    /**
     * 获取属性值
     * @param propName
     * @return
     */
    public static String getSysProperty(String propName) {
        Class<?> classType = null;
        String buildVersion = "";
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            buildVersion = (String) getMethod.invoke(classType, new Object[]{propName});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buildVersion;
    }

    /**
     * 写入属性值
     * @param name
     * @param value
     */
    private static void setSysProperty(String name, String value) {
        Class<?> classType = null;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method setMethod = classType.getDeclaredMethod("set", new Class<?>[]{String.class, String.class});
            setMethod.invoke(classType, new Object[]{name, value});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the current networking
     *
     * @param context
     * @return WIFI or MOBILE
     */
    public static String getNetworkType(Context context) {
        TelephonyManager manager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int type = manager.getNetworkType();
        String typeString = "UNKNOWN";
        if (type == TelephonyManager.NETWORK_TYPE_CDMA) {
            typeString = "CDMA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
            typeString = "EDGE";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
            typeString = "EVDO_0";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
            typeString = "EVDO_A";
        }
        if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
            typeString = "GPRS";
        }
        if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
            typeString = "HSDPA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_HSPA) {
            typeString = "HSPA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_HSUPA) {
            typeString = "HSUPA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
            typeString = "UMTS";
        }
        if (type == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
            typeString = "UNKNOWN";
        }
        if (type == TelephonyManager.NETWORK_TYPE_1xRTT) {
            typeString = "1xRTT";
        }
        if (type == 11) {
            typeString = "iDen";
        }
        if (type == 12) {
            typeString = "EVDO_B";
        }
        if (type == 13) {
            typeString = "LTE";
        }
        if (type == 14) {
            typeString = "eHRPD";
        }
        if (type == 15) {
            typeString = "HSPA+";
        }

        return typeString;
    }
}
