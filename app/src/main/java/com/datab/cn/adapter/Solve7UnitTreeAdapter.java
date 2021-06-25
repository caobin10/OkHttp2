package com.datab.cn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.datab.cn.dao.UnitDao;
import com.datab.cn.factory.DaoFactory;
import com.datab.cn.pojo.Unit;

import java.util.List;

import de.greenrobot.dao.query.WhereCondition;
import test.tree.adapter.Solve7ITreeAllAdapter;
import test.tree.common.ViewHolder;
import test.tree.util.ThreadPool;

/**
 * Created by wlm on 2017/8/22.
 */

public abstract class Solve7UnitTreeAdapter extends Solve7ITreeAllAdapter<Unit> {
    protected UnitDao unitDao;
    public Solve7UnitTreeAdapter(Context context, List<Unit> rootNodes) {
        super(context, rootNodes);
        addAllNodes();
    }

    public int addChildren(final Unit unit,int position){
        String code=getCode(unit);
        if(getLevel(unit)<4){
            for (Unit node:allNodes) {
                if (code.equals(getParentCode(node))) {
                    showNodes.add((++position), node);
                    if(isExpand(node)){
                        position=addChildren(node,position);
                    }
                }
            }
        }else{
//            ThreadPool.getExecutor().execute(new Runnable() {
//                @Override
//                public void run() {
            List<Unit> units = unitDao.queryBuilder().where(UnitDao.Properties.UnitCode.like(getCode(unit) + "%"),
                    new WhereCondition.StringCondition("length(unitCode)==35")).build().list();//
            showNodes.addAll((++position),units);
            notifyDataSetChanged();
//                }
//            });
        }
        return position;
    }

    @Override
    public void actionOnExpandClick(ViewHolder holder, Unit item, View convertView, int position, boolean isExpand) {
        item.setRemark(isExpand ? "false" : "true");
    }

    @Override
    public boolean isHaveChildren(Unit unit) {
        return getLevel(unit)<5?true:false;
    }

    @Override
    public String getCode(Unit unit) {
        return unit.getUnitCode();
    }

    @Override
    public int getLevel(Unit unit) {
        int level = 0;
        switch (unit.getUnitCode().length()){
            case 11:level=1;break;
            case 17:level=2;break;
            case 23:level=3;break;
            case 29:level=4;break;
            case 35:level=5;break;
        }
        return level;
    }

    @Override
    public String getParentCode(Unit unit) {
        return unit.getParentCode();
    }

    @Override
    public String getName(Unit unit) {
        return unit.getUnitName();
    }

    @Override
    public boolean isExpand(Unit unit) {
        return Boolean.valueOf(unit.getRemark());
    }

    public void addAllNodes(){
        unitDao= DaoFactory.getUnitDao(context);

        final StringBuilder builder=new StringBuilder();
        for(int i=0;i<rootNodes.size();i++){
            if(i==0){builder.append("unitCode LIKE '"+getCode(rootNodes.get(i))+"%'");}
            else{builder.append(" OR unitCode LIKE '"+getCode(rootNodes.get(i))+"%'");}
        }

        ThreadPool.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Unit> units = unitDao.queryBuilder().where(new WhereCondition.StringCondition("length(unitCode)<35"),
                        new WhereCondition.StringCondition(builder.toString())).orderAsc(UnitDao.Properties.UnitCode).build().list();
                Log.i("units.size()",units.size()+"");
                addTreeNodes(units);
            }
        });
    }

}
