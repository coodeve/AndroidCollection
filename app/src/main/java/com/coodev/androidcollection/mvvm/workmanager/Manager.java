package com.coodev.androidcollection.mvvm.workmanager;

import android.content.Context;

import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Manager {
    /**
     * 设置触发条件
     */
    Constraints mConstraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build();

    /**
     * 一次性
     *
     * @return
     */
    private OneTimeWorkRequest createOneTimeRequest() {
        return new OneTimeWorkRequest.Builder(UploadLogWork.class)
                .setConstraints(mConstraints)
                .addTag(UploadLogWork.class.getSimpleName())
//                .setInitialDelay(10) 没有符合的条件的，可以设置延迟执行
                .build();
    }

    /**
     * 周期性，间隔不少于15分钟
     *
     * @return
     */
    private PeriodicWorkRequest createPeriodicRequest() {
        return new PeriodicWorkRequest.Builder(UploadLogWork.class, 10, TimeUnit.HOURS)
                .setConstraints(mConstraints)
                .addTag(UploadLogWork.class.getSimpleName())
//                .setInitialDelay(10) 没有符合的条件的，可以设置延迟执行
                .build();
    }

    /**
     * workmanager 向 work传递数据
     * {@link UploadLogWork#doWork()}
     */
    public void sendDataToWord() {
        // 不能超过10Kb
        final Data value = new Data.Builder().putInt("value", 1).build();
        final OneTimeWorkRequest build = new OneTimeWorkRequest.Builder(UploadLogWork.class)
                .setInputData(value) // 设置传入数据
                .build();
        post(null, build);
    }

    /**
     * 加入执行
     *
     * @param context
     * @param workRequest
     */
    public void post(Context context, WorkRequest workRequest) {
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    /**
     * 取消执行
     *
     * @param context
     */
    public void cancel(Context context, String tag) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag);
    }

    /**
     * 有类似获取任务的方法
     *
     * @param context
     * @param tag
     * @return
     */
    public ListenableFuture<List<WorkInfo>> getWord(Context context, String tag) {
        return WorkManager.getInstance(context).getWorkInfosByTag(tag);
    }

    /**
     * 通过livedata观察
     *
     * @param context
     * @param id
     */
    public void observe(Context context, UUID id) {
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(id)
                .observe(null, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED){
                            final Data outputData = workInfo.getOutputData();// 获取任务返回的Data
                        }
                    }
                });
    }
}
