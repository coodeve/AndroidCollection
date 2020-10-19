package com.picovr.androidcollection.widget.webview;

import android.content.Intent;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.picovr.androidcollection.R;

public class WebActivity extends AppCompatActivity {
    private BrowserView mWeb;
    public static Message sMessage;
    private FrameLayout webVideoContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWeb = findViewById(R.id.main_web);
        webVideoContainer = findViewById(R.id.fl_video);
        mWeb.setVideoFullScreen(webVideoContainer);
        if (sMessage != null) {
            WebView.WebViewTransport webViewTransport = (WebView.WebViewTransport) sMessage.obj;
            webViewTransport.setWebView(mWeb);
            sMessage.sendToTarget();
            sMessage = null;
            return;
        }
        mWeb.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWeb.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(mWeb.canGoBack()){
            mWeb.goBack();
            return;
        }
        super.onBackPressed();
    }
}
