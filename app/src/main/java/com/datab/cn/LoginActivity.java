package com.datab.cn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.datab.cn.bean.User;
import com.datab.cn.manager.AppApplication;
import com.datab.cn.manager.AppConfig;
import com.datab.cn.manager.AppManager;
import com.datab.cn.pojo.Unit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2021/6/24.
 */

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Map<String, Boolean> dbMap=new HashMap<>();
        dbMap.put(AppConfig.DB_BASIC_NAME, true);
        try {
            AppManager.getAppManager().copyDbFileFromAsset(LoginActivity.this, dbMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void btnLogin(View view){
        User user = new User();
        Unit unit = new Unit();
        unit.setUnitCode("00000-00013-00098-00001");
        unit.setUnitName("xx市2");
        unit.setParentCode("00000-00013-00098");
        user.setUnit(unit);
        AppApplication app = AppApplication.instance;
        AppApplication.instance.setUser(user);

        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
