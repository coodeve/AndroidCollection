package com.picovr.androidcollection.Utils.system;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class ClassUtil {
    private static final String TAG = ClassUtil.class.getSimpleName();

    /**
     * 获取某个包下的所有类
     *
     * @param context
     * @param packageName
     * @return
     */
    public static List<String> getClassName(Context context, String packageName) {
        List<String> classNameList = new ArrayList<String>();
        try {

            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> enumeration = df.entries();
            while (enumeration.hasMoreElements()) {
                String className = (String) enumeration.nextElement();
                // $是去除内部类
                if (className.contains(packageName) && !className.contains("$")) {
                    Log.i(TAG, "getClassName: " + className);
                    classNameList.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classNameList;
    }
}
