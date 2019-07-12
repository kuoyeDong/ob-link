package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * 设备
 * Created by adolf_dong on 2019/7/1.
 */
public class Device implements Serializable {
    /**
     * 设备id
     */
    private String name;

    /**
     * 序列号
     */
    protected String serialId;
    /**
     * 地址
     */
    private String addr;
    /**
     * 组地址
     */
    private String groupAddr;
    /**
     * 状态
     */
    private String state;
    /**
     * 类型
     */
    protected String device_type;
    /**
     * 子类型
     */
    protected String device_child_type;
    /**
     * 版本号
     */
    private String version;

    /**
     * obox序列号
     */
    private String obox_serial_id;

    public Device() {

    }

    public Device(String name, String serialId, String addr, String groupAddr,
                  String state, String device_type,
                  String device_child_type,
                  String version) {
        this.name = name;
        this.serialId = serialId;
        this.addr = addr;
        this.groupAddr = groupAddr;
        this.state = state;
        this.device_type = device_type;
        this.device_child_type = device_child_type;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }

    public String getState() {
        if (state == null || state.equals("00")) {
            state = "0000000000000000";
        }
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_child_type() {
        return device_child_type;
    }

    public void setDevice_child_type(String device_child_type) {
        this.device_child_type = device_child_type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getObox_serial_id() {
        return obox_serial_id;
    }

    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
    }


}
