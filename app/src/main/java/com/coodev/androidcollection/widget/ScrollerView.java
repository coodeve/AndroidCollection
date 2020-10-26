package com.coodev.androidcollection.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Scroller;

public class ScrollerView extends View {

    private Scroller mScroller;

    public ScrollerView(Context context) {
        this(context, null);
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initScroller();
    }

    /**
     * scroller的使用
     */
    private void initScroller() {
        mScroller = new Scroller(getContext());
    }

    private void smoothScrollTo(int destX, int destY, int duration) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int dx = destX - scrollX;
        int dy = destY = scrollY;

        mScroller.startScroll(scrollX, scrollY, dx, dy, duration);
        // 触发重绘
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
