package com.inso.core.entity;

import java.io.Serializable;

/**
 * 封装结果信息
 */
public class Result implements Serializable{
    private Boolean flag;  //true:成功
    private String message;

    public Result(Boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
