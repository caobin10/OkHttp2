package com.datab.cn.manager;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.datab.cn.bean.User;

/**
 * Created by Administrator on 2021/6/23.
 */

public class AppApplication extends MultiDexApplication {
    private User user;
    private static Context mContext;

    public static AppApplication instance;

    public static AppApplication getInstance() {
        return (AppApplication) instance;
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 上下文
        mContext = getApplicationContext();
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Context getmContext() {
        return mContext;
    }
}
