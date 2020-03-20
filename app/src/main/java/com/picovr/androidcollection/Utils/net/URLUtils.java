package com.picovr.androidcollection.Utils.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLUtils {
    /**
     * url中常见的头部参数：x-www-form-urlencoded
     * 表示：
     * 用url即web形式的字符串进行编码
     * 主要是将除一下三种之外的字符，进行编码。将其转换为字节，每个字节
     * 要写为百分号后面加2个16进制数字，例如：%2B
     * 1.大小写字母
     * 2.数字
     * 3.标点符号 -_.!~*,
     *
     * @param sourceString
     * @return
     */
    public static String getURLEncoder(String sourceString) {
        try {
            return URLEncoder.encode(sourceString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将url即web形式的字符串进行解码
     *
     * @param sourceString
     * @return
     */
    public static String getURLDecoder(String sourceString) {
        try {
            return URLEncoder.encode(sourceString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }



}
