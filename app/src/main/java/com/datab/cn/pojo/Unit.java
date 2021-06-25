package com.datab.cn.pojo;

/**
 * Created by Administrator on 2021/6/21.
 */

public class Unit {
    private String unitCode;
    private String unitName;
    private String parentCode;
    private String remark;

    public Unit(){

    }

    public Unit(String unitCode, String unitName, String parentCode, String remark) {
        this.unitCode = unitCode;
        this.unitName = unitName;
        this.parentCode = parentCode;
        this.remark = remark;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
