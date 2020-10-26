package com.coodev.androidcollection.dao.greendao;

import android.util.Log;


import com.coodev.androidcollection.App;
import com.coodev.androidcollection.entity.Info;
import com.coodev.androidcollection.entity.InfoDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * @author patrick.ding
 * @since 19/12/2
 */
public class DaoUtil {

    private static final String TAG = DaoUtil.class.getSimpleName();

    private static DaoManager sDaoManager = DaoManager.getInstance();


    /**
     * app 数据写入，如果为创建表，则先行创建表
     *
     * @param app
     * @return
     */
    public static long insert(Info app) {
        long insert = sDaoManager.getDaoSession().getInfoDao().insert(app);
        Log.i(TAG, "insert# insert app:" + (insert != -1));
        return insert;
    }

    public static long insertOrUpdate(Info app) {
        long insert = sDaoManager.getDaoSession().getInfoDao().insertOrReplace(app);
        Log.i(TAG, "insert# insert app:" + (insert != -1));
        return insert;
    }

    public static boolean insertMultiApp(List<Info> appList) {
        for (Info app : appList) {
            sDaoManager.getDaoSession().getInfoDao().insertOrReplace(app);
        }

        return true;
    }

    public static boolean updateApp(Info app) {
        sDaoManager.getDaoSession().update(app);
        return true;
    }

    public static boolean deleteApp(Info app) {
        sDaoManager.getDaoSession().delete(app);
        return true;
    }

    public static boolean deleteAll() {
        sDaoManager.getDaoSession().deleteAll(Info.class);
        return true;
    }

    public static List<Info> queryAllApp() {
        return sDaoManager.getDaoSession().loadAll(Info.class);
    }

    public static Info queryAppByID(long id) {
        return sDaoManager.getDaoSession().load(Info.class, id);
    }

    public static List<App> queryAppBySQL(String sql, String[] conditions) {
        return sDaoManager.getDaoSession().queryRaw(App.class, sql, conditions);
    }
    
}
