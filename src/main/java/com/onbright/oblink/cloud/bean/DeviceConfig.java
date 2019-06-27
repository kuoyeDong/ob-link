package com.onbright.oblink.cloud.bean;

import android.support.annotation.NonNull;

import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import java.io.UnsupportedEncodingException;

/**
 * 服务器版本设备
 * Created by adolf_dong on 2016/1/7.
 */
public class DeviceConfig   implements  Comparable {


    public transient boolean isOnline = true;
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

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void toggle() {
        this.isChecked = !this.isChecked;
    }

    public boolean getChecked() {
        return this.isChecked;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (another instanceof DeviceConfig) {
            if (this instanceof Groups && another instanceof Groups) {
                Groups thisGroups = (Groups) this;
                Groups anotherGroups = (Groups) another;
//                int tgaddr = Integer.parseInt(thisGroups.getObox_serial_num() + thisGroups.getGroupAddr(), 16);
//                int angaddr = Integer.parseInt(anotherGroups.getObox_serial_num() + anotherGroups.getGroupAddr(), 16);
//                return tgaddr < angaddr ? -1 : (tgaddr > angaddr ? 1 : 0);
                if (thisGroups.getObox_serial_num() == null) {
                    return thisGroups.getGroup_id().compareTo(anotherGroups.getGroup_id());
                } else {
                    return (thisGroups.getObox_serial_num() + thisGroups.getGroupAddr()).compareTo(anotherGroups.getObox_serial_num() + anotherGroups.getGroupAddr());
                }
            }
            DeviceConfig anDev = (DeviceConfig) another;
//            int tsaddr = Integer.parseInt(this.getObox_serial_id() + this.addr, 16);
//            int ansddr = Integer.parseInt(anDev.getObox_serial_id() + anDev.addr, 16);
//            return tsaddr < ansddr ? -1 : (tsaddr > ansddr ? 1 : 0);
            return (this.getObox_serial_id() + this.addr).compareTo(anDev.getObox_serial_id() + anDev.addr);
        }
        return 0;
    }

}
