package com.picovr.androidcollection.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RelativeLayout;


/**
 * <pre>
 *   @author  : fpt
 *   e-mail  : fengfei0205@qq.com
 *   time    : 2018/09/15 14:33
 *   desc    : 自定义能显示红点的RadioButton
 * </pre>
 */
public class RedRelativeLayout extends RelativeLayout {
    private Context mContext;
    /**
     * 画笔对象
     */
    private Paint mPaint;
    /**
     * 是否显示红点
     */
    private boolean isShowDot;
    /**
     * 是否显示文字
     */
    private boolean isShowNumberDot;
    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;
    /**
     * 顶部图片宽
     */
    private int IntrinsicWidth;
    /**
     * 文字显示
     */
    private String mNumberText;
    /**
     * 文字显示区域
     */
    private RectF mRectF;
    /**
     * 圆点默认颜色
     */
    private int mPaintColor;
    /**
     * 圆点半径
     */
    private int mCircleDotRadius;
    private int IntrinsicHeight;

    public RedRelativeLayout(Context context) {
        super(context);
        initView(context);
    }

    public RedRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RedRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    /**
     * 初始化
     */
    private void initView(Context context) {
        mContext = context;
        //圆点半径
        mCircleDotRadius = dp2px(3);
        // 初始化画笔对象
        mPaint = new Paint();
        // 默认画笔颜色

        mPaintColor = Color.RED;
        // 文字显示区域
        mRectF = new RectF();
        // 拿到DrawableTop


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        View child = getChildAt(0);
        Log.i("Patrick", "onDraw: " + child);
        int childWidth = 0;
        int childHeight = 0;
        if (child != null) {
            Log.i("Patrick", "onDraw: " + child.getWidth());
            IntrinsicWidth = child.getWidth();
            IntrinsicHeight = child.getHeight();
        }
        mWidth = getWidth();
        mHeight = getHeight();
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(mPaintColor);
        mPaint.setTextSize(30f);
        float pivotX = (float) (mWidth / 2 + IntrinsicWidth / 2);
        float pivotY = (float) (mHeight / 2 - IntrinsicHeight / 2);
        // 显示红点
        if (isShowDot) {
            canvas.drawCircle(pivotX, pivotY + dp2px(2), mCircleDotRadius, mPaint);
        }
        // 显示数字红点
        else if (isShowNumberDot && !TextUtils.isEmpty(mNumberText)) {
            float textWidth = mPaint.measureText(mNumberText);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float textHeight = Math.abs((fontMetrics.top + fontMetrics.bottom));
            // 数字左右增加一定的边距
            mRectF.setEmpty();
            mRectF.set(pivotX - dp2px(5),
                    pivotY -  dp2px(8),
                    pivotX + dp2px(17) + dp2px(3),
                    pivotY  + dp2px(8));
            canvas.drawRoundRect(mRectF, dp2px(8), dp2px(8), mPaint);
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawText(mNumberText,
                    pivotX - dp2px(5)+dp2px(12),
                    pivotY +dp2px(4),
                    mPaint);
        }
    }

    /**
     * 设置是否显示小圆点
     *
     * @param isShowDot
     */
    public void setShowSmallDot(boolean isShowDot) {
        this.isShowDot = isShowDot;
        if (isShowDot) {
            isShowNumberDot = false;
        }
        invalidate();
    }

    /**
     * 是否显示数字
     *
     * @param isShowNumberDot
     */
    public void setShowNumberDot(boolean isShowNumberDot) {
        this.isShowNumberDot = isShowNumberDot;
        if (isShowNumberDot) {
            isShowDot = false;
        } else {
            mNumberText = "0";
        }
        invalidate();
    }

    /**
     * 设置数字(0~99+)
     *
     * @param text
     */
    public void setDotNumber(@NonNull String text) {
        this.mNumberText = text;
        if (isShowNumberDot) {
            invalidate();
        }
    }

    private int dp2px(int dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
