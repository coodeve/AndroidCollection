package com.picovr.androidcollection.Utils.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class IntentUtil {
    /**
     * @param context  上下文
     * @param path     文件路径
     * @param mimeType 例如：file/*,image/png
     */
    public void openFileByFileBroswer(Context context, String path, String mimeType) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), mimeType);
        try {
            context.startActivity(Intent.createChooser(intent, "选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 进入 启用/禁用 下载管理程序界面
     */
    public static void skipToDownloadManager(Context context) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }
}
