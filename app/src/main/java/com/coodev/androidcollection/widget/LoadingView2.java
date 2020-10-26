package com.coodev.androidcollection.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;


public class LoadingView2 extends ViewGroup {

    private static final int CIRCLE_COUNT = 3;

    private static final String TAG = "LoadingView2";

    private static final int DEFAULT_PADDING = 5;
    private int padding = DEFAULT_PADDING;

    private float DEFAULT_RADIUS = 100;

    private final Animator[] animators = new Animator[CIRCLE_COUNT];
    private int radius;

    public LoadingView2(Context context) {
        this(context, null);
    }

    public LoadingView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        DEFAULT_RADIUS = getContext().getResources().getDimension(R.dimen.loading_dot_radius);
        initCircles();
        initAnimation();
    }

    private void initAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            ObjectAnimator animator = ObjectAnimator.ofFloat((Circle) getChildAt(i), "scale", 0.5f, 1, 0.5f);
            animator.setDuration(500);
            animator.setInterpolator(new LinearInterpolator());
            animators[i] = animator;
            animator.setRepeatMode(ValueAnimator.REVERSE);
        }
        for (int i = 0; i < animators.length; i++) {
            final int tmp = i;
            Animator animator = animators[i];
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (getVisibility() == VISIBLE) {
                        animators[(tmp + 1) % animators.length].start();
                    }
                }
            });
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if (changedView != this) {
            return;
        }
        if (visibility == VISIBLE && animators[0] != null) {
            animators[0].start();
        } else {
            for (Animator animator : animators) {
//                animator.cancel();
                animator.end();
            }
        }
    }

    private void initCircles() {
        for (int i = 0; i < CIRCLE_COUNT; i++) {
            Circle circle = new Circle(getContext());
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(circle, lp);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radius = (int) DEFAULT_RADIUS;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ((Circle) child).setRadius(radius);
            LayoutParams lp = child.getLayoutParams();
            lp.width = radius * 2;
            lp.height = radius * 2;
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int neededWidth = r - l - (radius << 1) * CIRCLE_COUNT - padding * (CIRCLE_COUNT - 1)
                - getPaddingLeft() - getPaddingRight();
        int horizontalOffset = l + neededWidth >> 1;

        int verticalOffset = (b + t - radius) / 2;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(horizontalOffset,
                    verticalOffset,
                    horizontalOffset += child.getMeasuredWidth(),
                    verticalOffset + radius << 1);
            horizontalOffset += padding;
        }
    }

    public static class Circle extends View {

        private int radius = 100;
        private Paint paint;
        private float scale = 0.5f;

        void setScale(float scale) {
            this.scale = scale;
            invalidate();
        }

        float getScale() {
            return this.scale;
        }

        public Circle(Context context) {
            this(context, null);
        }

        public Circle(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public Circle(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            paint = new Paint();
            paint.setColor(0xFFCCCCCC);
            paint.setAntiAlias(true);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.translate(getMeasuredWidth() >> 1, getMeasuredHeight() >> 1);
            canvas.scale(scale, scale);
            canvas.drawCircle(0, 0, radius, paint);
            canvas.restore();
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }
    }
}
