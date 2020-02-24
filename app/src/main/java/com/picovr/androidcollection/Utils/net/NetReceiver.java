package com.picovr.androidcollection.Utils.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

/**
 * @author patrick.ding
 * @since 20/2/24
 */
public class NetReceiver {
    /**
     * 防重复状态标志位
     */
    private boolean flag = false;

    private Context mContext;

    private OnNetworkStateChangeListener mListener;

    public NetReceiver(Context context, OnNetworkStateChangeListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    /**
     * 注册
     */
    public void register() {
        if (mContext == null) {
            return;
        }
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest nr = new NetworkRequest.Builder().build();

        cm.requestNetwork(nr, new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);

                if (!flag) {
                    flag = true;
                    if (mListener != null) {
                        mListener.onNetworkStateChange(true);
                    }
                }
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);

                if (flag) {
                    flag = false;
                    if (mListener != null) {
                        mListener.onNetworkStateChange(false);
                    }
                }
            }

        });
    }

    public interface OnNetworkStateChangeListener {

        /**
         * 网络状态变化
         *
         * @param connect
         */
        void onNetworkStateChange(boolean connect);

    }

}
