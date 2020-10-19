package com.picovr.androidcollection.widget.recycleview;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class FeatureBigSpacing extends RecyclerView.ItemDecoration {
    /**
     * 顶部和底部间距
     */
    private float boarder_width;
    /**
     * child间距
     */
    private float halfSpaceInPx;

    public FeatureBigSpacing(float boarder_width, float halfSpaceInPx) {
        this.boarder_width = boarder_width;
        this.halfSpaceInPx = halfSpaceInPx;
    }

    @Override
    public void getItemOffsets( Rect outRect,  View view,  RecyclerView parent,  RecyclerView.State state) {
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter != null) {
            int itemCount = adapter.getItemCount();
            outRect.left = (int) halfSpaceInPx;
            //the first one
            if (childAdapterPosition == 0) {
                outRect.left = (int) boarder_width;
            }
            //the last one
            if (childAdapterPosition == itemCount - 1) {
                outRect.right = (int) boarder_width;
            }
        }

    }
}
