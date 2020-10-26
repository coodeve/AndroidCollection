package com.coodev.androidcollection.Utils.net.okhttp;

import com.coodev.androidcollection.Utils.io.FileUtil;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuild {
    /**
     * 创建一个get请求
     *
     * @param url
     * @return
     */
    public Request createGetRequest(String url) {
        return new Request.Builder().url(url).get().build();
    }

    /**
     * 参数以表单形式的post请求
     *
     * @param url
     * @param params
     * @return
     */
    public Request createPostRequestForm(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            builder.add(entry.getKey(), entry.getValue());
        }

        return new Request.Builder().url(url).post(builder.build()).build();
    }

    /**
     * 参数以json形式发送的post请求
     *
     * @param url
     * @param params
     * @return
     */
    public Request createPostRequestRaw(String url, Map<String, Object> params) {
        JSONObject jsonObject = new JSONObject(params);
        return createPostRequestRaw(url, jsonObject.toString());
    }

    /**
     * 直接以字符串形式发送的post请求
     *
     * @param url
     * @param params
     * @return
     */
    public Request createPostRequestRaw(String url, String params) {
        RequestBody requestBody =
                FormBody.create(MediaType.parse("application/json"), params);
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**
     * 上传一个文件
     *
     * @param url
     * @param filePath
     * @param mimeType like "image/jpg"
     * @return
     */
    public Request createUploadRequest(String url, String filePath, String mimeType) {
        String fileName = FileUtil.getFileName(filePath);
        if (fileName == null) {
            return null;
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart(fileName, fileName, RequestBody.create(MediaType.parse(mimeType), new File(filePath)));
        MultipartBody multipartBody = builder.build();
        return new Request.Builder().url(url).post(multipartBody).build();
    }
}
