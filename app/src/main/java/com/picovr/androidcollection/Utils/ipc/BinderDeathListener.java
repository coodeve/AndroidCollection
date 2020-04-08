package com.picovr.androidcollection.Utils.ipc;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BinderDeathListener implements IBinder.DeathRecipient {
    public static final String TAG = BinderDeathListener.class.getSimpleName();

    private Binder mIBinder;

    public BinderDeathListener(Binder IBinder) {
        mIBinder = IBinder;
        mIBinder.linkToDeath(this, 0);
    }

    @Override
    public void binderDied() {
        Log.e(TAG, "binderDied: ");
        mIBinder.unlinkToDeath(this, 0);
        mIBinder = null;
    }

    public Binder getBinder() {
        return mIBinder;
    }
}
