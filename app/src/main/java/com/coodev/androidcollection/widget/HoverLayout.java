package com.coodev.androidcollection.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * @author patrick.ding
 * @since 18/10/17
 */
public class HoverLayout extends LinearLayout {
    private final static String TAG = HoverLayout.class.getSimpleName();

    public HoverLayout(Context context) {
        this(context, null);
    }

    public HoverLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HoverLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Log.i(TAG, "onHoverEvent# HoverLayout");
        return super.onHoverEvent(event);
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        super.onHoverChanged(hovered);
        Log.i(TAG, "onHoverChanged# HoverLayout");
    }
}
