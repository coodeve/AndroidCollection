package com.coodev.androidcollection.dao.room;

import android.service.autofill.Dataset;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 定义数据库操作
 */
@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Delete
    void delete(User user);

    @Update
    void update(User user);

    @Query("DELETE FROM User")
    void clear();

    /**
     * 结合LiveData
     *
     * @return
     */
    @Query("SELECT * FROM User")
    LiveData<List<User>> queryUsers();

    @Query("SELECT * FROM User WHERE id = :id")
    User queryUser(int id);

    /**
     * 结合Paging
     * @return
     */
    @Query("SELECT * FROM user")
    DataSource.Factory<Integer,User> getUser();
 }
