package com.coodev.androidcollection.mvvm.page;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.coodev.androidcollection.entity.Movies;

class MovieDataSourceFactory extends DataSource.Factory<Integer, Movies.Movie> {

    private MutableLiveData<MoviePositionDataSource> mMutableLiveData = new MutableLiveData<>();

    @NonNull
    @Override
    public DataSource<Integer, Movies.Movie> create() {
        mMutableLiveData.postValue(new MoviePositionDataSource());
        return null;
    }
}
