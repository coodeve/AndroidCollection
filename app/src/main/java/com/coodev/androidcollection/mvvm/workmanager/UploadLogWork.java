package com.coodev.androidcollection.mvvm.workmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * 定义任务
 */
public class UploadLogWork extends Worker {
    public UploadLogWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    /**
     * 异步执行方法，代码在这个里面编写
     *
     * @return
     */
    @NonNull
    @Override
    public Result doWork() {
        // 获取传入的数据
        final Data inputData = getInputData();
//        final Data build = new Data.Builder().putString("result", "success").build();
//        return Result.success(build);// 任务完成后返回数据
        return Result.success();
    }

}
