package com.demo.test.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2021/8/11.
 */

public class Version implements Serializable{
    /**版本号*/
    private String code;
    /**是否为强制更新版本*/
    private boolean apkForce;
    /**是否更新离线数据库*/
    private boolean dbForce;
    /**本次升级概要*/
    private String description;
    /**配置年*/
    private String configYear;

    /**是否逐项确认*/
    private boolean isConfirm;

    /**basic版本号*/
    private String basicVersion;

    public String getBasicVersion() {
        return basicVersion;
    }

    public void setBasicVersion(String basicVersion) {
        this.basicVersion = basicVersion;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }

    public String getConfigYear() {
        return configYear;
    }

    public void setConfigYear(String configYear) {
        this.configYear = configYear;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isApkForce() {
        return apkForce;
    }

    public void setApkForce(boolean apkForce) {
        this.apkForce = apkForce;
    }

    public boolean isDbForce() {
        return dbForce;
    }

    public void setDbForce(boolean dbForce) {
        this.dbForce = dbForce;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
