package com.picovr.androidcollection.Utils.system;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

public class IMEUtils {
    public static final String TAG = IMEUtils.class.getSimpleName();
    private static final String sogouInput = "com.sohu.inputmethod.sogou.car/.CrossSogouIME";

    public static void setSogou(Context context) {
        setIme(context, sogouInput);
    }

    public static void setDefault(Context context) {
        setIme(context, null);
    }

    private static void setIme(Context context, String ime /**Null for default*/) {
        InputMethodManager inputMtdManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> inputMethodList = inputMtdManager.getInputMethodList();
        if (inputMethodList == null || inputMethodList.isEmpty()) {
            Log.w(TAG, "MainActivity, 没有输入法");
            return;
        }

        if (ime == null) {
            ime = inputMethodList.get(0).getId();
            setSecureIme(context, ime);
            return;
        }
        for (InputMethodInfo info : inputMethodList) {
            if (ime.equals(info.getId())) {
                setSecureIme(context, ime);
                break;
            }
        }
    }

    private static void setSecureIme(Context context, String ime) {
        String key = Settings.Secure.DEFAULT_INPUT_METHOD;
        Settings.Secure.putString(context.getContentResolver(), key, ime);
        Log.w(TAG, "IMEUtils, 设置输入法 %s" + ime);
    }
}