package com.picovr.androidcollection.Utils.net.okhttp;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 将数据切换到主线程
 */
public abstract class StringCallback implements Callback {

    @Override
    public void onFailure(Call call, IOException e) {
        try {
            onFailure(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
            onResponse(response.body().string());
        } else {
            onFailure(new IOException(response.toString()));
        }

    }

    public abstract void onFailure(IOException e) throws IOException;

    public abstract void onResponse(String result) throws IOException;

}
