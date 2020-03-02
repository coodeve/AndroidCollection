package com.picovr.androidcollection.widget.x5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.picovr.androidcollection.Utils.io.DownloadUtils;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebView extends WebView {
    private final static String TAG = X5WebView.class.getSimpleName();
    private static final long NET_SLOW_TIME = 8000;
    private final Context context;
    TextView title;
    private ProgressBar mProgressBar;

    private boolean needClearHistory;

    private Handler mHandler;

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            mHandler.sendEmptyMessageDelayed(MainActivity.START_NET_SPEED, NET_SLOW_TIME);
            Log.i(TAG, "onPageStarted# ");
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            mHandler.sendEmptyMessage(MainActivity.START_NET_SPEED_CANCEL);
            Log.i(TAG, "onPageFinished# ");
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            Log.i(TAG, "doUpdateVisitedHistory# ");
            if (needClearHistory) {
                clearHistory();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (error.getErrorCode() == android.webkit.WebViewClient.ERROR_HOST_LOOKUP) {
                Log.i(TAG, "onReceivedError# 断网显示~");
                needClearHistory = true;
                mHandler.sendEmptyMessage(MainActivity.START_NET_SPEED_CANCEL);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (errorCode == android.webkit.WebViewClient.ERROR_HOST_LOOKUP) {
                Log.i(TAG, "onReceivedError# 断网显示.");
                needClearHistory = true;
                mHandler.sendEmptyMessage(MainActivity.START_NET_SPEED_CANCEL);
            }
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }

    };


    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView webView, int i) {
            if (mProgressBar != null) {
                mProgressBar.setProgress(i);
                mProgressBar.setVisibility(i < 100 ? VISIBLE : INVISIBLE);
            }
        }

        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            return super.onJsAlert(webView, s, s1, jsResult);
        }

        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            return super.onJsConfirm(webView, s, s1, jsResult);
        }
    };

    public X5WebView(Context context) {
        this(context, null);
        setBackgroundColor(85621);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        this.setWebViewClient(client);
        this.setWebChromeClient(mWebChromeClient);
        this.setDownloadListener(new X5DownloadListener());
        initWebViewSettings();
        this.getView().setClickable(true);
        Log.i(TAG, "X5WebView# X5 init Success");
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

//        webSetting.setMixedContentMode(WebSettings.LOAD_NORMAL);
//        webSetting.setLoadWithOverviewMode(true);
//        webSetting.setAllowFileAccess(true);
//        webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
//        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
//        webSetting.setUseWideViewPort(true);
//        webSetting.setSupportMultipleWindows(false);
//        webSetting.setAppCacheEnabled(true);
//        webSetting.setBlockNetworkImage(true);
//        webSetting.setDatabaseEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setJavaScriptEnabled(true);
//        webSetting.setGeolocationEnabled(true);
//        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webSetting.setAppCachePath(context.getDir("appcache", 0).getPath());
//        webSetting.setDatabasePath(context.getDir("databases", 0).getPath());
//        webSetting.setGeolocationDatabasePath(context.getDir("geolocation", 0).getPath());
//        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
//        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
//        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        // webSetting.setPreFectch(true);
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().stopSync();
    }


    public void setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }

    private class X5DownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            DownloadUtils.downloadBySystem(getContext(),url, contentDisposition, mimeType);
            mHandler.sendEmptyMessage(MainActivity.START_DOWNLOAD_FILE);
        }
    }
}
