package com.picovr.androidcollection.Utils.ipc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class UdpHelper {

    private static final String TAG = "UdpHelper";
    private DatagramSocket mTrialSocket;
    private DatagramPacket mInPack;
    private static final int TRIAL_PORT = 9090;
    private static final int SO_TIME_OUT = 2 * 1000;
    private static final int BUF_SIZE = 1024;

    public UdpHelper() {
//        initSocket();
        initPacket();
    }

    public void initSocket() {
        if (mTrialSocket != null) {
            return;
        }
        try {
            if (mTrialSocket == null) {
                mTrialSocket = new DatagramSocket(null);
                mTrialSocket.setReuseAddress(true);
                mTrialSocket.bind(new InetSocketAddress(TRIAL_PORT));
                mTrialSocket.setSoTimeout(SO_TIME_OUT);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void initPacket() {
        // 非空不初始化
        mInPack = (mInPack == null) ? new DatagramPacket(new byte[BUF_SIZE], BUF_SIZE) : mInPack;
    }

    public void receivePack(IPacketDataCallback callback) {
        if (mTrialSocket == null) {
            initSocket();
        }
        try {
            mTrialSocket.receive(mInPack);
            callback.onReceived(mInPack);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onException(e);
        }
    }

    public interface IPacketDataCallback {
        void onReceived(DatagramPacket packet);

        void onException(Exception e);
    }

    public void release() {
        if (mTrialSocket != null) {
            while (!mTrialSocket.isClosed()) //保证socket关闭
            {
                mTrialSocket.close();
            }
        }
        mTrialSocket = null;
    }
}
