package com.picovr.androidcollection.Utils.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author patrick.ding
 * @since 19/8/28
 */
public class NetUtil {

    public static final String TAG = "NetUtil";

    /**
     * 网络请求回调
     */
    public interface NetCallback {
        void onSuccess(String var1);

        void onError(String var1);
    }

    private static class TrustAllTrustManager implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
            return;
        }
    }

    //设置URLConnection的连接超时时间
    private final static int CONNET_TIMEOUT = 5 * 1000;
    //设置URLConnection的读取超时时间
    private final static int READ_TIMEOUT = 5 * 1000;
    //设置请求参数的字符编码格式
    private final static String QUERY_ENCODING = "UTF-8";
    // http请求类型
    private static final String POST = "POST";

    /**
     * http调用
     *
     * @param postType http/https
     * @param url      目标地址
     * @param data     数据
     * @param callback 回调函数
     * @return
     */
    public static String postHttp(String url, String data, NetCallback callback) {
        String format = String.format("url:%s,data:%s", url, data);
        System.out.println(format);

        HttpURLConnection connect = null;
        try {
            URL bootUrl = new URL(url);
            connect = (HttpURLConnection) bootUrl.openConnection();
            connect.setRequestMethod(POST);
            connect.setConnectTimeout(CONNET_TIMEOUT);
            connect.setReadTimeout(READ_TIMEOUT);
            connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connect.setDoInput(true);
            connect.setDoOutput(true);
            OutputStream outputStream = connect.getOutputStream();
            outputStream.write(((URLDecoder.decode(data, QUERY_ENCODING))).getBytes());
            outputStream.close();
            int responseCode = connect.getResponseCode();
            System.out.println("Status code=" + responseCode);
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String line;
                String result = "";
                while ((line = br.readLine()) != null) {
                    result += line;
                }
                System.out.println("returnString = " + URLDecoder.decode(result, "UTF-8"));
                br.close();
                if (callback != null)
                    callback.onSuccess(result);
                return result;
            } else {
                System.out.println("server not response or other error info");
                if (callback != null)
                    callback.onError("Status code " + responseCode + ",server not response or other error info");
                return null;
            }
        } catch (IOException e) {
            System.out.println("network is error,please check," + e + "  (url=" + url + ")");
            if (callback != null) {
                callback.onError("network is error,please check");
            }
        } finally {
            if (connect != null)
                connect.disconnect();
        }
        return null;
    }

    /**
     * https不安全方式
     *
     * @param url
     * @param data
     * @param callback
     * @return
     */
    public static String postHttps(String url, String data, NetCallback callback) {
        String format = String.format("url:%s,data:%s", url, data);
        System.out.println(format);

        //  直接通过主机认证
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        //  配置认证管理器
        javax.net.ssl.TrustManager[] trustAllCerts = {new TrustAllTrustManager()};
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");

            SSLSessionContext sslsc = sc.getServerSessionContext();
            sslsc.setSessionTimeout(0);
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            //  激活主机认证
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        HttpsURLConnection connect = null;
        try {
            URL bootUrl = new URL(url);
            connect = (HttpsURLConnection) bootUrl.openConnection();
            connect.setRequestMethod("POST");
            connect.setConnectTimeout(CONNET_TIMEOUT);
            connect.setReadTimeout(READ_TIMEOUT);
            connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connect.setDoInput(true);
            connect.setDoOutput(true);
            OutputStream outputStream = connect.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            int responseCode = connect.getResponseCode();
            System.out.println("NetPost Status code=" + responseCode);
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                String line;
                String result = "";
                while ((line = br.readLine()) != null) {
                    result += line;
                }
                System.out.println("NetPost returnString = " + URLDecoder.decode(result, "UTF-8"));
                br.close();
                if (callback != null)
                    callback.onSuccess(result);
                return result;
            } else {
                System.out.println("NetPost server not response or other error info +");
                if (callback != null)
                    callback.onError("NetPost Status code " + responseCode + ",server not response or other error info");
                return null;
            }
        } catch (IOException e) {
            System.out.println("NetPost network is error,please check," + e + "  (url=" + url + ")");
            if (callback != null) {
                callback.onError("NetPost network is error,please check");
            }
        } finally {
            if (connect != null)
                connect.disconnect();
        }
        return null;
    }


    /**
     * get方式的参数
     *
     * @param map
     * @return
     */
    public static String encodeUrl(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String key : map.keySet()) {
                String value = map.get(key);
                sb.append(URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
                sb.append("&");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String parasm = sb.toString();
        if (parasm.endsWith("&")) {
            parasm = parasm.substring(0, parasm.length() - 1);
        }
        return parasm;
    }
}
