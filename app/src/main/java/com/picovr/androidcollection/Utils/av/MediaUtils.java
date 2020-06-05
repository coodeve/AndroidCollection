package com.picovr.androidcollection.Utils.av;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Trace;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.picovr.androidcollection.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MediaUtils {
    public static final String TAG = MediaUtils.class.getSimpleName();

    public static class VideoBean {
        private int id;
        private String path;
        private long duration;
        private long size;
        private long date;
        private String name;

        private int number = 0;

        public VideoBean(int id, String path, String name, long duration, long size, long date) {
            this.id = id;
            this.path = path;
            this.duration = duration;
            this.size = size;
            this.date = date;
            this.name = name;
        }

        public String getFormatDuration() {
            return MediaUtils.formatTimeWithUs(duration);
        }

        public String getVideoPath() {
            return TextUtils.isEmpty(path) ? "" : path;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public boolean isSelected() {
            return number != 0;
        }

    }

    /**
     * 获取视频时长
     *
     * @param videoPath
     * @return
     */
    public static long getVideoDuration(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        String strDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long duration = Long.valueOf(strDuration);
        mmr.release();
        return duration;
    }

    /**
     * 更新媒体库显示
     *
     * @param filePath
     */
    public static void notifyContentResolve(Context context, String filePath) {
        File file = new File(filePath);
        //获取ContentResolve对象，来操作插入文件
        ContentResolver localContentResolver = context.getContentResolver();
        //ContentValues：用于储存一些基本类型的键值对
        ContentValues localContentValues = getContentValues(file);
        //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
        Uri localUri;
        if (filePath.endsWith(".mp4")) {
            localContentValues.put(MediaStore.Video.Media.DURATION, getVideoDuration(filePath));
            localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
        } else {
            localUri = localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
        }
        if (localUri != null) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
        }
    }

    private static ContentValues getContentValues(File file) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", file.getName());
        localContentValues.put("_display_name", file.getName());
        localContentValues.put("mime_type", getMimeType(file));
        localContentValues.put("datetaken", System.currentTimeMillis());
        localContentValues.put("date_modified", System.currentTimeMillis());
        localContentValues.put("date_added", System.currentTimeMillis());
        localContentValues.put("_data", file.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(file.length()));
        return localContentValues;
    }

    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public static String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null || !type.isEmpty()) {
            return type;
        }
        return "file/*";
    }

    public static String formatTimeWithUs(long us) {
        int second = (int) (us / 1000.0);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }

    /**
     * 获取手机中所有可见视频
     */
    private static List<VideoBean> getAllVideos(Context context) {
        List<VideoBean> videoList = new ArrayList<>();
        Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Thumbnails._ID
                , MediaStore.Video.Thumbnails.DATA
                , MediaStore.Video.Media.DURATION
                , MediaStore.Video.Media.SIZE
                , MediaStore.Video.Media.DATE_ADDED
                , MediaStore.Video.Media.DISPLAY_NAME
                , MediaStore.Video.Media.DATE_MODIFIED};
        Cursor mCursor = context.getContentResolver().query(mVideoUri, projection
                , null, null
                , MediaStore.Video.Media.DATE_ADDED + " DESC ");
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA));
                long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                //单位kb
                long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024;
                if (size < 0) {
                    size = new File(path).length() / 1024;
                }
                String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                long date = mCursor.getLong(timeIndex) * 1000;

                //需要判断当前文件是否存在(有些文件已经不存在图片显示不出来)
                boolean exists = FileUtils.isFileExists(path);
                if (exists) {
                    videoList.add(new VideoBean(videoId, path, displayName, duration, size, date));
                }
            }
            mCursor.close();
        }
        return videoList;
    }


    /**
     * 获取视频分辨率
     *
     * @param videoPath
     * @return 854*480
     */
    public static String getVideoRatio(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        //视频高度
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        //视频宽度
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String result = width + "*" + height;
        mmr.release();
        return result;
    }

    /**
     * 图片压缩
     *
     * @param imagePath 图片路径
     */
    public static File compress(Context context, String imagePath) {
        String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        String dirPath = context.getExternalCacheDir().getAbsolutePath();
        String desPath = dirPath + "/" + fileName;
        File destFile = new File(desPath);
        if (destFile.exists() && destFile.length() != 0) {
            return destFile;
        } else {
            Bitmap source = com.blankj.utilcode.util.ImageUtils.getBitmap(imagePath);
            Bitmap dest = ImageUtils.compressByScale(source, source.getWidth() / 4, source.getHeight() / 4, true);
            boolean b = ImageUtils.save(dest, desPath, Bitmap.CompressFormat.PNG, true);
            if (b) {
                return destFile;
            } else {
                return null;
            }
        }
    }

    /**
     * 获取视频第一帧
     *
     * @param videoPath
     * @return
     */
    public static File frame(Context context, String videoPath) {
        String fileName = videoPath.substring(videoPath.lastIndexOf("/") + 1).replace("mp4", "png");
        String dirPath = context.getExternalCacheDir().getAbsolutePath();
        String desPath = dirPath + "/" + fileName;
        File destFile = new File(desPath);
        if (destFile.exists() && destFile.length() != 0) {
            return destFile;
        } else {
            Bitmap source = null;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            source = mmr.getFrameAtTime(1000);
            mmr.release();
            if (source == null) {
                mmr = new MediaMetadataRetriever();
                mmr.setDataSource(videoPath);
                source = mmr.getFrameAtTime();
                mmr.release();
            }

            if (source == null) {
                source = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            }

            boolean b = ImageUtils.save(source, desPath, Bitmap.CompressFormat.PNG, true);
            if (b) {
                return destFile;
            } else {
                return null;
            }

        }
    }

    /**
     * 获取视频宽高比
     *
     * @param file
     * @return
     */
    public static Float getVideoRadio(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)); // 视频高度
        int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)); // 视频宽度
        Log.d(TAG, "height:" + height + "***width:" + width);
        DecimalFormat df = new DecimalFormat("0.0");
        float radio = Float.parseFloat(df.format((float) width / height));
        Log.d(TAG, "radio = " + radio);
        return radio;
    }

    /**
     * 扫描指定文件夹，将文件更新到媒体库，类似{@link #notifyContentResolve(Context, String)}
     * <p>
     * <p>
     * MediaScannerConnection还有其他方法可用
     *
     * @param context
     * @param path
     */
    public void scanMedia(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
    }


    public static class ImageBean {
        public String path;
        public String name;
    }

    public static List<ImageBean> getAllImages(Context context) {
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(imageUri,
                null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);
        if (cursor == null) {
            return null;
        }

        List<ImageBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            ImageBean imageBean = new ImageBean();
            imageBean.path = path;
            imageBean.name = displayName;
            list.add(imageBean);
        }

        cursor.close();

        return list;
    }

}
