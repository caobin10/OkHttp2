package com.datab.cn.db;

import android.database.sqlite.SQLiteDatabase;

import com.datab.cn.dao.UnitDao;
import com.datab.cn.pojo.Unit;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by Administrator on 2021/6/22.
 */

public class BasicDaoSession extends AbstractDaoSession {
    private DaoConfig unitConfig;
    private UnitDao unitDao;
    public BasicDaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap) {
        super(db);
        unitConfig=daoConfigMap.get(UnitDao.class).clone();
        unitDao=new UnitDao(unitConfig,this);
        registerDao(Unit.class, unitDao);
    }
    public void clear(){
        unitConfig.getIdentityScope().clear();
    }

    public UnitDao getUnitDao(){
        return unitDao;
    }

}
