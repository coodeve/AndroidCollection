package com.coodev.androidcollection.Utils.system;

import android.app.ActivityManager;
import android.app.IProcessObserver;
import android.content.Context;
import android.os.RemoteException;

import com.coodev.androidcollection.Utils.log.Logs;

import java.lang.reflect.Method;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 监听进程状态，需要系统权限
 * @author patrick.ding
 * @date 18/4/17
 */

public class ProcessController extends IProcessObserver.Stub {
    private static final String TAG = ProcessController.class.getSimpleName();
    private final ProcessControllerListener processControllerListener;
    private String mCLASSNAME = "android.app.ActivityManagerNative";
    private String mGetDefaultMethod = "getDefault";
    private String mRegisterProcessObserver = "registerProcessObserver";
    private final ActivityManager am;

    public ProcessController(Context context, ProcessControllerListener processControllerListener) {
        this.processControllerListener = processControllerListener;
        am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
    }

    @Override
    public void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities) throws RemoteException {
        if (processControllerListener != null && foregroundActivities) {
            processControllerListener.onForegroundActivitiesChanged(pid, uid, foregroundActivities);
        }
    }

    @Override
    public void onProcessStateChanged(int pid, int uid, int procState) throws RemoteException {

    }

    @Override
    public void onProcessDied(int pid, int uid) throws RemoteException {
        Logs.i(TAG, "onProcessDied: pid:" + pid + ", uid" + uid);
    }

    void registerProcessObserver() {
        try {
            Class<?> activityManagerNative = Class.forName(mCLASSNAME);
            Method getDefault = activityManagerNative.getMethod(mGetDefaultMethod);
            Object iActivityManager = getDefault.invoke(null);
            Method setActivityController = iActivityManager.getClass().getMethod(mRegisterProcessObserver, IProcessObserver.class);
            setActivityController.invoke(iActivityManager, this);
            Logs.i(TAG, "registerProcessObserver: reflect success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface ProcessControllerListener {
        void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities);
    }

}
