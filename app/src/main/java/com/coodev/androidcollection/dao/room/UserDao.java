package com.coodev.androidcollection.dao.room;

import androidx.lifecycle.LiveData;
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

    /**
     * 结合LiveData
     *
     * @return
     */
    @Query("SELECT * FROM User")
    LiveData<List<User>> queryUsers();

    @Query("SELECT * FROM User WHERE id = :id")
    User queryUser(int id);
}
