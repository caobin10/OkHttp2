package com.datab.cn.bean;

import com.datab.cn.pojo.Unit;

import java.io.Serializable;

/**
 * Created by Administrator on 2021/6/23.
 */

public class User implements Serializable {
    private Unit unit;
    private String unitCode;
    private String username;
    private String remark ;

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
