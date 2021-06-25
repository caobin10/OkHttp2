package com.datab.cn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.datab.cn.db.BasicDaoMaster;
import com.datab.cn.db.BasicDaoSession;
import com.datab.cn.factory.DaoFactory;
import com.datab.cn.pojo.Unit;
import com.datab.cn.ui.UnitTextView;

public class MainActivity extends AppCompatActivity
{
    private UnitTextView unitTv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
        }catch (Exception e){
            e.printStackTrace();
        }

        unitTv= (UnitTextView) findViewById(R.id.tv_unit);

//        unitTv.setText("xx市2");
//        unitTv.setTag(new Unit("00000-00013-00098-00001", "xx市2", "00000-00013-00098", ""));
        BasicDaoMaster daoMaster = DaoFactory.getBasicDaoMaster(MainActivity.this);
        final BasicDaoSession daoSession = daoMaster.newSession();
        unitTv.setFinishCallBack(new UnitTextView.UnitCodeCallBack<Unit>() {
            @Override
            public void setCallBack(Unit unit) {

                unitTv.setText(unit.getUnitName());
                unitTv.setTag(unit);
            }
        });
    }
}
