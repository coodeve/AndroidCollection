package com.coodev.androidcollection.widget.recycleview;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 使用BaseRecyclerViewAdapterHelper
 * object:数据JavaBean
 * BaseViewHolder：viewholder
 */
public class TestRecycleAdapter extends BaseQuickAdapter<Object, BaseViewHolder> {

    public TestRecycleAdapter(int layoutResId, @Nullable List<Object> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {

    }

}
