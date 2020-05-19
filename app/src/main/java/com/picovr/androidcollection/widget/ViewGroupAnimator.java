package com.picovr.androidcollection.widget;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.view.ViewGroup;

/**
 * ViewGroup组内添加动画
 */
public class ViewGroupAnimator {
    /**
     * 设置默认动画
     *
     * @param viewGroup
     */
    public static void setDefaultInOutAnimator(ViewGroup viewGroup) {
        LayoutTransition layoutTransition = new LayoutTransition();
        ObjectAnimator aniout = ObjectAnimator.ofFloat(null, "rotation", 0f, 90f, 0f);
        ObjectAnimator aniin = ObjectAnimator.ofFloat(null, "rotationY", 0f, 360f, 0f);
        layoutTransition.setAnimator(LayoutTransition.APPEARING, aniin);
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, aniout);
        viewGroup.setLayoutTransition(layoutTransition);
    }
}
