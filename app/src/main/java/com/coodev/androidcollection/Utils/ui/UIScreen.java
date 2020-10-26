package com.coodev.androidcollection.Utils.ui;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import com.blankj.utilcode.util.Utils;

public class UIScreen {

    /**
     * 头条屏幕适配
     * android的dp公式：
     * px = density * dp
     * density = dpi / 160
     * px = dp * ( dpi / 160 )
     * <p>
     * 在activity的onCreate中调用,在setContentView之前
     * <p>
     * px = density * dp 依据这个公式
     * 如果设计图是以360dp为准，那么
     * density = px / dp,这样就可以选取一个维度进行计算，比如宽度
     * 示例：density = 1080 / 360 ，计算出一个density
     */

    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;

    public static void setCustomDensity(Activity activity, Application application) {
        DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        int baseLevel = 160;
        int standard = 360;// 假设设计图宽度是360dp

        float targetDensity = appDisplayMetrics.widthPixels / standard;
        float targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatScaledDensity);
        int targetDensityDpi = (int) (baseLevel * targetDensity);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMerics = activity.getResources().getDisplayMetrics();
        activityDisplayMerics.density = targetDensity;
        activityDisplayMerics.scaledDensity = targetScaledDensity;
        activityDisplayMerics.densityDpi = targetDensityDpi;

    }

    /**
     * 取消屏幕适配
     *
     * @param activity
     */
    public static void cancelAdaptScreen(final Activity activity) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        activityDm.density = systemDm.density;
        activityDm.scaledDensity = systemDm.scaledDensity;
        activityDm.densityDpi = systemDm.densityDpi;

        appDm.density = systemDm.density;
        appDm.scaledDensity = systemDm.scaledDensity;
        appDm.densityDpi = systemDm.densityDpi;
    }

    /**
     * 还原屏幕适配
     *
     * @param activity
     * @param isVerticalSlide
     * @param sizeInPx
     */
    public static void restoreAdaptScreen(Activity activity, boolean isVerticalSlide, int sizeInPx) {
        final DisplayMetrics systemDm = Resources.getSystem().getDisplayMetrics();
        final DisplayMetrics appDm = Utils.getApp().getResources().getDisplayMetrics();
        final DisplayMetrics activityDm = activity.getResources().getDisplayMetrics();
        if (isVerticalSlide) {
            activityDm.density = activityDm.widthPixels / (float) sizeInPx;
        } else {
            activityDm.density = activityDm.heightPixels / (float) sizeInPx;
        }
        activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density);
        activityDm.densityDpi = (int) (160 * activityDm.density);

        appDm.density = activityDm.density;
        appDm.scaledDensity = activityDm.scaledDensity;
        appDm.densityDpi = activityDm.densityDpi;
    }

        /**
         * 获取View的图像
         *
         * @param view
         * @return
         */
        public static Bitmap getViewBitmap (View view){
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
