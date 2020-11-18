package com.coodev.androidcollection.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.coodev.androidcollection.dao.room.User;
import com.coodev.androidcollection.dao.room.UserDao;
import com.coodev.androidcollection.dao.room.UserDatabase;

import java.util.List;

/**
 * ViewModel和LiveData及Room结合使用
 */
public class RoomViewModel extends AndroidViewModel {

    private final UserDao mUserDao;
    private LiveData<List<User>> mListLiveData;

    public RoomViewModel(@NonNull Application application) {
        super(application);
        mUserDao = UserDatabase.getInstance(application).userDao();
        mListLiveData = mUserDao.queryUsers();
    }


    public LiveData<List<User>> getListLiveData() {
        return mListLiveData;
    }


}
