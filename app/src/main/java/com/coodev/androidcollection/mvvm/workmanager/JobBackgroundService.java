package com.coodev.androidcollection.mvvm.workmanager;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.MainThread;

import java.util.concurrent.TimeUnit;

@SuppressLint("SpecifyJobSchedulerIdRange")
public class JobBackgroundService extends JobService {

    /**
     * open jobService
     *
     * @param context context
     * @param jobId   This ID must be unique
     */
    public static void openJobService(Context context, int jobId) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(context, JobBackgroundService.class));
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(10)); //执行的最小延迟时间
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(15));  //执行的最长延时时间
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);  //非漫游网络状态
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
        builder.setRequiresCharging(false); // 未充电状态
        jobScheduler.schedule(builder.build());
    }

    @MainThread
    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @MainThread
    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
