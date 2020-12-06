package com.coodev.androidcollection.mvvm.page;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coodev.androidcollection.entity.Movies;

class MoveActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MoviePageListAdapter mMoviePageListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(this);
        setContentView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mMoviePageListAdapter = new MoviePageListAdapter(this);
        MovieViewModel movieViewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        movieViewModel.mPagedListLiveData.observe(this, new Observer<PagedList<Movies.Movie>>() {
            @Override
            public void onChanged(PagedList<Movies.Movie> movies) {
                mMoviePageListAdapter.submitList(movies);// 数据提交
            }
        });

        mRecyclerView.setAdapter(mMoviePageListAdapter);

    }
}
