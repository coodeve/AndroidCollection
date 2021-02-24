package com.coodev.androidcollection.Utils.net.okhttp;

import android.util.Log;

import com.coodev.androidcollection.Utils.net.SSLManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;

public class CooHttp {
    public static final String TAG = CooHttp.class.getSimpleName();

    private static final int TIME_OUT_READ = 10;

    private static final int TIME_OUT_CONNECT = 10;

    private static OkHttpClient mOkHttpClient;

    public static class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            Log.i(TAG, message);
        }
    }


    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_READ, TimeUnit.SECONDS)
                .followRedirects(true)
                .retryOnConnectionFailure(true)
                .hostnameVerifier(SSLManager.getEmptyHostNameVerifier())// https的支持
                .sslSocketFactory(SSLManager.getSSLContextNone().getSocketFactory(), SSLManager.getEmptyTrustManager());// https的支持

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        builder.addNetworkInterceptor(httpLoggingInterceptor);


    }


    public static OkHttpClient getOkhttpClient() {
        return mOkHttpClient;
    }

    /**
     * 加载拦截器
     *
     * @param interceptor
     */
    public static void addInterceptor(Interceptor interceptor) {
        mOkHttpClient = mOkHttpClient.newBuilder().addInterceptor(interceptor).build();
    }

    /**
     * 加载网路拦截器
     *
     * @param interceptor
     */
    public static void addNetworkInterceptor(Interceptor interceptor) {
        mOkHttpClient = mOkHttpClient.newBuilder().addNetworkInterceptor(interceptor).build();
    }

    /**
     * 异步请求，回调格式是String
     * @param request
     * @param stringCallback
     */
    public void request(Request request, StringCallback stringCallback) {
        mOkHttpClient.newCall(request).enqueue(stringCallback);
    }

    /**
     * 异步请求,回调在Android主线程
     *
     * @param request
     * @param callback
     */
    public void request(Request request, Callback callback) {
        mOkHttpClient.newCall(request).enqueue(new WrapperCallback(callback));
    }

    /**
     * 同步请求
     *
     * @param request
     * @return
     */
    public Response request(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }


    /**
     * 下载文件
     *
     * @param request
     * @param saveFilePath
     * @param downloadListener
     */
    public void downloadFile(Request request, String saveFilePath, DownloadCallback.DownloadListener downloadListener) {
        mOkHttpClient.newCall(request).enqueue(new DownloadCallback(saveFilePath, downloadListener).get());
    }

    /**
     * 构建一个webSocket连接
     *
     * @param request
     * @return
     */
    public WebSocketHelper buildWebSocket(Request request, WebSocketListener webSocketListener) {
        WebSocketHelper webSocketHelper = new WebSocketHelper();
        webSocketHelper.createWebSocket(mOkHttpClient, request, webSocketListener);
        return webSocketHelper;
    }
}
