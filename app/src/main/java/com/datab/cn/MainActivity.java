package com.datab.cn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import sqlite.RecordSQLiteOpenHelper;
import view.FlowViewGroup;

public class MainActivity extends AppCompatActivity
{

    TextView tv;
    private ArrayList<String> list=new ArrayList<String>();

    private FlowViewGroup mFlowViewGroup;
    private EditText et_search;
    private TextView tv_tip;
    private TextView tv_clear;
    private RecordSQLiteOpenHelper helper = new RecordSQLiteOpenHelper(this);
    ;
    private SQLiteDatabase db;
    private ImageButton Pib01;

    public MainActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控件
        initView();
        Intent intent = getIntent();
        // 清空搜索历史
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
                initDatas("");
                mFlowViewGroup.setVisibility(View.INVISIBLE);
                finish();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        // 搜索框的键盘搜索键点击回调
        et_search.setOnKeyListener(new View.OnKeyListener() {// 输入完后按键盘上的搜索键

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                    boolean hasData = hasData(et_search.getText().toString().trim());
                    if (!hasData) {
                        insertData(et_search.getText().toString().trim());
                        queryData_(String.valueOf(et_search.getText()));
                    }
                    finish();
                    Intent intent = new Intent(MainActivity.this,MainActivity.class);
                    intent.putExtra("ss",et_search.getText().toString().trim());
                    startActivity(intent);
                }
                return false;
            }
        });
        // 搜索框的文本变化实时监听
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String tempName = et_search.getText().toString();
                // 根据tempName去模糊查询数据库中有没有数据
                //initDatas(tempName);

            }
        });


        // 插入数据，便于测试，否则第一次进入没有数据怎么测试呀？
        insertData(null);

        /*// 第一次进入查询所有的历史记录
        queryData("");*/
        initDatas("");
    }



    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        if (tempName!=null) {
            db = helper.getWritableDatabase();
            db.execSQL("insert into records(name) values('" + tempName + "')");
            db.close();
        }
    }

    /**
     * 模糊查询数据
     */
    private void queryData_(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);
        if (cursor.moveToNext()) {
            cursor.moveToFirst();
            int a=0;
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Log.i("TAG", "表searchHisTab的name=" + name);
                if (name != null && !name.equals("")) {
                    list.add(name);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }



    /**
     * 检查数据库中是否已经有该条记录
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    private void initView() {
        mFlowViewGroup = (FlowViewGroup) findViewById(R.id.flowlayout);
        et_search = (EditText) findViewById(R.id.et_search);
        tv_clear = (TextView) findViewById(R.id.tv_clear);
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.i("---","搜索操作执行");
                   /* jsonArray1=null;
                    init2(null);*/
                }
                return false;
            }
        });

    }

    /**
     * 隐藏软键盘
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }

        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    private void initDatas(String name) {
        queryData_(name);
        for (int i=0;i<list.size();i++){
            tv= (TextView) LayoutInflater.from(this).inflate(R.layout.item_flow,mFlowViewGroup,false);
            tv.setText(list.get(i));
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(Pdd_search.this,""+((TextView)v).getText(),Toast.LENGTH_SHORT).show();
                    et_search.setText(((TextView)v).getText());
                }
            });
            mFlowViewGroup.addView(tv);
        }
    }

}
