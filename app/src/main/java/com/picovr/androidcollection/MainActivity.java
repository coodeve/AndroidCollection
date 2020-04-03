package com.picovr.androidcollection;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.picovr.androidcollection.Utils.ui.DpPxUtils;
import com.picovr.androidcollection.widget.webview.BrowserView;

import java.util.List;

public class MainActivity extends FragmentActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private BrowserView mBrowserView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
