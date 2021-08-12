package com.demo.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Administrator on 2021/8/9.
 */

public class GetData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2568878761842508805L;
    @JsonProperty(value="IsSuccess")
    private boolean isSuccess;
    @JsonProperty(value="Message")
    private String message;
    @JsonProperty(value="Data")
    private String data;
    @JsonProperty(value="Extra")
    private String extra;
    public boolean isSuccess() {
        return isSuccess;
    }
    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }


}

