package com.coodev.androidcollection.Utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import java.io.IOException;

public class BitmapUtils {
    /**
     * bitmap 复用
     *
     * @param context
     * @param resId
     * @param reuseBitmap
     * @return
     */
    public static Bitmap getBitmap(Context context, int resId, Bitmap reuseBitmap) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (canUseForInBitmap(reuseBitmap, options)) {
            options.inMutable = true;
            options.inBitmap = reuseBitmap;
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    /**
     * 判断是否可以复用
     * 新内存 <= 可复用内存
     *
     * @param reuseBitmap
     * @param options
     * @return
     */
    private static boolean canUseForInBitmap(Bitmap reuseBitmap, BitmapFactory.Options options) {
        int width = options.outWidth / Math.max(options.inSampleSize, 1);
        int height = options.outHeight / Math.max(options.inSampleSize, 1);
        int byteCount = width * height * getBytesPerPixel(reuseBitmap.getConfig());
        return byteCount <= reuseBitmap.getAllocationByteCount();
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            default:
                bytesPerPixel = 4;
                break;
        }

        return bytesPerPixel;
    }

    /**
     * 大图局部显示
     *
     * @param context
     * @param imagePath
     * @param showRect 显示区域
     * @return
     */
    public static Bitmap getRegionBitmap(Context context, String imagePath, Rect showRect) {
        try {
            final BitmapRegionDecoder bitmapRegionDecoder =
                    BitmapRegionDecoder.newInstance(imagePath, false);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            return bitmapRegionDecoder.decodeRegion(showRect, options);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
