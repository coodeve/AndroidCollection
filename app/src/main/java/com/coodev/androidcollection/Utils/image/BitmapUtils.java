package com.coodev.androidcollection.Utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {


    public static Bitmap decodeBitmap(String pathName, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = sampleSize;
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(pathName, options);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return checkInBitmap(bitmap, options, pathName);
    }

    private static Bitmap checkInBitmap(Bitmap bitmap, BitmapFactory.Options options, String path) {
        boolean honeycomb = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
        if (honeycomb && bitmap != options.inBitmap && options.inBitmap != null) {
            options.inBitmap.recycle();
            options.inBitmap = null;
        }

        if (bitmap == null) {
            try {
                bitmap = BitmapFactory.decodeFile(path, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public static int getSampleSize(InputStream inputStream, int reqWidth, int reqHeight) {
        // decode bound
        int[] bound = decodeBound(inputStream);
        // calculate sample size
        return calculateSampleSize(bound[0], bound[1], reqWidth, reqHeight);
    }

    public static int getSampleSize(String pathName, int reqWidth, int reqHeight) {
        // decode bound
        int[] bound = decodeBound(pathName);
        // calculate sample size
        return calculateSampleSize(bound[0], bound[1], reqWidth, reqHeight);
    }

    public static int calculateSampleSize(int width, int height, int reqWidth, int reqHeight) {
        // can't proceed
        if (width <= 0 || height <= 0) {
            return 1;
        }
        // can't proceed
        if (reqWidth <= 0 && reqHeight <= 0) {
            return 1;
        } else if (reqWidth <= 0) {
            reqWidth = (int) (width * reqHeight / (float) height + 0.5f);
        } else if (reqHeight <= 0) {
            reqHeight = (int) (height * reqWidth / (float) width + 0.5f);
        }

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            if (inSampleSize == 0) {
                inSampleSize = 1;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }

        return inSampleSize;
    }

    public static int[] decodeBound(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static int[] decodeBound(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static int[] decodeBound(Context context, int res) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), res, options);
        return new int[]{options.outWidth, options.outHeight};
    }

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
     * @param showRect  显示区域
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
