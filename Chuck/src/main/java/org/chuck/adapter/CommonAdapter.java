package org.chuck.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.chuck.common.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {

    private LayoutInflater inflater;
    private List<T> datas;
    private int resId;

    public CommonAdapter(Context context, List<T> datas, int resId) {
        this.inflater = LayoutInflater.from(context);
        this.datas = datas;
        this.resId = resId;
    }


    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas == null ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getViewHolder(inflater, convertView, parent, resId);
        convertView = viewHolder.getConvertView();
        convert(viewHolder, getItem(position), convertView, parent, position);
        return convertView;
    }

    public abstract void convert(ViewHolder holder, T item, View convertView, ViewGroup parent, int position);

    public List<T> getAdapterDatas() {
        return datas;
    }

    public void addDatas(List<T> items) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        if (items != null) {
            datas.addAll(items);
        }
    }

    public void addDatas(int location, List<T> items) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        if (items != null) {
            datas.addAll(location, items);
        }
    }

    public void clearDatas() {
        if (datas != null) {
            datas.clear();
        }
    }
}
