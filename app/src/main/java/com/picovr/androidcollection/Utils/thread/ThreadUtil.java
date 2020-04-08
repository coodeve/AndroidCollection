package com.picovr.androidcollection.Utils.thread;

import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

public class ThreadUtil {

    public static final String TAG = ThreadUtil.class.getSimpleName();

    /**
     * 根据线程名获得线程对象，native层会调用该方法，不能混淆     * @param threadName     * @return
     */
    static Thread getThreadByName(String threadName) {
        if (TextUtils.isEmpty(threadName)) {
            return null;
        }
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        Thread theThread = null;
        for (Thread thread : threadArray) {
            if (thread.getName().equals(threadName)) {
                theThread = thread;
            }
        }
        Log.d(TAG, "threadName: " + threadName + ", thread: " + theThread);
        return theThread;
    }
}
