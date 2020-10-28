package com.coodev.androidcollection.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class InfoListView extends ListView {
    public InfoListView(Context context) {
        this(context, null);
    }

    public InfoListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
