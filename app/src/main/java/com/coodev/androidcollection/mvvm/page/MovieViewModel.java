package com.coodev.androidcollection.mvvm.page;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.coodev.androidcollection.entity.Movies;

class MovieViewModel extends ViewModel {
    public LiveData<PagedList<Movies.Movie>> mPagedListLiveData;

    public MovieViewModel() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPageSize(MoviePositionDataSource.PRE_PAGE)
                .setPrefetchDistance(3)
                .setInitialLoadSizeHint(MoviePositionDataSource.PRE_PAGE * 4)
                .setMaxSize(Short.MAX_VALUE * MoviePositionDataSource.PRE_PAGE)
                .build();

        mPagedListLiveData = new LivePagedListBuilder<>(new MovieDataSourceFactory(),config).build();

    }
}
