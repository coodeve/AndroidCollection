package com.picovr.androidcollection.widget.recycleview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * recycleview 分割线配置
 * 可以实现很多效果
 */
public class ItemSpacing extends RecyclerView.ItemDecoration {

    // 左边距
    private int left;
    // 上边距
    private int top;
    // 右边距
    private int right;
    // 下边距
    private int bottom;

    private Paint mPaint;

    // 分割线宽度
    private int mDividerWidth;
    // 分割线高度
    private int mDividerHeight;
    // 粘性头部,吸顶
    private boolean stickHead;
    // 时间线
    private boolean timeline;

    public ItemSpacing() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);// 默认画笔颜色
        mDividerWidth = 2;
    }

    public void setItemOffset(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    public void setDividerHeight(int dividerHeight) {
        mDividerHeight = dividerHeight;
    }

    public void setStickHead(boolean stickHead) {
        this.stickHead = stickHead;
    }

    /**
     * 设置ItemView的内嵌偏移长度（inset）
     * 效果类似于item的padding，所以控制outRect即可
     * 即 outRest.set(10,10,10,10)
     * <p>
     * 这个是针对每个item的
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        setOutRect(outRect);
    }

    // 在子视图上设置绘制范围，并绘制内容
    // 绘制图层在ItemView以下，所以如果绘制区域与ItemView区域相重叠，会被遮挡
    // 这个是针对RecyclerView本身的
    // 所以在 onDraw 方法中需要遍历屏幕上可见的 ItemView，
    // 分别获取它们的位置信息，然后分别的绘制对应的分割线。
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    //同样是绘制内容，但与onDraw（）的区别是：绘制在图层的最上层
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int childCount = parent.getChildCount();
        View child = null;
        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            drawDivider(c, parent);
            showStickHeader(child, parent, i);
            showTimeLine(child, parent, i);
        }
    }

    /**
     * 绘制header部
     * 配合getItemOffsets
     *
     * @param child
     * @param parent
     * @param showIndex
     */
    private void showStickHeader(View child, RecyclerView parent, int showIndex) {
        if (!stickHead) {
            return;
        }
    }

    /**
     * 绘制时间轴
     * 配合getItemOffsets
     *
     * @param child
     * @param parent
     * @param showIndex
     */
    private void showTimeLine(View child, RecyclerView parent, int showIndex) {
        if (!timeline) {
            return;
        }
    }

    /**
     * 画分割线到item的下边缘
     */
    private void drawDivider(Canvas c, View child) {
        c.drawRect(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom() + mDividerHeight, mPaint);
    }

    /**
     * 边距控制
     *
     * @param outRect
     */
    protected void setOutRect(Rect outRect) {
        outRect.set(left, top, right, bottom);
    }
}
