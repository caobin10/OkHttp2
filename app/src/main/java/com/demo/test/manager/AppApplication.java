package com.demo.test.manager;

import android.app.Application;

import com.demo.test.bean.User;

/**
 * Created by Administrator on 2021/8/11.
 */

public class AppApplication extends Application{

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
