package com.coodev.androidcollection.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

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
            boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
            Log.i("NestServer", "action :" + action + "," + packageName);
            if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {   // uninstall
                Log.i("NestServer", "Uninstall :" + packageName + "," + replacing);
                if (replacing) {
                    return;
                }
                if (mCallback != null) {
                    mCallback.uninstall(packageName);
                }
            }

            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {     // install
                Log.i("NestServer", "Install :" + packageName + "," + replacing);
                if (replacing) {
                    return;
                }
                if (mCallback != null) {
                    mCallback.install(packageName);
                }
            }

            if (action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
                Log.i("NestServer", "Replaced :" + packageName + "," + replacing);
                if (mCallback != null) {
                    mCallback.replace(packageName);
                }
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

        void replace(String packageName);
    }
}