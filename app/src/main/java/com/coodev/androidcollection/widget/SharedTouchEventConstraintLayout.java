package com.coodev.androidcollection.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * 共享parent view 的 hover效果
 */
public class SharedTouchEventConstraintLayout extends ConstraintLayout {
    public static final String TAG = "SharedTouchEventConstraintLayout";

    private Rect[] mChildRegion;

    public SharedTouchEventConstraintLayout(@NonNull Context context) {
        this(context, null);
    }

    public SharedTouchEventConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SharedTouchEventConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }

        if (mChildRegion == null) {
            mChildRegion = new Rect[childCount];
            for (int i = 0; i < childCount; i++) {
                mChildRegion[i] = new Rect();
            }
        }

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            mChildRegion[i].set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());

        }


    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        if (isInChildRegion((int) event.getX(), (int) event.getY())) {
            return super.dispatchHoverEvent(event);
        }
        event.setAction(MotionEvent.ACTION_HOVER_EXIT);
        return super.dispatchHoverEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()
                && !isInChildRegion((int) event.getX(), (int) event.getY())) {
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        super.onHoverChanged(hovered);
        // 放大效果，特例
        if (hovered) {
            getChildAt(1).setScaleX(1.1f);
            getChildAt(1).setScaleY(1.1f);
        } else {
            getChildAt(1).setScaleX(1.0f);
            getChildAt(1).setScaleY(1.0f);
        }
    }


    private boolean isInChildRegion(int x, int y) {
        boolean isInChildRegion = false;
        for (Rect rect : mChildRegion) {
            if (rect.contains(x, y)) {
                isInChildRegion = true;
                break;
            }
        }

        return isInChildRegion;
    }
}