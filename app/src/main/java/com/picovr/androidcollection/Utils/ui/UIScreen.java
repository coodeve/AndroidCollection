package com.picovr.androidcollection.Utils.ui;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.view.View;

public class UIScreen {

    /**
     * 头条屏幕适配
     */
    public static void setCustomDesity() {

    }

    /**
     * 获取View的图像
     *
     * @param view
     * @return
     */
    public static Bitmap getViewBitmap(View view) {
        if (null == view) {
            return null;
        }
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        if (Build.VERSION.SDK_INT >= 11) {
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(),
                    View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                    view.getHeight(), View.MeasureSpec.EXACTLY));
            view.layout((int) view.getX(), (int) view.getY(),
                    (int) view.getX() + view.getMeasuredWidth(),
                    (int) view.getY() + view.getMeasuredHeight());
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        Bitmap b = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();
        b = ThumbnailUtils.extractThumbnail(b, 150, 150);
        return b;

    }


}
