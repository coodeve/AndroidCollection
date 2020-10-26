package com.coodev.androidcollection.Utils.ui;

import android.os.SystemClock;
import android.view.View;

public class ViewUtils {
    /**
     * 只调用此view上的点击回调函数回调函数
     *
     * @param view
     */
    public static void callOnClick(View view) {
        view.callOnClick();
    }

    public static abstract class OnDoubleClickListener implements View.OnClickListener {

        public abstract void onDoubleClickListener(View v);

        /**
         * 连续点击有效时间
         */
        private final static long DURATION = 100;
        private static long[] mHits = new long[2];
        ;

        public void checkTimes(View v) {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (SystemClock.uptimeMillis() - mHits[0] <= DURATION) {
                mHits = null;
                onDoubleClickListener(v);
            }

        }

        @Override
        public void onClick(View v) {
            checkTimes(v);
        }
    }


}
