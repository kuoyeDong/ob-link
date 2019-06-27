package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * 阿里设备状态
 * Created by adolf_dong on 2018/3/20.
 */
public class AliDevState implements Serializable{
    /**
     * 功能点ID
     */
    private String functionId;
    /**
     * 数据
     */
    private Boolean data;

    public AliDevState(String functionId, Boolean data) {
        this.functionId = functionId;
        this.data = data;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public Boolean getData() {
        return data;
    }

    public void setData(Boolean data) {
        this.data = data;
    }
}
