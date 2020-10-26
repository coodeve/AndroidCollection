package com.coodev.androidcollection.Utils.net.retrofit;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;

public class RemoteLoader {
    private final INetServer mEventNetInterface;


    public RemoteLoader() {
        mEventNetInterface = CooRetrofit.getInstance().create(INetServer.class);
    }

    /**
     * 使用rxjava
     *
     * @param userToken
     * @param quiuid
     * @param responseObserver
     */
    public void getUserInfo(String userToken, String quiuid, Observer<BaseResponse> responseObserver) {
        Observable<BaseResponse> userInfo = mEventNetInterface.getUserInfo(userToken, quiuid);
        userInfo.subscribe(responseObserver);
    }

    /**
     * 使用call进行异步请求
     */
    public void getUser() {
        Call<BaseResponse> uer = mEventNetInterface.getUer();
        uer.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {

            }
        });

        // 也可进行同步请求
//        try {
//            Response<BaseResponse> execute = uer.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
