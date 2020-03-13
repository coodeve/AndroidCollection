package com.picovr.androidcollection.Utils.system;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;

import java.lang.reflect.Method;

public class DisplayUtils {

    public static final String TAG = "DisplayUtils";

    private Context mContext;

    private DisplayManager mDisplayManager;

    private int mLaunchDisplayId = -1;

    private Display mLaunchDisplay;

    public DisplayUtils(Context context) {
        mContext = context;
    }

    private DisplayManager getDisplayManager() {
        if (mDisplayManager == null) {
            mDisplayManager = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
        }
        return mDisplayManager;
    }

    public boolean init() {

        mDisplayManager = getDisplayManager();
        for (Display display : mDisplayManager.getDisplays()) {
            final int displayId = display.getDisplayId();
            final int displayType = getDisplayType(display);
            Log.i(TAG, "openUPlus# display:" + displayId + "," + displayType);
            if (displayType != 1) {
                mLaunchDisplayId = displayId;
                mLaunchDisplay = display;
            }
        }

        Log.i(TAG, "openUPlus# mLaunchDisplayId:" + mLaunchDisplayId);

        if (mDisplayManager.getDisplays().length > 1 && mLaunchDisplayId != -1) {
            Log.i(TAG, "openUPlus# 连接到第二屏幕了");
            return true;
        }

        return false;
    }


    public int getLaunchDisplayId() {
        return mLaunchDisplayId;
    }

    public void startActivityOnDisplay(Class<? extends Activity> activity , int launchDisplayId){

        ActivityOptions activityOptions = null;
        if (launchDisplayId > 0) {
            activityOptions = ActivityOptions.makeBasic();
            activityOptions.setLaunchDisplayId(launchDisplayId);
        }

        Intent intent = new Intent(mContext, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        if (activityOptions != null) {
            mContext.startActivity(intent,activityOptions.toBundle());
        }else{
            mContext.startActivity(intent);
        }


    }


    public void startActivityOnDisplay(Class<? extends Activity> activity){
        startActivityOnDisplay(activity,getLaunchDisplayId());
    }



    /**
     * DISPLAY_TYPE_UNKNOWN = 0;
     * DISPLAY_TYPE_BUILT_IN = h1;
     * DISPLAY_TYPE_HDMI = 2;
     * DISPLAY_TYPE_WIFI = 3;
     * DISPLAY_TYPE_OVERLAY = 4;
     * DISPLAY_TYPE_VIRTUAL = 5;
     *
     * @param display
     * @return
     */
    private int getDisplayType(Display display) {
        try {
            Class<?> sysClass = Class.forName("android.view.Display");
            Method method = sysClass.getMethod("getType");
            Object obj = method.invoke(display);
            int displayType = Integer.valueOf(String.valueOf(obj)).intValue();
            return displayType;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
