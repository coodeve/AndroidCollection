package com.coodev.androidcollection.mvvm.page;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.coodev.androidcollection.entity.Movies;

class MoviePageKeyDataSource extends PageKeyedDataSource<Integer, Movies.Movie> {
    public static final int FIRST_PAGE = 1;
    public static final int PRE_PAGE = 8;

    /**
     * 页面首次加载
     * @param params
     * @param callback
     */
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Movies.Movie> callback) {
        // previousPageKey上一页的key，可以认为是页码，此处是null，因为是第一次加载，nextPageKey是下一页的页码
        callback.onResult(null,null,FIRST_PAGE+1);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movies.Movie> callback) {
        //
    }

    /**
     * 加载下一页
     * @param params
     * @param callback
     */
    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movies.Movie> callback) {
        // 如果下一页没有数据，则adjacentPageKey为null，表示请求完毕
        callback.onResult(null,null);
    }
}
