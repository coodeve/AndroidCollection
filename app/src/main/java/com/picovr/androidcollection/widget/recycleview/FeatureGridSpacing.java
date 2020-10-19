package com.picovr.androidcollection.widget.recycleview;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class FeatureGridSpacing extends RecyclerView.ItemDecoration {
    private int spanCount; //列数
    private float spacing; //间隔
    private float broader; //间隔

    public FeatureGridSpacing(float broader, float spacing, int spanCount) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.broader = broader;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter adapter = parent.getAdapter();
        int itemCount = adapter.getItemCount();
        // view位置
        int position = parent.getChildAdapterPosition(view);
        // view属于哪一列
        int column = position % spanCount;

        outRect.left = (int) spacing;

        outRect.bottom = (int) spacing;

        //如果是第一行：
        if (position < spanCount) {
            outRect.left = (int) broader;
        }

        // 如果是最后一行：
        if (isLastRow(position, itemCount, spanCount)) {
            outRect.right = (int) broader;
        }

        // 如果是第一列：
        if (column == 0) {
            outRect.top = 0;
        }

        //如果是第二列：
        if (column == spanCount - 1) {
            outRect.bottom = 0;
        }

    }

    private boolean isLastRow(int currentItemPosition, int totalItems, int spanCount) {
        boolean result = false;
        int rowCount = 0;

        if (0 == totalItems % spanCount) {
            rowCount = totalItems / spanCount;
        } else {
            rowCount = totalItems / spanCount + 1;
        }
        if ((currentItemPosition + 1) > (rowCount - 1) * spanCount)
            result = true;

        return result;
    }
}
