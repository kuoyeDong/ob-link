package com.onbright.oblink;

import com.google.gson.annotations.SerializedName;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.bean.Group;
import com.onbright.oblink.local.net.Transformation;

import java.io.Serializable;
import java.util.List;

/**
 * obox,昂宝设备中枢，此设备上连接云，下连接下级节点设备
 * Created by adolf_dong on 2019/7/2.
 */
public class Obox implements Serializable {

    /**
     * obox的id，也是obox的唯一标识
     */
    private String obox_serial_id;
    private String serialId;
    /**
     * obox的版本号
     */
    private String obox_version;
    /**
     * obox的名字
     */
    private String obox_name;
    /**
     * obxo的密码
     */
    private String obox_pwd;
    /**
     * obox里面的设备配置信息
     */
    private List<Device> device_config;

    public List<Group> getGroup_config() {
        return group_config;
    }

    public void setGroup_config(List<Group> group_config) {
        this.group_config = group_config;
    }

    public List<CloudScene> getScene_config() {
        return scene_config;
    }

    public void setScene_config(List<CloudScene> scene_config) {
        this.scene_config = scene_config;
    }

    /**
     * 组
     */
    private List<Group> group_config;
    /**
     * 场景
     */
    private List<CloudScene> scene_config;

    private String obox_activate;
    private String obox_person;
    /**
     * "0" offLine
     */
    private String obox_status;
    private String obox_control;


    /**
     * 释放时间
     */
    private transient long releaseTime;

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    public boolean canSearch(long currentTime, int second) {
        return currentTime - releaseTime > second * 1000;
    }


    public Obox(String obox_serial_id, String obox_version, String obox_name, String obox_pwd, List<Device> device_config) {
        this.obox_serial_id = obox_serial_id;
        this.obox_version = obox_version;
        this.obox_name = obox_name;
        this.obox_pwd = obox_pwd;
        this.device_config = device_config;

    }

    public Obox(String obox_status, String obox_activate, String obox_person, String obox_control) {
        this.obox_activate = obox_activate;
        this.obox_person = obox_person;
        this.obox_status = obox_status;
        this.obox_control = obox_control;
    }

    public Obox() {
    }

    public String getObox_serial_id() {
        return obox_serial_id;
    }

    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
    }

    public String getSerialId() {
        return serialId;
    }

    public String getDevice_type() {
        return "0a";
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getObox_version() {
        return obox_version;
    }

    public void setObox_version(String obox_version) {
        this.obox_version = obox_version;
    }

    public String getObox_name() {
        return obox_name;
    }

    public void setObox_name(String obox_name) {
        this.obox_name = obox_name;
    }

    public String getObox_pwd() {
        return obox_pwd;
    }

    public void setObox_pwd(String obox_pwd) {
        this.obox_pwd = obox_pwd;
    }

    public List<Device> getDevice_config() {
        return device_config;
    }

    public void setDevice_config(List<Device> device_config) {
        this.device_config = device_config;
    }

    public void setObox_serial_id(byte[] id) {
        this.obox_serial_id = Transformation.byteArryToHexString(id);
    }

    public void setObox_version(byte[] version) {
        this.obox_version = Transformation.byteArryToHexString(version);
    }


    public String getObox_person() {
        return obox_person;
    }

    public void setObox_person(String obox_person) {
        this.obox_person = obox_person;
    }

    public String getObox_activate() {
        return obox_activate;
    }

    public void setObox_activate(String obox_activate) {
        this.obox_activate = obox_activate;
    }

    public String getObox_status() {
        return obox_status;
    }

    public void setObox_status(String obox_status) {
        this.obox_status = obox_status;
    }

    public String getObox_control() {
        return obox_control;
    }

    public void setObox_control(String obox_control) {
        this.obox_control = obox_control;
    }

}
