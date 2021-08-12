package com.demo.test.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2021/8/11.
 */

public class User implements Serializable{

    @JsonProperty(value="ID")
    private String id ;
    @JsonProperty(value="UpdateTime")
    private Date updateTime ;
}
