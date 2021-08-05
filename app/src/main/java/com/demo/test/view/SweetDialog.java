package com.demo.test.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.demo.test.R;

/**
 * Created by Administrator on 15-11-11.
 */
public class SweetDialog extends Dialog {
    private final SparseArray<View> views;
    private OnDismissListener dismissListener;
    private AnimationSet successAnimSet;
    private AnimationSet failAnimSet;
    private WindowManager.LayoutParams layoutParams;

    public static final int SUCCESS=1;
    public static final int ERROR=0;

    public SweetDialog(Context context) {
        this(context, R.style.sweet_dialog_default);
    }
    public SweetDialog(Context context, int theme) {
        super(context, theme);
        this.views=new SparseArray<>();
        init();
    }

    public void init(){
        layoutParams=this.getWindow().getAttributes();
    }
    public <T extends View> T getView(int id){
        View view=views.get(id);
        if(view==null){
            view=findViewById(id);
            views.put(id, view);
        }
        return (T) view;
    }



    public SweetDialog setText(int id,CharSequence text){
        TextView textView=getView(id);
        textView.setText(text);
        return this;
    }


    public SweetDialog setTextWatch(int id,TextWatcher textWatcher){
        TextView textView=getView(id);
        textView.addTextChangedListener(textWatcher);
        return this;
    }

    public SweetDialog setAdapter(int id,BaseAdapter adapter,AdapterView.OnItemClickListener... listener){
        AdapterView contentAv=getView(id);
        contentAv.setAdapter(adapter);
        if(listener!=null&&listener.length>0){
            contentAv.setOnItemClickListener(listener[0]);
        }
        return this;
    }

    public SweetDialog setOnClickListener(int id,View.OnClickListener listener){
        getView(id).setOnClickListener(listener);
        return this;
    }

    public SweetDialog setOnItemClickListener(int id,AdapterView.OnItemClickListener listener){
        ((AdapterView)getView(id)).setOnItemClickListener(listener);
        return this;
    }

    public SweetDialog setOnItemClickListener(int id, final OnItemClickListener listener){
        ((AdapterView)getView(id)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof CompoundButton) {
                    CompoundButton button = (CompoundButton) view;
                    button.toggle();
                }
                listener.onItemClick(parent, view, position, id);
            }
        });
        return this;
    }

    public SweetDialog setOnCheckedChangeListener(int id,CompoundButton.OnCheckedChangeListener listener){
        ((CompoundButton)getView(id)).setOnCheckedChangeListener(listener);
        return this;
    }

    public SweetDialog setDefault(){
        setPadding(15, 10, 15, 10).setSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        return this;
    }

    public SweetDialog setFullScreen(){
        setSize(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        return this;
    }

    public SweetDialog setGravity(int gravity){
        this.getWindow().setGravity(gravity);
        return this;
    }
    public SweetDialog setPadding(int left,int top,int right,int bottom){
        this.getWindow().getDecorView().setPadding(left, top, right, bottom);
        return this;
    }
    public SweetDialog setSize(int width,int height){
        this.layoutParams.width = width;
        this.layoutParams.height = height;
        this.getWindow().setAttributes(layoutParams);
        return this;
    }


    public interface OnItemClickListener{
        public void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }

    public interface OnItemCheckedChangedListener{
        public void onItemCheckedChanged();
    }
}
