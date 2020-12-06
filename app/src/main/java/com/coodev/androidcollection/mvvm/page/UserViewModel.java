package com.coodev.androidcollection.mvvm.page;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.coodev.androidcollection.dao.room.User;
import com.coodev.androidcollection.dao.room.UserDatabase;

class UserViewModel extends AndroidViewModel {

    public static final int PRE_SIZE = 8;

    public LiveData<PagedList<User>> mPagedListLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        UserDatabase userDatabase = UserDatabase.getInstance(getApplication());
        mPagedListLiveData = new LivePagedListBuilder<>(userDatabase.userDao().getUser(),PRE_SIZE)
                .setBoundaryCallback(new UserBoundaryCallback())
                .build();
    }

    /**
     * 从头加载数据，就是刷新数据,比如，下拉刷新
     */
    public void refresh(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                UserDatabase.getInstance(getApplication()).userDao().clear();
            }
        });
    }


}
