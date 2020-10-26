package com.coodev.androidcollection.Utils.net;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLManager {
    private static final String TAG = SSLManager.class.getSimpleName();

    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型 固定值
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型 固定值

    private static final String KEY_STORE_CLIENT_PATH = "client.p12";//客户端要给服务器端认证的证书
    private static final String KEY_STORE_TRUST_PATH = "client.truststore";//客户端验证服务器端的证书库
    private static final String KEY_STORE_PASSWORD = "123456";// 客户端证书密码
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";//客户端证书库密码

    public static final String MD5KEY = "11111111111111";
    private InputStream sTrustIn;


    /**
     * 主机认证，默认都通过
     */
    private static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };


    /**
     * 信任所有主机--对于任何证书都不做检查
     */
    private static class EmptyTurstManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static X509TrustManager getEmptyTrustManager() {
        return new EmptyTurstManager();
    }

    public static HostnameVerifier getEmptyHostNameVerifier() {
        return DO_NOT_VERIFY;
    }

    /**
     * Android HTTPS SSL双向验证
     *
     * @param context 上下文
     * @return SSLContext
     */
    private static SSLContext getSSLContextDeuble(Context context) {
        try {
            //服务端需要验证的客户端证书
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            //客户端信任的服务端证书
            KeyStore trustSotre = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
            //读取客户端要给服务器端认证的证书
            InputStream ksIn = context.getResources().getAssets().open(KEY_STORE_CLIENT_PATH);
            //读取客户端验证服务器端的证书库
            InputStream tsIn = context.getResources().getAssets().open(KEY_STORE_TRUST_PATH);

            try {
                keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
                trustSotre.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (ksIn != null) {
                    ksIn.close();
                }
                if (tsIn != null) {
                    tsIn.close();
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            // TrustManagerFactory 初始化
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustSotre);
            // KeyManagerFactory 初始化
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
            // sslContext 初始化
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            return sslContext;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Android HTTPS SSL单向验证,只验证服务端证书
     *
     * @param context 上下文
     * @return SSLContext
     */
    public static SSLContext getSSLContextSingle(Context context) {
        InputStream sTrustIn = null;
        try {
            //将服务端证书名称放到assest文件夹下
            sTrustIn = context.getResources().getAssets().open("trust.cer");
            //生产自己的CA
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(sTrustIn);
            Log.i(TAG, "ca=" + ((X509Certificate) certificate).getSubjectDN());

            //创建一个证书库
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);


            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sTrustIn != null) {
                try {
                    sTrustIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * Android HTTPS SSL  服务端和客户端都不进行验证
     *
     * @param context 上下文
     * @return SSLContext
     */
    public static SSLContext getSSLContextNone() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new EmptyTurstManager()}, null);
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 获取一个服务端和客户端都不进行验证的httpsurlconnection
     *
     * @param context
     * @param url
     * @param method
     * @return
     */
    public static HttpsURLConnection getHttpsUrlConnection(Context context, String url, String method) {
        URL bootUrl;
        HttpsURLConnection connection = null;
        try {
            SSLContext sslContext = getSSLContextSingle(context);
            if (sslContext != null) {
                bootUrl = new URL(url);
                connection = (HttpsURLConnection) bootUrl.openConnection();
                connection.setRequestMethod(method);
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setSSLSocketFactory(getSSLContextNone().getSocketFactory());
                connection.setHostnameVerifier(DO_NOT_VERIFY);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

}