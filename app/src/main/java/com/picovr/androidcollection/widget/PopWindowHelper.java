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

    public void show(View anchor, int layout) {
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

    }
}
