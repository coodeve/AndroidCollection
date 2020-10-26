package com.coodev.androidcollection.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coodev.androidcollection.R;


public class ItemHoverConstraintlayout extends ConstraintLayout {

    /**
     * 边框宽度
     */
    private float borderWidth;
    /**
     * 边框距离主体距离，类似padding
     */
    private float borderHostInterval;
    /**
     * 边框颜色
     */
    private int color;
    /**
     * 圆角半径
     */
    private float radius;

    private boolean hovered;

    private Paint mPaint;
    /**
     * 是否按下
     */
    private boolean press;

    public ItemHoverConstraintlayout(Context context) {
        this(context, null);
    }

    public ItemHoverConstraintlayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemHoverConstraintlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        borderWidth = getResources().getDimension(R.dimen.game_app_list_item_hover_line_width);
        borderHostInterval = getResources().getDimension(R.dimen.game_app_list_item_hover_line_bg_interval);
        radius = getResources().getDimension(R.dimen.common_conners_10);
        color = getResources().getColor(R.color.Button_Text_Hover, null);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制边框
        if (hovered) {
            canvas.drawRoundRect(-borderHostInterval,
                    -borderHostInterval,
                    getWidth() + borderHostInterval,
                    getHeight() + borderHostInterval,
                    radius,
                    radius,
                    mPaint);
        }

        if (press) {
            canvas.drawRoundRect(-borderWidth,
                    -borderWidth,
                    getWidth() + borderWidth,
                    getHeight() + borderWidth,
                    radius,
                    radius,
                    mPaint);
        }
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        this.hovered = hovered;
        View parent = (View) getParent();
        if (parent == null) {
            return;
        }
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            press = true;
            postInvalidate();
        } else if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            press = false;
            postInvalidate();
        }
        return super.onTouchEvent(event);
    }
}
