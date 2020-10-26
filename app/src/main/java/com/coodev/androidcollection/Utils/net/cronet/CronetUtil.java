package com.coodev.androidcollection.Utils.net.cronet;

import android.content.Context;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UploadDataProviders;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CronetUtil {

    private CronetEngine mCronetEngine;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CronetUtil(Context context) {
        init(context);
    }

    public void init(Context context) {
        CronetEngine.Builder builder = new CronetEngine.Builder(context);
        builder.enableHttp2(true);
        builder.enableQuic(false);
        mCronetEngine = builder.build();
    }

    /**
     * 创建urlrequest
     *
     * @param url
     * @param callback
     * @return
     */
    public UrlRequest create(String url, String data, UrlRequest.Callback callback) {
        UrlRequest.Builder builder = mCronetEngine.newUrlRequestBuilder(url, callback, executorService);
        builder.setHttpMethod("POST");
        builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
        builder.setUploadDataProvider(UploadDataProviders.create(data.getBytes()), executorService);
        return builder.build();
    }

    /**
     * 发起请求
     *
     * @param url
     * @param callback
     */
    public void request(String url, String data, UrlRequest.Callback callback) {
        create(url, data, callback).start();
    }

    /**
     * 获取一个httpurlconnection
     *
     * @param url
     * @return
     */
    public HttpURLConnection creataHttpUrlConnection(String url) {
        try {
            return (HttpURLConnection) mCronetEngine.openConnection(new URL(url));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 设置默认工厂
     */
    public void setDefaultURL() {
        URL.setURLStreamHandlerFactory(mCronetEngine.createURLStreamHandlerFactory());
    }

    /**
     * callback回调示例
     */
    private static class UrlRequestCallback extends UrlRequest.Callback {
        /**
         * 重定向，可以选择是否继续访问重定向地址
         *
         * @param urlRequest
         * @param urlResponseInfo
         * @param s
         * @throws Exception
         */
        @Override
        public void onRedirectReceived(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, String s) throws Exception {
            urlRequest.followRedirect();
        }

        /**
         * 开始请求body后回回调这里，只调用一次
         *
         * @param urlRequest
         * @param urlResponseInfo
         * @throws Exception
         */
        @Override
        public void onResponseStarted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) throws Exception {

        }

        /**
         * 读取body一定数据时，会回调这个方法，缓冲区不一定是满的，会多次回调此方法
         *
         * @param urlRequest
         * @param urlResponseInfo
         * @param byteBuffer
         * @throws Exception
         */
        @Override
        public void onReadCompleted(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, ByteBuffer byteBuffer) throws Exception {

        }

        /**
         * 最终请求成功回调，可此处处理所有数据
         *
         * @param urlRequest
         * @param urlResponseInfo
         */
        @Override
        public void onSucceeded(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo) {

        }

        @Override
        public void onFailed(UrlRequest urlRequest, UrlResponseInfo urlResponseInfo, CronetException e) {

        }

        @Override
        public void onCanceled(UrlRequest request, UrlResponseInfo info) {

        }
    }

}
