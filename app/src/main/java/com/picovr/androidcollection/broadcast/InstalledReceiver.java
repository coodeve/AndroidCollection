package com.picovr.androidcollection.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class InstalledReceiver extends BroadcastReceiver {
    private Context mContext;
    private ReceiverCallback mCallback;

    public InstalledReceiver() {
    }


    public InstalledReceiver(Context context, ReceiverCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        try {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (action.equals("android.intent.action.PACKAGE_ADDED")) {     // install

                if (mCallback != null) {
                    mCallback.install(packageName);
                }
            } else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {   // uninstall
                if (mCallback != null) {
                    mCallback.uninstall(packageName);
                }
            } else if (action.equals("android.intent.action.INSTALL_PACKAGE")) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_UNINSTALL_PACKAGE);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        mContext.registerReceiver(this, intentFilter);
    }

    protected void unRegister() {
        mContext.unregisterReceiver(this);
    }

    public interface ReceiverCallback {
        void install(String installPackageName);

        void uninstall(String uninstallPackageName);
    }
}