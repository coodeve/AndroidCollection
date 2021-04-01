package com.coodev.androidcollection.widget.progress;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.widget.AppCompatButton;

import com.coodev.androidcollection.R;
import com.coodev.androidcollection.Utils.common.AnimationUtil;

/**
 * 加载进度按钮，绘制三个动态的点
 */
public class LoadingButton extends AppCompatButton {

    public static final String TAG = "LoadingButton";
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_LOADING = 1;

    public static final int CIRCLE_COUNT = 3;
    private static final float CIRCLE_SCALE = 1.3f;
    private int currentStatus = STATUS_NORMAL;
    private final Paint mPaint;
    private boolean showLoading;
    private int circleScaleIndex = 0;
    private float currScale = 1.0f;
    private ValueAnimator mValueAnimator;
    private Drawable mSourceBackground;
    private String mSourceText;
    private ColorStateList mSourceTextColor;

    public LoadingButton(Context context) {
        this(context, null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(0xFFCCCCCC);
        mPaint.setAntiAlias(true);
        initAnimation();

    }

    private void initAnimation() {
        AnimationUtil.resetDurationScaleIfDisable();
        mValueAnimator = ValueAnimator.ofFloat(1.0f, CIRCLE_SCALE, 1.0f);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(500);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currScale = (float) animation.getAnimatedValue();
                if (showLoading) {
                    postInvalidate();
                }
            }
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                if (showLoading) {
                    circleScaleIndex++;
                }
            }
        });
    }

    public void load() {
        setStatus(STATUS_LOADING);
    }

    public void reset() {
        setStatus(STATUS_NORMAL);
    }

    /**
     * 设置状态
     *
     * @param status {@link #STATUS_LOADING,#STATUS_NORMAL}
     */
    public void setStatus(int status) {
        if (currentStatus == status) {
            Log.w(TAG, "setStatus: status repetitive !");
            return;
        }

        if (status != STATUS_LOADING && status != STATUS_NORMAL) {
            Log.w(TAG, "setStatus: status value invalid !");
            return;
        }

        currentStatus = status;
        if (currentStatus == STATUS_LOADING) {
            startRotate();
        } else {
            removeDrawableAnim();
        }
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    private void startRotate() {
        backup();
        setBackgroundResource(R.color.colorAccent);
        setText("");
        setEnabled(false);
        setClickable(false);
        circleScaleIndex = 0;
        showLoading = true;
        mValueAnimator.start();
    }


    private void removeDrawableAnim() {
        showLoading = false;
        mValueAnimator.cancel();
        setEnabled(true);
        setClickable(true);
        recover();
    }

    private void backup() {
        mSourceBackground = getBackground();
        mSourceText = getText().toString();
        mSourceTextColor = getTextColors();
    }

    private void recover() {
        setBackground(mSourceBackground);
        setText(mSourceText);
        setTextColor(mSourceTextColor);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        showLoading = false;
        mValueAnimator.cancel();
    }

    @Override
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        if (showLoading) {
            drawCircleView(canvas, circleScaleIndex % CIRCLE_COUNT);
        }
    }

    /**
     * 绘制进度小圆
     *
     * @param canvas           canvas
     * @param circleScaleIndex
     */
    private void drawCircleView(Canvas canvas, int circleScaleIndex) {
        int width = getWidth();
        int height = getHeight();
        int circleRadius = Math.min((height / 2) / 2, (width - width / CIRCLE_COUNT) / CIRCLE_COUNT / 2);
        int circleSpace = (width - CIRCLE_COUNT * (circleRadius * 2)) / 4;
        float cy = height >> 1;
        for (int i = 0; i < CIRCLE_COUNT; i++) {
            float cx = (i + 1) * (circleSpace + 2 * circleRadius) - circleRadius;
            canvas.drawCircle(cx, cy, i == circleScaleIndex ? (float) (circleRadius * currScale) : circleRadius, mPaint);
        }
    }

}
