package com.picovr.androidcollection.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

public class PopWindowHelper {

    private Context mContext;

    public PopWindowHelper(Context context) {
        mContext = context;
    }

    /**
     * 显示popupwindow
     *
     * @param anchor
     * @param layout
     * @return
     */
    public PopupWindow show(View anchor, int layout) {
        LayoutInflater from = LayoutInflater.from(mContext);
        View view = from.inflate(layout, null);
        PopupWindow popupWindow =
                new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(android.R.style.Widget_Holo_PopupWindow);
        popupWindow.setFocusable(false);

        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(anchor);
        }

        return popupWindow;
    }

    /**
     * 显示popupwindow
     *
     * @param anchor
     * @param view
     * @return
     */
    public PopupWindow show(View anchor, View view) {
        PopupWindow popupWindow =
                new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(android.R.style.Widget_Holo_PopupWindow);
        popupWindow.setFocusable(false);

        if (!popupWindow.isShowing()) {
            popupWindow.showAsDropDown(anchor);
        }

        return popupWindow;
    }
}
