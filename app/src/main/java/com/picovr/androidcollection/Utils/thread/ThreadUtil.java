package com.picovr.androidcollection.Utils.thread;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

public class ThreadUtil {

    public static final String TAG = ThreadUtil.class.getSimpleName();

    /**
     * 根据线程名获得线程对象，native层会调用该方法，不能混淆     * @param threadName     * @return
     */
    public static Thread getThreadByName(String threadName) {
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

    /**
     * 线程是否阻塞
     *
     * @param thread
     * @return
     */
    public static boolean isBlocked(Thread thread) {
        return thread.getState() == Thread.State.BLOCKED;
    }

    /**
     * 向系统发送SIGNAL_QUIT信号，用于生产anr文件
     * 可以通过anr文件查看当前线程的卡顿情况
     */
    public void sendSignal() {
        Process.sendSignal(Process.myPid(), Process.SIGNAL_QUIT);
    }
}
