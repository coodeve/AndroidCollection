package com.coodev.androidcollection.Utils.net.okhttp;

import android.util.Log;

import com.coodev.androidcollection.Utils.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Response;

/**
 * 文件下载回调
 * 没有断点续传
 */
public class DownloadCallback {
    public static final String TAG = DownloadCallback.class.getSimpleName();


    private final static int BUFFER_SIZE = 4096;
    /**
     * 保存文件路劲
     */
    private String saveFilePath;

    /**
     * 下载回调
     */
    private DownloadListener mDownloadListener;

    /**
     * 注意，回调都在子线程
     */
    public interface DownloadListener {
        /**
         * @param process 1~100
         */
        void process(int process);

        void success(String path);

        void failure(Exception e);
    }

    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            if (mDownloadListener != null) {
                mDownloadListener.failure(e);
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.w(TAG, "onResponse: " + response.toString());
                if (mDownloadListener != null) {
                    mDownloadListener.failure(new IOException(response.toString()));
                }
                return;
            }

            MediaType mediaType = response.body().contentType();
            long contentLength = response.body().contentLength();

            BufferedOutputStream bufferedOutputStream = null;
            BufferedInputStream bufferedInputStream = null;

            int tempProcess = 0; // 进度数值过滤，保证只回调1~100的整数
            int downloadTotal = 0;// 已下载总数
            int len = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            try {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(saveFilePath));
                bufferedInputStream = new BufferedInputStream(response.body().byteStream());
                while ((len = bufferedInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, len);
                    // TODO 下载中
                    downloadTotal += len;
                    int progress = (int) ((downloadTotal * 1.0f / contentLength * 1.0f) * 100);
                    if (tempProcess == progress) {
                        continue;
                    }
                    tempProcess = progress;
                    if (mDownloadListener != null) {
                        mDownloadListener.process(progress);
                    }
                }
                bufferedOutputStream.flush();
                // TODO 下载完成
                if (mDownloadListener != null) {
                    mDownloadListener.success(saveFilePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mDownloadListener != null) {
                    mDownloadListener.failure(e);
                }
            } finally {
                IOUtils.close(bufferedOutputStream);
                IOUtils.close(bufferedInputStream);
            }

        }
    };

    public DownloadCallback(String saveFilePath, DownloadListener downloadListener) {
        this.saveFilePath = saveFilePath;
        mDownloadListener = downloadListener;
    }

    public Callback get() {
        return mCallback;
    }
}
