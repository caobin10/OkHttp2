package org.chuck.adapter;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.chuck.common.ViewHolder;
import org.chuck.view.PinnedSectionListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 15-12-8.
 * 若想使用android:fastScrollEnabled="true" 请继承BaseAdapter
 */
public abstract class SectionAdapter<T> implements PinnedSectionListView.PinnedSectionListAdapter {
    public static final int ITEM = 0;
    public static final int SECTION = 1;

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    private LayoutInflater inflater;
    private List<T> datas;
    private int resId;

    public SectionAdapter(Context context, List<T> datas, int resId) {
        this.inflater=LayoutInflater.from(context);
        this.datas=datas;
        this.resId=resId;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    public boolean areAllItemsEnabled() {
        return true;
    }

    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }




    @Override
    public int getCount() {
        return datas==null?0:datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas==null?null:datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=ViewHolder.getViewHolder(inflater, convertView, parent, resId);
        convertView=viewHolder.getConvertView();
        convert(viewHolder,getItem(position),convertView,parent,position);
        return convertView;
    }

    public abstract void convert(ViewHolder holder,T item,View convertView, ViewGroup parent,int position);



    public void addDatas(List<T> items){
        if(datas==null){datas=new ArrayList<>();}
        if(items!=null){datas.addAll(items);}
    }
    public void addDatas(int location, List<T> items){
        if(datas==null){datas=new ArrayList<>();}
        if(items!=null){datas.addAll(location,items);}
    }
    public void clearDatas(){
        if(datas!=null){
            datas.clear();
        }
    }

}
