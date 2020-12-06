package com.coodev.androidcollection.mvvm.page;

import androidx.annotation.NonNull;
import androidx.paging.ItemKeyedDataSource;

import com.coodev.androidcollection.entity.Movies;

class MovieItemKeyDataSource extends ItemKeyedDataSource<Integer, Movies.Movie> {

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Movies.Movie> callback) {
        int key = 0;
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Movies.Movie> callback) {
        Integer next = params.key;
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Movies.Movie> callback) {

    }

    @NonNull
    @Override
    public Integer getKey(@NonNull Movies.Movie item) {
        return Integer.valueOf(item.id);
    }
}
