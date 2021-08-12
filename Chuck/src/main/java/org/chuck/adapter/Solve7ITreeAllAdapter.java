package org.chuck.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.chuck.R;
import org.chuck.common.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class Solve7ITreeAllAdapter<T> extends BaseAdapter{

	protected LayoutInflater inflater;
	protected List<T> allNodes=new ArrayList<>();
	protected List<T> showNodes=new ArrayList<>();
	protected List<T> rootNodes;

	protected Context context;
	public Solve7ITreeAllAdapter(Context context, List<T> rootNodes) {
		this.context=context;
		this.inflater=LayoutInflater.from(context);
		this.rootNodes=rootNodes;
		showNodes.addAll(rootNodes);
	}


	@Override
	public int getCount() {
		return showNodes==null?0:showNodes.size();
	}

	@Override
	public T getItem(int position) {
		return showNodes==null?null:showNodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder= ViewHolder.getViewHolder(inflater, convertView, parent, R.layout.item_tree);
		convertView=viewHolder.getConvertView();
		convert(viewHolder, getItem(position), convertView, position);
		return convertView;
	}

	public void convert(final ViewHolder holder, final T item, final View convertView, final int position){
		ImageView expandIv=holder.getView(R.id.iv_expand);
		TextView nodeBodyTv=holder.getView(R.id.tv_nodebody);
		nodeBodyTv.setText(getName(item));
		LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) expandIv.getLayoutParams();
		layoutParams.leftMargin=getLevel(item)*27+25;
		expandIv.setLayoutParams(layoutParams);

		if(!isExpand(item)&&position==0){
			actionOnExpandClick(holder, item, convertView, position, isExpand(item));
			addChildren(item, position);
		}

		expandIv.setBackgroundResource(isExpand(item) ? R.drawable.expand_on2 : R.drawable.expand_off1);
		expandIv.setVisibility(isHaveChildren(item) ? View.VISIBLE : View.INVISIBLE);
		nodeBodyTv.setCompoundDrawablesWithIntrinsicBounds(isHaveChildren(item) ?
				(isExpand(item) ? context.getResources().getDrawable(R.drawable.eeee) : context.getResources().getDrawable(R.drawable.cccc))
				: context.getResources().getDrawable(R.drawable.dddd), null, null, null);

		expandIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(position!=0){

					actionOnExpandClick(holder, item, convertView, position, isExpand(item));
					if (!isExpand(item)) {
						removeChildren(item);
					} else {
						int i = position;
						addChildren(item, i);
					}
					notifyDataSetChanged();
				}
			}
		});
		nodeBodyTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actionOnNodeBodyClick(holder, item, convertView, position);
			}
		});
	}

	public void removeChildren(T t){
		List<T> rmChildren=new ArrayList<>();
		removeChildren(t,rmChildren);
		showNodes.removeAll(rmChildren);
	}

	public void removeChildren(T t,List<T> rmChildren){
		String code=getCode(t);
		for(T node:showNodes){
			if(code.equals(getParentCode(node))){
				rmChildren.add(node);
				if(isHaveChildren(node)&& showNodes.contains(node)){
					removeChildren(node,rmChildren);
				}
			}
		}
	}


	public int addChildren(T t,int position){
		String code=getCode(t);
		for (T node:allNodes) {
			if (code.equals(getParentCode(node))) {
				showNodes.add((++position), node);
				if(isExpand(node)){
					position=addChildren(node,position);
				}
			}
		}
		return position;
	}
	public void addTreeNodes(List<T> treeNodes){
		if(treeNodes!=null){
			allNodes.addAll(treeNodes);
		}
	}

	public abstract void actionOnExpandClick(ViewHolder holder, T item, View convertView, int position, boolean isExpand);
	public abstract void actionOnNodeBodyClick(ViewHolder holder, T item, View convertView, int position);

	public abstract boolean isHaveChildren(T t);
	public abstract String getCode(T t);
	public abstract int getLevel(T t);
	public abstract String getParentCode(T t);
	public abstract String getName(T t);
	public abstract boolean isExpand(T t);

}
