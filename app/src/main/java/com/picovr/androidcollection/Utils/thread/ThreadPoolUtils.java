package com.picovr.androidcollection.Utils.thread;

import android.text.TextUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author patrick.ding
 * @since 20/2/25
 */
public class ThreadPoolUtils {
    private static final String TAG = "ThreadPool";
    private static ThreadPoolUtils mInstance;
    private final ThreadPoolExecutor threadPoolExecutor;

    private ThreadPoolUtils() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().build();
        threadPoolExecutor = new ThreadPoolExecutor(10,
                50,
                10,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory);
    }

    public static ThreadPoolUtils getInstance() {
        if (mInstance == null) {
            synchronized ("") {
                if (mInstance == null) {
                    mInstance = new ThreadPoolUtils();
                }
            }
        }
        return mInstance;
    }

    public void execute(Runnable r) {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.submit(r);
        }
    }

    public class ThreadFactoryBuilder implements ThreadFactory {
        private int tcount = 0;
        private String name;
        private int priority = Thread.NORM_PRIORITY;

        public void setName(String name) {
            this.name = name;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public ThreadFactory build() {
            return this;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            if (!TextUtils.isEmpty(name)) {
                t.setName(name + (tcount++));
            }
            t.setPriority(priority);
            return t;
        }
    }
}
