package org.chuck.downmanager;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig downloadDaoConfig;

    private final DownloadDao downloadDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        downloadDaoConfig = daoConfigMap.get(DownloadDao.class).clone();
        downloadDaoConfig.initIdentityScope(type);

        downloadDao = new DownloadDao(downloadDaoConfig, this);

        registerDao(DownloadDBEntity.class, downloadDao);
    }
    
    public void clear() {
        downloadDaoConfig.getIdentityScope().clear();
    }

    public DownloadDao getDownloadDao() {
        return downloadDao;
    }

}
