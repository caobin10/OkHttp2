package com.datab.cn.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.datab.cn.dao.UnitDao;
import com.datab.cn.manager.AppConfig;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

/**
 * Created by Administrator on 2021/6/22.
 */

public class BasicDaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1000;
    public BasicDaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UnitDao.class);
    }
    public static void createTables(SQLiteDatabase db, boolean ifNotExists){
//		UnitDao.createTable(db, ifNotExists);
    }
    public static void dropTables(SQLiteDatabase db, boolean ifExists){
//		UnitDao.dropTable(db, ifExists);
    }
    @Override
    public BasicDaoSession newSession() {
        return new BasicDaoSession(db, IdentityScopeType.Session,daoConfigMap);
    }

    @Override
    public BasicDaoSession newSession(IdentityScopeType type) {
        return new BasicDaoSession(db,type,daoConfigMap);
    }

    public static abstract class OpenHelper extends SQLiteOpenHelper {
        private static final int VERSION = 1;
        private static final String DBNAME= AppConfig.DB_BASIC_NAME;
        public OpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
            super(context, DBNAME, factory, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("onCreate", "onCreate");
            createTables(db, false);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.disableWriteAheadLogging();  // Here the solution
            }
            super.onOpen(db);
        }
    }

    public static class DevOpenHelper extends OpenHelper{
        public DevOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
            super(context, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("onUpgrade", "onUpgrade");
            dropTables(db, true);
            onCreate(db);
        }
    }
}
