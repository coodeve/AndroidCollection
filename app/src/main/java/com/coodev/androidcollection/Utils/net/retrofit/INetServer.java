package com.coodev.androidcollection.Utils.net.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

public interface INetServer {

    /**
     * post请求示例
     * 使用rxjava
     *
     * @Headers 这个用于动态URL配置
     */
    @Headers(CooRetrofit.CHANGE_URL_HEAD_NAME + ":" + "url的key")
    @FormUrlEncoded
    @POST("url的path部分")
    Observable<BaseResponse> getUserInfo(
            @Field("user_token") String user_token,
            @Field("uquid") String page);

    /**
     * post请求示例
     *
     * @Headers 这个用于动态URL配置
     */
    @Headers(CooRetrofit.CHANGE_URL_HEAD_NAME + ":" + "url的key")
    @FormUrlEncoded
    @POST("url的path部分")
    Call<BaseResponse> getUser(
            @Field("user_token") String user_token,
            @Field("uquid") String page);

    /**
     * get请求
     *
     * @return
     */
    @GET("url的path部分")
    Call<BaseResponse> getUer();
}
