package com.datab.cn.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.datab.cn.dao.UnitDao;
import com.datab.cn.factory.DaoFactory;
import com.datab.cn.pojo.Unit;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.WhereCondition;
import test.tree.adapter.Solve7ITreeAllAdapter;
import test.tree.common.ViewHolder;
import test.tree.util.ThreadPool;


/**
 * Created by wlm on 2017/8/22.
 */

public abstract class OrgUnitTreeAdapter extends Solve7ITreeAllAdapter<Unit> {
    protected UnitDao unitDao;
    //机构对应服务地区
    private List<String> serUnitCode;

    public OrgUnitTreeAdapter(Context context, List<Unit> rootNodes, List<String> serUnitCode) {
        super(context, rootNodes);
        this.serUnitCode = serUnitCode;

        addAllNodes();
    }

    public int addChildren(final Unit unit, int position) {
        String code = getCode(unit);
        if (getLevel(unit) == 1) {
            for (Unit node : allNodes) {
                if (code.equals(getParentCode(node))) {
                    showNodes.add((++position), node);
                    if (isExpand(node)) {
                        position = addChildren(node, position);
                    }
                }
            }
        } else if (getLevel(unit) == 2) {
            List<Unit> units = unitDao.queryBuilder().where(UnitDao.Properties.UnitCode.like(getCode(unit) + "%"),
                    new WhereCondition.StringCondition("length(unitCode)==23")).build().list();//

            units = serviceScopeUnit(units);

            showNodes.addAll((++position), units);
            notifyDataSetChanged();
        } else if (getLevel(unit) == 3) {
            List<Unit> units = unitDao.queryBuilder().where(UnitDao.Properties.UnitCode.like(getCode(unit) + "%"),
                    new WhereCondition.StringCondition("length(unitCode)==29")).build().list();//

            units = serviceScopeUnit(units);

            showNodes.addAll((++position), units);
            notifyDataSetChanged();
        } else {
//            ThreadPool.getExecutor().execute(new Runnable() {
//                @Override
//                public void run() {
            List<Unit> units = unitDao.queryBuilder().where(UnitDao.Properties.UnitCode.like(getCode(unit) + "%"),
                    new WhereCondition.StringCondition("length(unitCode)==35")).build().list();//

            units = serviceScopeUnit(units);

            showNodes.addAll((++position), units);
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
        return getLevel(unit) < 5 ? true : false;
    }

    @Override
    public String getCode(Unit unit) {
        return unit.getUnitCode();
    }

    @Override
    public int getLevel(Unit unit) {
        int level = 0;
        switch (unit.getUnitCode().length()) {
            case 11:
                level = 1;
                break;
            case 17:
                level = 2;
                break;
            case 23:
                level = 3;
                break;
            case 29:
                level = 4;
                break;
            case 35:
                level = 5;
                break;
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

    public void addAllNodes() {
        unitDao = DaoFactory.getUnitDao(context);

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < rootNodes.size(); i++) {
            if (i == 0) {
                builder.append("unitCode LIKE '" + getCode(rootNodes.get(i)) + "%'");
            } else {
                builder.append(" OR unitCode LIKE '" + getCode(rootNodes.get(i)) + "%'");
            }
        }

        ThreadPool.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Unit> units = unitDao.queryBuilder().where(new WhereCondition.StringCondition("length(unitCode)<23"),
                        new WhereCondition.StringCondition(builder.toString())).orderAsc(UnitDao.Properties.UnitCode).build().list();
                Log.i("units.size()", units.size() + "");

                units = serviceScopeUnit(units);

                addTreeNodes(units);
            }
        });
    }

    //服务范围
    private List<Unit> serviceScopeUnit(List<Unit> units) {

        List<Unit> tempUnits = new ArrayList<>();

        if (serUnitCode != null) {

            for (Unit unit : units) {
                String unitCode = unit.getUnitCode();

                boolean flag = false;

                for (String item : serUnitCode) {
                    //判断条件一：单位相等
                    if (unitCode.equals(item)) {
                        flag = true;
                        break;
                    }
                    //判断条件二：上级单位是否包含这个，比如上级成都是否包含锦江区
                    if (unitCode.length() < item.length() && unitCode.equals(item.substring(0, unitCode.length()))) {
                        flag = true;
                        break;
                    }
                    //判断条件三：下级单位是否属于这个上级，比如锦江区是否属于上级成都
                    if (unitCode.length() > item.length() && unitCode.substring(0, item.length()).equals(item)) {
                        flag = true;
                        break;
                    }
                }

                //满足任何条件之一，可获取该单位实例
                if (!flag) continue;

                tempUnits.add(unit);

            }

        } else {
            tempUnits.addAll(units);
        }

        return tempUnits;
    }

}
