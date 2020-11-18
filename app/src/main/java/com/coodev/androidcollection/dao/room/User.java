package com.coodev.androidcollection.dao.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 定义表
 */
@Entity(tableName = "User")
public class User {
    // 主键
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    public int id;

    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    public String name;

    @ColumnInfo(name = "age", typeAffinity = ColumnInfo.INTEGER)
    public int age;

    /**
     * Room 只能使用一个构造器，如果希望定义多个，那么使用Ignore标签，此标签还可用于字段
     *
     * @param id
     * @param name
     * @param age
     */
    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
