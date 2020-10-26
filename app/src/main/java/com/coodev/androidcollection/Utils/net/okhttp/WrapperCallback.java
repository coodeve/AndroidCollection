package com.coodev.androidcollection.Utils.net.okhttp;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 将数据切换到主线程
 */
public class WrapperCallback implements Callback {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private Callback mCallback;

    public WrapperCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallback.onFailure(call, e);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mCallback.onResponse(call, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
