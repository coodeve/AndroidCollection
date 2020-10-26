package com.coodev.androidcollection.dao.greendao;

import android.content.Context;


import com.coodev.androidcollection.BuildConfig;
import com.coodev.androidcollection.entity.DaoMaster;
import com.coodev.androidcollection.entity.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * @author patrick.ding
 * @since 19/12/2
 * <p>
 * 数据库管理，使用greendao框架
 */
public class DaoManager {

    private static final String TAG = DaoManager.class.getSimpleName();

    private static final String DB_NAME = "coodev_dao";

    private static DaoMaster sDaoMaster;
    private static DaoMaster.DevOpenHelper sHelper;
    private static DaoSession sDaoSession;

    private Context mContext;

    private DaoManager() {
    }

    public static DaoManager getInstance() {
        return Holder.sDaoManager;
    }

    private static class Holder {
        private static DaoManager sDaoManager = new DaoManager();
    }

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public DaoMaster getDaoMaster() {
        if (sDaoMaster == null) {

            DaoMaster.OpenHelper openHelper = new DaoMaster.OpenHelper(mContext, DB_NAME) {
            };
            // 不加密
            sDaoMaster = new DaoMaster(openHelper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     *
     * @return
     */
    public DaoSession getDaoSession() {
        if (sDaoSession == null) {
            if (sDaoMaster == null) {
                sDaoMaster = getDaoMaster();
            }
            sDaoSession = sDaoMaster.newSession();
        }
        return sDaoSession;
    }

    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug() {
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    private void closeHelper() {
        if (sHelper != null) {
            sHelper.close();
            sHelper = null;
        }
    }

    private void closeDaoSession() {
        if (sDaoSession != null) {
            sDaoSession.clear();
            sDaoSession = null;
        }
    }

}
