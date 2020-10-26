package com.coodev.androidcollection.Utils.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.pvr.verify.ICallback;
import com.pvr.verify.IVerify;

public class VerifyService extends Service {

    public static final String TAG = "VerifyService";

    public static int RESULT = 0;

    private RemoteCallbackList<ICallback> mRemoteCallbackList = new RemoteCallbackList<>();

    private ICallback mICallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate# ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new VerifyBind();
    }

    private class VerifyBind extends IVerify.Stub {


        @Override
        public int verify(String appid, String packageName, String publicKey) throws RemoteException {
            return verifyApp(appid, packageName, publicKey);
        }

        @Override
        public void verifyAsync(String appid, String packageName, String publicKey, ICallback callback) throws RemoteException {
            synchronized (VerifyService.class) {
                if (mICallback != null) {
                    mRemoteCallbackList.unregister(mICallback);
                    mICallback = null;
                }

                mICallback = callback;

                mRemoteCallbackList.register(mICallback);
            }

            verifyAppAsync(appid, packageName, publicKey);
        }
    }

    /**
     * 同步校验
     */
    private int verifyApp(String appid, String packageName, String publicKey) {
        return 0;
    }


    /**
     * 异步校验
     *
     * @param appid
     * @param packageName
     * @param publicKey
     */
    private void verifyAppAsync(final String appid, final String packageName, final String publicKey) {
        call(0);
    }

    private void call(int code) {
        mRemoteCallbackList.beginBroadcast();

        int registeredCallbackCount = mRemoteCallbackList.getRegisteredCallbackCount();
        ICallback broadcastItem = null;
        for (int i = 0; i < registeredCallbackCount; i++) {
            broadcastItem = mRemoteCallbackList.getBroadcastItem(i);
            if (broadcastItem == null) {
                continue;
            }

            if (mICallback != null && mICallback == broadcastItem) {
                try {
                    broadcastItem.callback(code);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        mRemoteCallbackList.finishBroadcast();
    }

}
