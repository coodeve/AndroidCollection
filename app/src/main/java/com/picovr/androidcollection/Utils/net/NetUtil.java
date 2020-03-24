package com.picovr.androidcollection.Utils.net;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.picovr.androidcollection.Utils.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
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


    /**
     * 判断某个ip上的端口是否有TCP服务
     *
     * @param ip
     * @param port
     * @return
     */
    public static boolean isTakeUpPort(String ip, int port) {
        boolean takeUp = false;
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            Log.i(TAG, "isTakeUpPort: there is a server on port:" + port + " of " + ip);
            takeUp = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            takeUp = false;
        } finally {
            IOUtils.close(socket);
        }

        return takeUp;
    }


    /**
     * 指定要连接的主机和端口，
     * 可以作为强制软件许可认证的一部分
     *
     * @param targetIP         远程ip
     * @param targetPort       远程端口
     * @param netInterfaceName 本地host，可以是物理接口，也可以是虚拟机接口
     * @param localPort        本地端口，如果传入0，会随机选择1024~65535之间一个可用端口
     */
    public static Socket bindServerInterface(String targetIP, int targetPort, String localHost, int localPort) {
        Socket socket = null;
        try {
            InetAddress localAddress = InetAddress.getByName(localHost);
            socket = new Socket(targetIP, targetPort, localAddress, localPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return socket;
    }


    public static boolean isOpen(ServerSocket serverSocket) {
        return serverSocket.isBound() && !serverSocket.isClosed();
    }

    /**
     * 创建一个SSL的socket,可以强制转换为{@link SSLSocket}
     * <p>
     * 可以获取{@link SSLSocket#getSupportedCipherSuites()}
     * 和设置{@link SSLSocket#setEnabledCipherSuites(String[])}加密算法
     * getSupportedCipherSuites可以获取到Socket可用的算法组合，比如
     * TLS_KRB5_EXPORT_WITH_RC4_CBC_40_SHA256
     * 其组成部分是：协议、密钥交换算法、加密算法和校验
     *
     * @param host
     * @param port
     * @return
     */
    public static Socket getSSLSocket(String host, int port) {
        Socket socket = null;
        try {
            socket = SSLSocketFactory.getDefault().createSocket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return socket;
    }

    /**
     * 1.创建一个SSLContext
     * 2.为证书源创建一个TurstManagerFactory
     * 3.为密钥类型创建一个KeyManagerFactory
     * 4.为密钥和证书数据库创建一个KeyStore对象
     * 5.用密钥和证书填充KeyStore
     * 6.用KeyStore初始化KeyManagerFactory
     * 7.KeyManagerFactory，TrustManagerFactory，和一个随机来源初始化上下文
     */
    public static SSLSocketFactory getSimpleSSLFactorySocket() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");
            // 加载密钥库和密钥库的密码
            ks.load(null, null);
            kmf.init(ks, null);
            context.init(kmf.getKeyManagers(), null, null);

            return context.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回默认SSLSocketFactory
     *
     * @return
     */
    public static SSLSocketFactory getDefaultSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError();
        }
    }

    /**
     * 返回默认SSLSocketFactory
     *
     * @return
     */
    public static SSLSocketFactory getSSLSocketFactoryByTrustFactoryManager() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, null);
            return sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError();
        }
    }

    public static SSLContext getSSLContext(Context context) {
        try {
            // 证书
            InputStream inputStream = context.getAssets().open("test.cer");
            // 证书工厂
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(inputStream);
            // 加载证书到密钥库
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyStore.setCertificateEntry("cert", ca);
            // 加载密钥库到信任管理器
            String algorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            // 用 TrustManager 初始化一个 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);

            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
