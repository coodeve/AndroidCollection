package com.picovr.androidcollection.Utils.net.retrofit;

import android.text.TextUtils;

import com.picovr.androidcollection.Utils.log.Logs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CooRetrofit {

    private static final String TAG = CooRetrofit.class.getSimpleName();
    private final static long CONNECT_TIME = 10;
    private final static long READ_TIME = 10;
    /**
     * 动态替换url的url池
     */
    private static HashMap<String, String> baseUrlMap = new HashMap<>();

    /**
     * 目标地址
     */
    private final static String BASE_URL = "";

    private final Retrofit mRetrofit;

    public static final String CHANGE_URL_HEAD_NAME = "base_url";

    private CooRetrofit() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.dns(new IPV4D());
        builder.connectTimeout(CONNECT_TIME, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME, TimeUnit.SECONDS);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addNetworkInterceptor(httpLoggingInterceptor);
        builder.addInterceptor(new CustomInterceptor());// 在发送前处理

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(builder.build())
                .build();

    }

    private static class CooRetrofitHolder {
        private static final CooRetrofit COO_RETROFIT = new CooRetrofit();
    }

    public static CooRetrofit getInstance() {
        return CooRetrofitHolder.COO_RETROFIT;
    }

    /**
     * 创建interface
     *
     * @param interfaces
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> interfaces) {
        return mRetrofit.create(interfaces);
    }

    /**
     * 过滤掉ipv6地址,只保留ipv4地址
     */
    private class IPV4D implements Dns {
        private static final String IP_4 = "[0-9]{0,3}.[0-9]{0,3}.[0-9]{0,3}.[0-9]{0,3}";

        @Override
        public List<InetAddress> lookup(String hostname) throws UnknownHostException {
            InetAddress[] inetAddresses = InetAddress.getAllByName(hostname);
            List<InetAddress> inetAddressList = new ArrayList<InetAddress>();
            for (int i = 0; i < inetAddresses.length; i++) {
                String ip = inetAddresses[i].getHostAddress();
                if (ip.matches(IP_4)) {
                    inetAddressList.add(inetAddresses[i]);
                }
            }

            return inetAddressList;
        }
    }

    public class HttpLogger implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            Logs.i(TAG, message);
        }
    }

    /**
     * 用于添加公用参数信息,动态切换url
     * 方法是通过增加http的header进行判断，然后动态替换url
     * header的key必须是
     * url地址通过{@link #baseUrlMap}进行添加
     */
    private class CustomInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl.Builder builder = request.url().newBuilder();
            String model = request.header(CHANGE_URL_HEAD_NAME);
            if (!TextUtils.isEmpty(model)) {
                URL targetURL = new URL(baseUrlMap.get(model));
                if (targetURL.getPort() != -1) {
                    builder.port(targetURL.getPort());
                }
                builder.host(targetURL.getHost());

            }
            HttpUrl httpUrl = builder.build();
            request = request.newBuilder()
                    .url(httpUrl)
                    .method(request.method(), request.body())
                    .build();
            return chain.proceed(request);
        }

    }

    /**
     * 将url地址加入
     *
     * @param key      url地址标识，会用在header中，然后进行判断获取
     * @param modelUrl 对应地址
     */
    public static void addBaseUrl(String key, String modelUrl) {
        if (modelUrl.contains("http") || modelUrl.contains("https")) {
            baseUrlMap.put(key, modelUrl);
        } else {
            Logs.e(TAG, "initBaseUrlCollections# modelUrl is invalid.");
        }
    }

}
