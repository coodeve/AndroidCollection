package com.picovr.androidcollection.Utils.image;

import android.content.Context;
import android.os.Looper;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * @author patrick.ding
 * @since 20/2/24
 */
public class ImageUtils {

    /**
     * 加载图片（正常）
     *
     * @param context
     * @param imageView
     * @param url
     */
    public static void loadImage(Context context, ImageView imageView, String url) {
        Glide.with(context).load(url).into(imageView);
    }

    /**
     * 加载指定大小的圆角图片
     *
     * @param context
     * @param imageView
     * @param url
     * @param round_dp
     */
    public static void loadRoundedCenterCropImage(Context context, ImageView imageView, String url, int round_dp) {

    }

    /**
     * 清理内存和磁盘缓存
     */
    public static void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    private static void clearImageMemoryCache(Context context) {
        try {
            //只能在主线程执行
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(context).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
