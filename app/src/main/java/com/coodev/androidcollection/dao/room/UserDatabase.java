package com.coodev.androidcollection.dao.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * 创建数据库
 */
@Database(entities = {User.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {

    private static final String DB_NAME = "db_name";
    private static UserDatabase mInstance;

    public static synchronized UserDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context, UserDatabase.class, DB_NAME).build();
            // 升级方案，使用Migration，注意修改version
//            mInstance = Room.databaseBuilder(context, UserDatabase.class, DB_NAME)
//                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
//                    .fallbackToDestructiveMigration()// 防止升级失败引发的异常(由少了部分migration引起)
//                    .build();
            // 本地数据库读取
//            mInstance = Room.databaseBuilder(context,UserDatabase.class,DB_NAME)
//                    .createFromAsset("本地数据库地址")
//                    .build();
        }
        return mInstance;
    }

    public abstract UserDao userDao();

    /**
     * 升级时有表结构修改，最好的策略是 销毁与重建
     * 1. 创建临时表
     * 2. 将原表数据拷贝到临时表中
     * 3. 删除原表
     * 4. 重命名临时表
     */
    public final static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE temp_user (" +
                    "id INTEGER PRIMARY KEY NOT NULL," +
                    "name TEXT," +
                    "age TEXT)");
            database.execSQL("INSERT INTO temp_user (id,name,age) " +
                    "SELECT id ,name,age FROM user");
            database.execSQL("DROP TABLE user");
            database.execSQL("ALTER TABLE temp_user RENAME TO user");
        }
    };

    public final static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

}
