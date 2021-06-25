package com.datab.cn.factory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.datab.cn.dao.UnitDao;
import com.datab.cn.db.BasicDaoMaster;
import com.datab.cn.db.BasicDaoSession;

/**
 * Created by Administrator on 2021/6/22.
 */

public class DaoFactory {
    public static BasicDaoMaster basicDaoMaster;
    private static BasicDaoSession basicDaoSession;

    public static BasicDaoMaster getBasicDaoMaster(Context context){
        if(basicDaoMaster==null){
            BasicDaoMaster.DevOpenHelper helper=new BasicDaoMaster.DevOpenHelper(context, null);
            SQLiteDatabase db=helper.getReadableDatabase();
            basicDaoMaster=new BasicDaoMaster(db);
        }
        return basicDaoMaster;
    }

    public static BasicDaoSession getBasicDaoSession(Context context) {
        if(basicDaoSession ==null){
            basicDaoSession = getBasicDaoMaster(context).newSession();
        }
        return basicDaoSession;
    }

    public static UnitDao getUnitDao(Context context){
        return  getBasicDaoSession(context).getUnitDao();
    }

}
