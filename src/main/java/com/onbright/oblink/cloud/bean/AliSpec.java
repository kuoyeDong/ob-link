package com.onbright.oblink.cloud.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Wi-Fi 单品功能定义
 * Created by adolf_dong on 2018/3/19.
 */
public class AliSpec implements Serializable {
    /**
     * 功能点
     */
    private String function;
    /**
     * 功能点ID
     */
    private String functionId;
    /**
     * 功能点名称
     */
    private String functionName;
    /**
     * 标识名
     */
    private String functionTag;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 数据
     */
    private List<Object> data;
    /**
     * 数据传输类型
     */
    private List<String> dataTranType;

    public AliSpec(String function, String functionId, String functionName, String functionTag, String dataType, List<Object> data, List<String> dataTranType) {
        this.function = function;
        this.functionId = functionId;
        this.functionName = functionName;
        this.functionTag = functionTag;
        this.dataType = dataType;
        this.data = data;
        this.dataTranType = dataTranType;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionTag() {
        return functionTag;
    }

    public void setFunctionTag(String functionTag) {
        this.functionTag = functionTag;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public List<String> getDataTranType() {
        return dataTranType;
    }

    public void setDataTranType(List<String> dataTranType) {
        this.dataTranType = dataTranType;
    }
}