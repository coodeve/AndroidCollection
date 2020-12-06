package com.coodev.androidcollection.mvvm.page;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import com.coodev.androidcollection.entity.Movies;

/**
 * PositionDataSource用法
 */
class MoviePositionDataSource extends PositionalDataSource<Movies.Movie> {
    /**
     * 页大小
     */
    public static final int PRE_PAGE = 8;

    /**
     * 页面首次加载数据时会调用此方法
     * @param params
     * @param callback
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Movies.Movie> callback) {
        // 网络请求
        int startPosition = 0;
        callback.onResult(null,0,0);
    }

    /**
     * 后续加载时调用，类似"加载更多"
     * @param params
     * @param callback
     */
    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Movies.Movie> callback) {
        int startPosition = params.startPosition;
        callback.onResult(null);
    }
}
