package com.onbright.oblink.cloud.bean;



import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import java.io.UnsupportedEncodingException;

/**
 * 服务器版本设备
 * Created by adolf_dong on 2016/1/7.
 */
public class DeviceConfig {


    /**
     * 设备id
     */
    private String name;

    /**
     * 序列号
     */
    private String serialId;
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
    private String device_type;
    /**
     * 子类型
     */
    private String device_child_type ;
    /**
     * 版本号
     */
    private String version;

    /**
     * obox序列号
     */
    private String obox_serial_id;
    /**
     * 用于选取时候标识是否被选中
     */
    private boolean isChecked;

    public DeviceConfig() {

    }

    public DeviceConfig(String name, String serialId, String addr, String groupAddr,
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

    public DeviceConfig(byte[] name, byte[] serialId, byte addr, byte groupAddr,
                        byte[] state, byte device_type,
                        byte device_child_type,
                        byte[] version) {
        try {
            this.name = new String(name, OBConstant.StringKey.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.serialId = Transformation.byteArryToHexString(serialId);
        this.addr = String.valueOf(addr & 0xff);
        this.groupAddr = String.valueOf(groupAddr & 0xff);
        this.state = Transformation.byteArryToHexString(state);
        this.device_type = Transformation.byte2HexString(device_type);
        this.device_child_type = Transformation.byte2HexString(device_child_type);
        this.version = Transformation.byteArryToHexString(version);
    }


    /**获取名称
     */
    public String getName() {
        return name;
    }

    /**设置名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**获取序列号
     */
    public String getSerialId() {
        return serialId;
    }

    /**设置序列号
     */
    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    /**获取设备地址
     */
    public String getAddr() {
        return addr;
    }

    /**设置节点地址
     */
    public void setAddr(String addr) {
        this.addr = addr;
    }

    /**获取设备组地址
     */
    public String getGroupAddr() {
        return groupAddr;
    }

    /**设置设备组地址
     */
    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }

    /**获取状态
     */
    public String getState() {
        return state;
    }

    /**设置状态
     */
    public void setState(String state) {
        this.state = state;
    }

    /**获取节点所属类型
     */
    public String getDevice_type() {
        return device_type;
    }

    /**设置节点所属类型
     * @param device_type
     */
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    /**获取节点所属类型
     */
    public String getDevice_child_type() {
        return device_child_type;
    }

    /**设置节点所属类型,注意均为16进制字符串
     * @see com.onbright.oblink.local.net.OBConstant.NodeType
     */
    public void setDevice_child_type(String device_child_type) {
        this.device_child_type = device_child_type;
    }

    /**获取版本
     */
    public String getVersion() {
        return version;
    }

    /**设置版本
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**获取obox序列号
     */
    public String getObox_serial_id() {
        return obox_serial_id;
    }

    /**设置obox序列号
     */
    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
    }

    /**设置是否被选中
     */
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /**
     * 反选选中状态
     */
    public void toggle() {
        this.isChecked = !this.isChecked;
    }

    /**获取选中状态
     */
    public boolean getChecked() {
        return this.isChecked;
    }

}
