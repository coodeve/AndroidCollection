package com.picovr.androidcollection.widget.x5;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @author patrick.ding
 * @since 19/9/17
 */
public class PvrAction extends Action {
    public static final String TAG = "PvrAction";

    private Map<String, String> params = new HashMap<>();

    private String PARAMS_URL_KEY = "url";

    private String PARAMS_ORENTATION_KEY = "orientation";

    public PvrAction(Uri uri) {
        super(uri);
    }

    @Override
    boolean action(String key, String value) {
        return false;
    }

    @Override
    String getOritation() {
        analyData(uri.toString());
        return params.get(PARAMS_ORENTATION_KEY);
    }

    @Override
    String getUrl() {
        return params.get(PARAMS_URL_KEY);
    }

    private void analyData(String url) {
        if (TextUtils.isEmpty(url)) {
            Log.w(TAG, "analyData# url is null");
            return;
        }
        // 获取url字段,url字段放在最后，
        // 以防止orientation字段作为了url字段中的参数
        int urlIndex = url.indexOf(PARAMS_URL_KEY + "=");
        if (urlIndex != -1) {
            params.put(PARAMS_URL_KEY, url.substring(urlIndex + PARAMS_URL_KEY.length() + 1));
        }
        // 获取orientation字段
        int orIndex = url.indexOf(PARAMS_ORENTATION_KEY + "=");
        if (orIndex != -1) {
            int lastIndex = 0;
            if ((lastIndex = url.indexOf("&", orIndex)) == -1) {
                lastIndex = url.length();
            }
            params.put(PARAMS_ORENTATION_KEY, url.substring(orIndex + PARAMS_ORENTATION_KEY.length() + 1, lastIndex));
        }
    }
}
