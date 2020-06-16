package com.picovr.androidcollection.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;


import com.picovr.androidcollection.R;

import java.util.List;

public class InfoView extends View {
    public static final String TAG = InfoView.class.getSimpleName();
    private final Paint mFramePaint;
    /**
     * 是否可折叠
     */
    private boolean mCanFold = true;
    /**
     * 是否可展开
     * 当此为false时，默认全部展开
     */
    private boolean mCanExpand = true;
    /**
     * 列数
     */
    private int spanCount = 3;
    /**
     * 总行数
     */
    private int rowTotalCount = 3;
    /**
     * 折叠时的列数
     */
    private int rowFoldCount = 2;
    /**
     * 一行的高度
     */
    private int rowHeight = 20;
    /**
     * 默认文字大小
     */
    private float textSize = 14f;

    /**
     * 默认文字颜色
     */
    private int textColor = Color.BLACK;

    private final static String mFoldText = "收起";

    private final static String mExpandText = "更多>";

    private Paint mPaint;
    /**
     * 表单文字
     */
    private List<String> infoTextList;

    private Paint.FontMetrics mFontMetrics;

    /**
     * 当前状态
     * true为展开
     * false为收缩
     */
    private boolean currentStat;

    private long lastTime = 0;

    private int rightPadding = 200;

    private Rect clickRect = new Rect();

    private int mTapTimeOut = ViewConfiguration.getTapTimeout();

    public InfoView(Context context) {
        this(context, null);
    }

    public InfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InfoView);
        mCanFold = typedArray.getBoolean(R.styleable.InfoView_infoView_canFlod, mCanFold);
        mCanExpand = typedArray.getBoolean(R.styleable.InfoView_infoView_canExpand, mCanExpand);
        spanCount = typedArray.getInt(R.styleable.InfoView_infoView_spanCount, spanCount);
        rowFoldCount = typedArray.getInt(R.styleable.InfoView_infoView_rowFoldCount, rowFoldCount);
        rowHeight = (int) typedArray.getDimension(R.styleable.InfoView_infoView_rowHeight, rowHeight);
        textSize = typedArray.getDimension(R.styleable.InfoView_infoView_textSize, textSize);
        textColor = typedArray.getColor(R.styleable.InfoView_infoView_textColor, textColor);
        typedArray.recycle();

        if (!mCanExpand) {// 不能展开，那么也不饿能折叠
            mCanFold = mCanExpand;
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(textColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(12);
        mPaint.setTextSize(textSize);
        mFontMetrics = mPaint.getFontMetrics();

        mFramePaint = new Paint();
        mFramePaint.setAntiAlias(true);
        mFramePaint.setColor(Color.BLUE);
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setStrokeWidth(2f);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        int width = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            height = currentStat ? rowTotalCount * rowHeight : rowFoldCount * rowHeight;
        } else if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
            height = currentStat ? rowTotalCount * rowHeight : rowFoldCount * rowHeight;
        }
        Log.i(TAG, "onMeasure: " + width + "," + height);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int spanWidth = (measuredWidth - rightPadding) / spanCount;
        Rect rect = new Rect();
        float baseLine = 0;
        mPaint.setColor(textColor);
        for (int i = 0; i < rowTotalCount; i++) {
            for (int j = 0; j < spanCount; j++) {
                rect.left = 0 + getPaddingLeft() + j * spanWidth;
                rect.top = 0 + getPaddingTop() + i * rowHeight;
                rect.right = 0 + getPaddingLeft() + (j + 1) * spanWidth;
                rect.bottom = 0 + getPaddingTop() + (i + 1) * rowHeight;
//                canvas.drawRect(rect, mFramePaint);
                baseLine = rect.bottom - mFontMetrics.bottom;
                if (infoTextList == null) {
                    continue;
                }
                int position = i * spanCount + j;
                if (position > infoTextList.size() - 1) {
                    continue;
                }
                Log.i(TAG, "onDraw: " + infoTextList.get(position));
                canvas.drawText(infoTextList.get(position), rect.left, baseLine, mPaint);
            }
        }

        if (!mCanExpand || rowTotalCount == rowFoldCount) {
            return;
        }

        mPaint.setColor(Color.RED);
        clickRect.top = (int) (measuredHeight / 2f - rowHeight / 2f);
        clickRect.left = (int) (measuredWidth - 50);
        clickRect.right = measuredWidth;
        clickRect.bottom = (int) (measuredHeight / 2f + rowHeight / 2f);

        if (currentStat) {
            float baseLines = clickRect.bottom - mFontMetrics.bottom;
            canvas.drawText(mFoldText, 0, mFoldText.length(), clickRect.left, baseLines, mPaint);
        } else {
            float baseLines = clickRect.bottom - mFontMetrics.bottom;
            canvas.drawText(mExpandText, 0, mExpandText.length(), clickRect.left, baseLines, mPaint);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Log.i(TAG, "onTouchEvent: " + event.toString());
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int dx = (int) event.getX();
                int dy = (int) event.getY();
                if (clickRect.contains(dx, dy)) {
                    currentStat = !currentStat;
                    requestLayout();
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    public void setInfoText(List<String> infoText) {
        this.infoTextList = infoText;
        rowTotalCount = infoText.size() / spanCount + (infoText.size() % spanCount == 0 ? 0 : 1);
        currentStat = false;
        requestLayout();
        invalidate();
    }
}