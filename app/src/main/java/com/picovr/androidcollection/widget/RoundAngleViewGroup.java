package com.picovr.androidcollection.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.picovr.androidcollection.R;

/**
 * 圆角的viewgroup
 */
public class RoundAngleViewGroup extends FrameLayout {
    private Path mPath;
    private RectF mRect;
    private float mConner = 10;
    private boolean showConner = true;

    public RoundAngleViewGroup(Context context) {
        this(context, null);
    }

    public RoundAngleViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundAngleViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPath = new Path();
        mRect = new RectF();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        showRectByRound(w, h);
    }

    private void showRectByRound(int w, int h) {
        mPath.reset();
        mRect.set(0, 0, w, h);
        if (showConner) {
            mPath.addRoundRect(mRect, mConner, mConner, Path.Direction.CW);
        } else {
            mPath.addRoundRect(mRect, 0, 0, Path.Direction.CW);
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mPath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    /**
     * 是否显示圆角
     *
     * @param showConner
     */
    public void setShowConner(boolean showConner) {
        this.showConner = showConner;
        post(new Runnable() {
            @Override
            public void run() {
                showRectByRound(RoundAngleViewGroup.this.getWidth(), RoundAngleViewGroup.this.getHeight());
                requestLayout();
                postInvalidate();
            }
        });
    }
}
