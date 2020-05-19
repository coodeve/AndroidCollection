package com.picovr.androidcollection.Utils.net.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * websocket客户端
 * <p>
 * 可以在建立websocket的服务端，可以找到相关服务端框架
 * websocket的地址，一般使用 ws:// 开头
 */
public class WebSocketHelper {


    private WebSocket mWebSocket;
    private WebSocketListener webSocketListener;

    public void createWebsocket(OkHttpClient mOkHttpClient, Request request, WebSocketListener webSocketListener) {
        this.webSocketListener = webSocketListener;
        mWebSocket = mOkHttpClient.newWebSocket(request, webSocketListener);
    }

    public void send(String msg) {
        mWebSocket.send(msg);
    }

    public void cannel() {
        mWebSocket.cancel();
    }

    public void close() {
        mWebSocket.close(1000, null);
    }

    private void reConnect() {
        CooHttp.getOkhttpClient().newWebSocket(mWebSocket.request(), webSocketListener);
    }


}
