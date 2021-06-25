package com.datab.cn.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.datab.cn.R;
import com.datab.cn.adapter.Solve7UnitTreeAdapter;
import com.datab.cn.bean.User;
import com.datab.cn.manager.AppApplication;
import com.datab.cn.pojo.Unit;

import java.util.ArrayList;
import java.util.List;

import test.tree.adapter.Solve7ITreeAllAdapter;
import test.tree.common.ViewHolder;

/**
 * Created by Administrator on 2021/6/21.
 */

public class UnitTextView extends TextView {

    private User user;
    private UnitCodeCallBack finishRecorderCallBack;
    private Solve7PopupWindow popupWindow;

    public UnitTextView(Context context) {
        this(context, null);
        init(context);
    }

    public UnitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {

        AppApplication application = (AppApplication) context.getApplicationContext();

        user = application.getUser();
        user.getUnit().setRemark(null);
        createUnitTree(context, user.getUnit());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //弹出前  隐藏键盘 防止显示位置错误
                InputMethodManager imm = (InputMethodManager)((Activity)context). getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }else{
//                    popupWindow.showAsDropDown(v);
                    showPopupWindow(popupWindow, v);
                }
            }
        });
    }

    //PopupWindow显示位置，7.0/8.0兼容问题
    public void showPopupWindow(PopupWindow popupWindow, View view) {
        if (Build.VERSION.SDK_INT < 24) {

            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            popupWindow.showAsDropDown(view);
        } else {
            Rect visibleFrame = new Rect();
            view.getGlobalVisibleRect(visibleFrame);
            int height = view.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            popupWindow.setHeight(height);
            popupWindow.showAsDropDown(view, 0, 0);
        }
    }

    private void createUnitTree(Context context, Unit unit) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_tree, null);
        popupWindow = new Solve7PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        ListView contentLv = (ListView) view.findViewById(R.id.lv_content);

        List<Unit> rootUnits = new ArrayList<>();
        rootUnits.add(unit);


        final Solve7ITreeAllAdapter<Unit> adapter = new Solve7UnitTreeAdapter(context, rootUnits) {
            @Override
            public void actionOnNodeBodyClick(ViewHolder holder, Unit item, View convertView, int position) {

                finishRecorderCallBack.setCallBack(item);

                popupWindow.dismiss();
            }

        };

        contentLv.setAdapter(adapter);
    }


    public interface UnitCodeCallBack<T> {
        void setCallBack(T t);
    }

    public void setFinishCallBack(UnitCodeCallBack callBack) {
        finishRecorderCallBack = callBack;
    }
}
