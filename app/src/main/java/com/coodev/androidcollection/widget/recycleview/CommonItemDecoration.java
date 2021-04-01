package com.coodev.androidcollection.widget.recycleview;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommonItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpanCount;//横条目数量
    private final int mRowSpacing;//行间距
    private final int mColumnSpacing;// 列间距

    /**
     * @param spanCount     列数
     * @param rowSpacing    行间距
     * @param columnSpacing 列间距
     */
    public CommonItemDecoration(int spanCount, int rowSpacing, int columnSpacing) {
        this.mSpanCount = spanCount;
        this.mRowSpacing = rowSpacing;
        this.mColumnSpacing = columnSpacing;
    }


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.Adapter<?> adapter = parent.getAdapter();
        int itemCount = adapter.getItemCount();
        // view位置
        int position = parent.getChildAdapterPosition(view);
        // view属于哪一列
        int column = position % mSpanCount;
        // 如果是第一列
        if (column == 0) {

        }

        // 如果是最后一列
        if (column == mSpanCount - 1) {

        }

        //如果是第一行：
        if (position < mSpanCount) {

        }
        // 如果是最后一行：
        if (isLastRow(position, itemCount, mSpanCount)) {

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