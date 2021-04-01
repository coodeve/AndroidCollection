package com.coodev.androidcollection.Utils.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

public class AnimationUtil {
    /**
     * 是否是滑动
     *
     * @param context
     * @param distance
     * @return
     */
    public static boolean isFlig(Context context, int distance) {
        return distance >= ViewConfiguration.get(context).getScaledTouchSlop();
    }


    /**
     * 如果动画被禁用，则重置动画缩放时长
     * 用于处理系统关闭动画时的情况
     */
    public static void resetDurationScaleIfDisable() {
        if (getDurationScale() == 0)
            resetDurationScale();
    }

    /**
     * 重置动画缩放时长
     */
    private static void resetDurationScale() {
        try {
            getField().setFloat(null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float getDurationScale() {
        try {
            return getField().getFloat(null);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static Field getField() throws NoSuchFieldException {
        Field field = ValueAnimator.class.getDeclaredField("sDurationScale");
        field.setAccessible(true);
        return field;
    }
}
