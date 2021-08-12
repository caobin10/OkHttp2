package org.chuck.common;

import org.chuck.view.BadgeView;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHolder {
	
	private final SparseArray<View> views;	
	private View convertView;
	
	private ViewHolder(LayoutInflater inflater,ViewGroup parent,int resId){
		this.views=new SparseArray<>();
		this.convertView=inflater.inflate(resId, parent,false);
		this.convertView.setTag(this);
	}
	
	public static ViewHolder getViewHolder(LayoutInflater inflater,View convertView,ViewGroup parent,int resId){
		if(convertView==null){
			return new ViewHolder(inflater, parent, resId);
		}
		return (ViewHolder) convertView.getTag();
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int id){		
		View view=views.get(id);
		if(view==null){
			view=convertView.findViewById(id);
			views.put(id, view);
		}		
		return (T) view;
	}
	
	public View getConvertView(){
		return convertView;
	}
	
	public ViewHolder setText(int id,CharSequence text){
		TextView textView=getView(id);
		textView.setText(text);
		return this;
	}
	public ViewHolder setHint(int id,CharSequence hint){
		TextView textView=getView(id);
		textView.setHint(hint);
		return this;
	}
	public ViewHolder setContentDescription(int id,CharSequence contentDescription){
	    getView(id).setContentDescription(contentDescription);
		return this;
	}
	public ViewHolder setProgress(int id,int progress){
		ProgressBar progressBar=getView(id);
		progressBar.setProgress(progress);
		return this;
	}
	
	public ViewHolder setImageResource(int id,int resId){
		ImageView imageView=getView(id);
		imageView.setImageResource(resId);
		return this;
	}
	
	public ViewHolder setImageBitmap(int id,Bitmap bm){
		ImageView imageView=getView(id);
		imageView.setImageBitmap(bm);
		return this;
	}
	
	public ViewHolder setBadge(Context context,int id,CharSequence text,int gravity){
		BadgeView badgeView=new BadgeView(context);
		badgeView.setBadgeText(text);
		badgeView.setTargetView(getView(id));
		badgeView.setGravity(gravity);
		return this;
	}
	
	public ViewHolder setBadge(Context context,int id,int count,int gravity){
		BadgeView badgeView=new BadgeView(context);
		badgeView.setBadgeCount(count);
		badgeView.setTargetView(getView(id));
		badgeView.setGravity(gravity);
		return this;
	}
	
	public ViewHolder setVisibility(int id,int visible){
		getView(id).setVisibility(visible);
		return this;
	}
	
	public ViewHolder setTextSize(int id,float size){
		TextView textView=getView(id);
		textView.setTextSize(size);
		return this;
	}
	
	public ViewHolder setTextColor(int id,int color){
		TextView textView=getView(id);
		textView.setTextColor(color);
		return this;
	}
	
	
	public ViewHolder setGravity(int id,int gravity){
		TextView textView=getView(id);
		textView.setGravity(gravity);
		return this;
	}
	public ViewHolder setChecked(int id,boolean isChecked){
		((CheckBox)getView(id)).setChecked(isChecked);
		return this;
	}
	public ViewHolder setOnClickListener(int id,OnClickListener listener){
		getView(id).setOnClickListener(listener);
		return this;
	}
	public ViewHolder setOnLongClickListener(int id, OnLongClickListener listener){
		getView(id).setOnLongClickListener(listener);
		return this;
	}

	public ViewHolder setOnItemClickListener(int id,AdapterView.OnItemClickListener listener){
		((AdapterView)getView(id)).setOnItemClickListener(listener);
		return this;
	}


}
