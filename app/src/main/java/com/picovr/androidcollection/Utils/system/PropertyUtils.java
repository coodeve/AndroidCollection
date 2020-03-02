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


}
