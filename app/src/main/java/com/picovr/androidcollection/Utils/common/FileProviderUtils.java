package com.picovr.androidcollection.Utils.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * 文件分享
 */
public class FileProviderUtils {

    public static Uri getUriForFile(Context mContext, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(mContext, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFile24(Context mContext, File file) {
        Uri fileUri = android.support.v4.content.FileProvider.getUriForFile(mContext,
                mContext.getPackageName() + ".fileProvider",
                file);
        return fileUri;
    }

    public static void setIntentDataAndType(Context mContext,
                                            Intent intent,
                                            String type,
                                            File file,
                                            boolean writeAble) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(mContext, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
    }

    /**
     * Android传入type打开文件
     *
     * @param mContext
     * @param file
     * @param type
     */
    public static void commonOpenFileWithType(Context mContext, File file, String type) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        FileProviderUtils.setIntentDataAndType(mContext, intent, type, file, true);
        mContext.startActivity(intent);
    }

    /**
     * Android打开Video文件
     *
     * @param mContext
     * @param file
     */
    public static void openVideoFileIntent(Context mContext, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        FileProviderUtils.setIntentDataAndType(mContext, intent, DATA_TYPE_VIDEO, file, false);
        mContext.startActivity(intent);
    }

    /**
     * Android打开Audio文件
     *
     * @param mContext
     * @param file
     */
    private static void openAudioFileIntent(Context mContext, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        FileProviderUtils.setIntentDataAndType(mContext, intent, DATA_TYPE_AUDIO, file, false);
        mContext.startActivity(intent);
    }

    /**
     * 声明各种类型文件的dataType
     **/
    private static final String DATA_TYPE_APK = "application/vnd.android.package-archive";
    private static final String DATA_TYPE_VIDEO = "video/*";
    private static final String DATA_TYPE_AUDIO = "audio/*";
    private static final String DATA_TYPE_HTML = "text/html";
    private static final String DATA_TYPE_IMAGE = "image/*";
    private static final String DATA_TYPE_PPT = "application/vnd.ms-powerpoint";
    private static final String DATA_TYPE_EXCEL = "application/vnd.ms-excel";
    private static final String DATA_TYPE_WORD = "application/msword";
    private static final String DATA_TYPE_CHM = "application/x-chm";
    private static final String DATA_TYPE_TXT = "text/plain";
    private static final String DATA_TYPE_PDF = "application/pdf";
    /**
     * 未指定明确的文件类型，不能使用精确类型的工具打开，需要用户选择
     */
    private static final String DATA_TYPE_ALL = "*/*";

    /**
     * 打开文件
     *
     * @param mContext
     * @param file
     */
    public static void openFile(Context mContext, File file) {
        if (!file.exists()) {
            return;
        }
        // 取得文件扩展名
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
        // 依扩展名的类型决定MimeType
        switch (end) {
            case "3gp":
            case "mp4":
                openVideoFileIntent(mContext, file);
                break;
            case "m4a":
            case "mp3":
            case "mid":
            case "xmf":
            case "ogg":
            case "wav":
                openAudioFileIntent(mContext, file);
                break;
            case "doc":
            case "docx":
                commonOpenFileWithType(mContext, file, DATA_TYPE_WORD);
                break;
            case "xls":
            case "xlsx":
                commonOpenFileWithType(mContext, file, DATA_TYPE_EXCEL);
                break;
            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                commonOpenFileWithType(mContext, file, DATA_TYPE_IMAGE);
                break;
            case "txt":
                commonOpenFileWithType(mContext, file, DATA_TYPE_TXT);
                break;
            case "htm":
            case "html":
                commonOpenFileWithType(mContext, file, DATA_TYPE_HTML);
                break;
            case "apk":
                commonOpenFileWithType(mContext, file, DATA_TYPE_APK);
                break;
            case "ppt":
                commonOpenFileWithType(mContext, file, DATA_TYPE_PPT);
                break;
            case "pdf":
                commonOpenFileWithType(mContext, file, DATA_TYPE_PDF);
                break;
            case "chm":
                commonOpenFileWithType(mContext, file, DATA_TYPE_CHM);
                break;
            default:
                commonOpenFileWithType(mContext, file, DATA_TYPE_ALL);
                break;
        }
    }

}
