package com.picovr.androidcollection.Utils;

import android.app.IActivityController;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 监听Activity状态，需要系统权限
 * @author patrick.ding
 * @date 2017/9/27
 */
public class ActivityController extends IActivityController.Stub{
    private static final String TAG = ActivityController.class.getSimpleName();
    private String mCLASSNAME =  "android.app.ActivityManagerNative";
    private String mGetDefaultMethod =  "getDefault";
    private String mSetActivityController =  "setActivityController";
    private ActivityControllerCallback mCallback;
    public ActivityController() {}

    public void setSetActivityController(ActivityControllerCallback callback){

        mCallback = callback;

        try {
            Class<?> activityManagerNative = Class.forName(mCLASSNAME);
            Method getDefault = activityManagerNative.getMethod(mGetDefaultMethod);
            Object iActivityManager = getDefault.invoke(null);
            Method setActivityController = iActivityManager.getClass().getMethod(mSetActivityController, IActivityController.class,boolean.class);
            setActivityController.invoke(iActivityManager,this,false);
            Logs.i(TAG, "ActivityController: reflect success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //这个Method中，反射获取到的方法，再Finch设备中，不存在，反射获取到的方法，请参考setSetActivityController中的方式
    private void removeActivityController(){
        mCallback = null;

        try {
            Class<?> activityManagerNative = Class.forName(mCLASSNAME);
            Method getDefault = activityManagerNative.getMethod(mGetDefaultMethod);
            Object iActivityManager = getDefault.invoke(null);
            Method setActivityController = iActivityManager.getClass().getMethod(mSetActivityController, IActivityController.class);
            setActivityController.invoke(iActivityManager,new Object[]{null});
            Logs.i(TAG, "ActivityController: reflect success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean activityStarting(Intent intent, String pkg) throws RemoteException {
        if(mCallback != null)
            mCallback.activityStarting(intent,pkg);
        return true;
    }

    @Override
    public boolean activityResuming(String pkg) throws RemoteException {
        Log.i(TAG, "activityResuming-- "+pkg);
        if(mCallback != null)
            mCallback.activityResuming(pkg);
        return true;
    }

    @Override
    public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg, long timeMillis, String stackTrace) throws RemoteException {
        return true;
    }

    @Override
    public int appEarlyNotResponding(String processName, int pid, String annotation) throws RemoteException {
        return 0;
    }

    @Override
    public int appNotResponding(String processName, int pid, String processStats) throws RemoteException {
        return 0;
    }

    @Override
    public int systemNotResponding(String msg) throws RemoteException {
        return 0;
    }

    public interface ActivityControllerCallback{
        void activityStarting(Intent intent, String pkg);
        void activityResuming(String pkg);
    }

}
