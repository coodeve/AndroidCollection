package com.picovr.androidcollection.widget.x5;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.picovr.androidcollection.R;
import com.tencent.smtt.sdk.CookieManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String VERTICAL = "vertical";
    public static final int START_NET_SPEED = 10;
    public static final int START_NET_SPEED_CANCEL = 11;
    public static final int START_DOWNLOAD_FILE = 12;
    private FrameLayout mFrameLayout;
    private ProgressBar mProgressBar;
    private X5WebView mX5WebView;
    private Action mAction;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_NET_SPEED:
                    Snackbar.make(mFrameLayout, "网络有点慢哦，努力加载中...", Snackbar.LENGTH_SHORT).show();
                    break;
                case START_NET_SPEED_CANCEL:
                    mHandler.removeMessages(START_NET_SPEED);
                    break;
                case START_DOWNLOAD_FILE:
                    Snackbar.make(mFrameLayout, "正在下载文件，请稍后...", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) return;
        setScreenConfig();
        setContentView(R.layout.activity_x5);
        initView();
        dispatchLoadUrlMode(intent);
        mFrameLayout = findViewById(android.R.id.content);
    }

    private void setScreenConfig() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent# " + intent);
        if (intent == null || mX5WebView == null)
            return;
        dispatchLoadUrlMode(intent);

    }

    private void initView() {
        mProgressBar = findViewById(R.id.progressBar);
        mX5WebView = findViewById(R.id.X5WebView);
        mX5WebView.setProgressBar(mProgressBar);
        mX5WebView.setHandler(mHandler);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mX5WebView != null && mX5WebView.canGoBack()) {
                mX5WebView.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 分类加载
     */
    private void dispatchLoadUrlMode(Intent intent) {
        Uri data = intent.getData();
        if (data == null) {
            return;
        }
        if (isHttpUrl(data)) {
            mAction = new HttpAction(data);
        } else if (isPvrUrl(data)) {
            mAction = new PvrAction(data);
        } else {
            return;
        }

        // 获取横竖屏配置参数
        String orientation = mAction.getOritation();
        setRequestedOrientation(
                VERTICAL.equals(orientation) ?
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mX5WebView.loadUrl(mAction.getUrl());
    }


    @Override
    protected void onDestroy() {
        if (mX5WebView != null) {
            mX5WebView.destroy();
            mX5WebView.clearHistory();
            CookieManager.getInstance().removeAllCookie();

        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    private boolean isHttpUrl(Uri uri) {
        return "https".equals(uri.getScheme()) || "http".equals(uri.getScheme());
    }

    private boolean isPvrUrl(Uri uri) {
        return "pvrweb".equals(uri.getScheme());
    }
}
