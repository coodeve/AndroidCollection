package com.picovr.androidcollection.Utils.common;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

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

}
