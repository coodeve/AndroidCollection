package com.coodev.androidcollection.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class BatteryView extends View {
    private float percent = 1.0f;
    private Paint outer,inner,header;
    
    public BatteryView(Context context) {
        super(context);
        init(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 电池电量外面的Paint
        outer = new Paint();
        // 电池电量里面的Paint
        inner = new Paint();
        // 电池头部的Paint
        header = new Paint();

        //抗锯齿
        outer.setAntiAlias(true);
        inner.setAntiAlias(true);
        header.setAntiAlias(true);
        //填充类型
        outer.setStyle(Paint.Style.STROKE);
        outer.setStrokeWidth(4);
        inner.setStyle(Paint.Style.FILL);
        header.setStyle(Paint.Style.FILL);
        //设置颜色
        outer.setColor(Color.GRAY);
        if (percent > 0.2f) {
            inner.setColor(Color.GRAY);
        } else {
            inner.setColor(Color.RED);
        }
        header.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        //根据电量百分比画图
        float p = (w - 12) * percent;

        RectF re1 = new RectF(4, 4, p, h-4);
        RectF re2 = new RectF(0, 0, w-8, h);
        RectF re3 = new RectF(w-8, h / 2 - 8, w, h / 2 + 8);
        // 绘制圆角矩形
        canvas.drawRect(re1, inner);
        canvas.drawRect(re2, outer);
        canvas.drawRect(re3, header);
    }

    /**
     * 设置当前电量
     * @param percent  0～100
     */
    public synchronized void setProgress(int percent) {
        this.percent = (float) (percent / 100.0);
        if (this.percent > 1){
            this.percent = 1.0f;
        }else if (this.percent < 0){
            this.percent = 0;
        }
        postInvalidate();
    }

}
