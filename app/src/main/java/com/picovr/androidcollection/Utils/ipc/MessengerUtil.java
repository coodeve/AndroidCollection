package com.picovr.androidcollection.Utils.ipc;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

public class MessengerUtil {

    public static final int MSG_SERVICE = 1;
    public static final int MSG_CLIENT = 2;

    /**
     * 服务端
     */
    private static class Server extends Service {

        private static class MessengerHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CLIENT:
                        Messenger client = msg.replyTo;
                        replyToClient(client);
                        break;
                }
            }

            private void replyToClient(Messenger message) {
                try {
                    message.send(generateMessage());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            private Message generateMessage() {
                Message obtain = Message.obtain(null, MSG_SERVICE);
                Bundle bundle = new Bundle();
                bundle.putString("reply", "welcome");
                obtain.setData(bundle);
                return obtain;
            }

        }

        private Messenger mMessenger = new Messenger(new MessengerHandler());

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return mMessenger.getBinder();
        }
    }

    /**
     * 客户端
     */
    private static class Client {

        private Context mContext;

        private Intent mIntent;

        private Messenger mService;

        private static class ClientHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {

            }
        }

        private Messenger mMessenger = new Messenger(new ClientHandler());

        public Client(Context context, Intent intent) {
            mContext = context;
            mIntent = intent;
        }

        private ServiceConnection mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = new Messenger(service);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        public void send(String msg) {
            Message obtain = Message.obtain(null, MSG_CLIENT);
            Bundle bundle = new Bundle();
            bundle.putString("reply", "hello");
            obtain.setData(bundle);
            // send client
            obtain.replyTo = mMessenger;
            try {
                mService.send(obtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void bind() {
            mContext.bindService(mIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }

        public void unBind() {
            mContext.unbindService(mServiceConnection);
        }

    }

}
