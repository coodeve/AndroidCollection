package com.picovr.androidcollection.Utils.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadUtils {
    public static final String TAG = DownloadUtils.class.getSimpleName();

    /**
     * 使用系统DownloadManager下载
     * 通过广播监听是否下载完成
     * @param context
     * @param url
     * @param contentDisposition
     * @param mimeType
     */
    public static  long downloadBySystem(Context context, String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
//        request.setTitle("This is title");
        // 设置通知栏的描述
//        request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(false);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Log.i(TAG, "downloadBySystem: filename=" + fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//        另外可选一下方法，自定义下载路径
//        request.setDestinationUri()
//        request.setDestinationInExternalFilesDir()
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
        Log.i(TAG, "downloadBySystem: downloadId=" + downloadId);
        return downloadId;
    }

    private static class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("onReceive. intent:{}", intent != null ? intent.toUri(0) : null);
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    Log.i(TAG, "onReceive: downloadId=" + downloadId);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                    Log.i(TAG, "onReceive: getMimeTypeForDownloadedFile=" + type);
                    if (TextUtils.isEmpty(type)) {
                        type = "*/*";
                    }

                    // uri和fileUri地址不一致
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    Log.i(TAG, "onReceive: UriForDownloadedFile=" + uri);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(query);
                    if (cursor.moveToFirst()) {
                        String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if (fileUri != null) {
                            Log.i(TAG, "onReceive: fileUri=" + fileUri);
                            action(context, Uri.parse(fileUri), type);
                        }
                    }
                    cursor.close();
                    if (uri != null) {
                        Log.i(TAG, "onReceive: UriForDownloadedFile=" + uri.toString());
                        action(context, uri, type);
                    }
                }
            }
        }
    }


    public static void action(Context context,Uri uri,String type) {
        Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
        handlerIntent.setDataAndType(uri, type);
        context.startActivity(handlerIntent);
    }

}
