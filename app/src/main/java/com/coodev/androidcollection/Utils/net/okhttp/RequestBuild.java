package com.coodev.androidcollection.Utils.net.okhttp;

import com.blankj.utilcode.util.FileUtils;

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
     * @param url 地址
     * @return Request
     */
    public Request createGetRequest(String url) {
        return new Request.Builder().url(url).get().build();
    }

    /**
     * 参数以表单形式的post请求
     * HTTP1.1 hostname
     * ...
     * Content-Type: x-www-from-urlencoded
     * ...
     * 空行
     * <p>
     * key1=value1&key2=value2
     *
     * @param url    地址
     * @param params 参数集合
     * @return Request
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
     * HTTP1.1 hostname
     * ...
     * Content-Type: application/json
     * ...
     * 空行
     * <p>
     * json字符串
     *
     * @param url    地址
     * @param params 参数集合
     * @return Request
     */
    public Request createPostRequestRaw(String url, Map<String, Object> params) {
        JSONObject jsonObject = new JSONObject(params);
        return createPostRequestRaw(url, jsonObject.toString());
    }

    /**
     * 直接以字符串形式发送的post请求
     * HTTP1.1 hostname
     * ...
     * Content-Type: application/json
     * 选择text，则请求头是： text/plain
     * 选择javascript，则请求头是： application/javascript
     * 选择html，则请求头是： text/html
     * 选择application/xml，则请求头是： application/xml
     * ...
     * 空行
     * <p>
     * 字符串
     *
     * @param url    地址
     * @param params 字符串
     * @return Request
     */
    public Request createPostRequestRaw(String url, String params) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), params);
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**
     * 上传一个文件，没有其他参数
     * <p>
     * HTTP1.1 hostname
     * ...
     * Content-Type: multipart/form-data;boundary=xxxooo
     * ...
     * 空行
     * xxxooo
     * Content-Description: form-data;name="test"
     * <p>
     * this is text data
     * xxxooo
     * Content-Description: form-data;name="name"
     * <p>
     * this is first line
     * this is second line
     * xxxooo
     * Content-Description: form-data;name="file";filename="file.png"
     * Content-Type: image/png
     * <p>
     * 二进制数据0100001010101001001010
     * xxxooo--
     *
     * @param url      地址
     * @param filePath 文件路径
     * @param mimeType like "image/jpg"
     * @return Request
     */
    public Request createUploadRequest(String url, String fileKey, String filePath, String mimeType) {
        String fileName = fileKey != null ? fileKey : FileUtils.getFileName(filePath);
        if (fileName == null) {
            return null;
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart(fileName, fileName, MultipartBody.create(new File(filePath), MediaType.parse(mimeType)));
        MultipartBody multipartBody = builder.build();
        return new Request.Builder().url(url).post(multipartBody).build();
    }

    /**
     * 上传文件，携带表单参数
     *
     * @param url           地址
     * @param filePath      文件路径
     * @param mimeType      like "image/jpg"
     * @param requestParams 请求参数
     * @return Request
     */
    public Request createUploadRequest(String url, String fileKey, String filePath, String mimeType, Map<String, String> requestParams) {
        String fileName = fileKey != null ? fileKey : FileUtils.getFileName(filePath);
        if (fileName == null) {
            return null;
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        // 参数
        for (Map.Entry<String, String> entrySet : requestParams.entrySet()) {
            builder.addFormDataPart(entrySet.getKey(), entrySet.getValue());
        }
        // 文件
        builder.addFormDataPart(fileName, fileName, MultipartBody.create(new File(filePath), MediaType.parse(mimeType)));
        MultipartBody multipartBody = builder.build();
        return new Request.Builder().url(url).post(multipartBody).build();
    }
}
