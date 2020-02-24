package com.picovr.androidcollection.ui.base;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;


public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullScreen(userFullScreen());
        setStatusBarImmersive(statusBarTextColorIsBlack());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(attachLayoutRes());
        initViews();
        registerEventBus(useEventBus());
    }


    protected abstract boolean userFullScreen();

    /**
     * 状态栏字体颜色是否是黑色的
     *
     * @return 是/否
     */
    protected abstract boolean statusBarTextColorIsBlack();


    /**
     * 绑定布局文件
     *
     * @return 布局文件的ID
     */
    @LayoutRes
    protected abstract int attachLayoutRes();

    /**
     * 初始化视图控件
     */
    protected abstract void initViews();

    /**
     * 是否使用eventBus
     *
     * @return
     */
    protected boolean useEventBus() {
        return false;
    }


    @Override
    protected void onDestroy() {
        unRegisterEventBus(useEventBus());
        super.onDestroy();
    }

    /**
     * 沉浸式状态栏
     *
     * @param isBlack 状态栏字体颜色是否为黑色
     */
    private void setStatusBarImmersive(boolean isBlack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isBlack) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 全屏
     *
     * @param userFullScreen
     */
    private void requestFullScreen(boolean userFullScreen) {
        if (!userFullScreen) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }


    private void registerEventBus(boolean userEventBus) {
        if (userEventBus && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unRegisterEventBus(boolean userEventBus) {
        if (userEventBus && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
