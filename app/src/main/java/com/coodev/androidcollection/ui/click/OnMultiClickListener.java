package com.coodev.androidcollection.ui.click;

import android.view.View;

public abstract class OnMultiClickListener implements View.OnClickListener {

    /**
     * 两次点击按钮之间的点击间隔不能少于1500毫秒
     */
    private static final int MIN_CLICK_DELAY_TIME = 1500;

    /**
     * 上一次点击时间
     */
    private static long lastClickTime = 0;

    /**
     * 多次点击相应的一次
     *
     * @param v
     */
    public abstract void onMultiClick(View v);

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();

        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onMultiClick(v);
        }
    }

}