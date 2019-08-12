package com.onbright.oblink.cloud.bean.infraredtransponderbean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 遥控设备方案
 *
 * @author dky
 * 2019/8/12
 */
public class Program implements Serializable {
    /**
     * 红外遥控器对应的服务器码库序列号唯一值
     */
    private int index;
    /**
     * 遥控方案名称
     */
    private String name;
    /**
     * 遥控方案类型
     */
    private int deviceType;
    /**
     * 品牌id
     */
    private int brandId;

    /**
     * 标准按键集合
     */
    private ArrayList<StandardKey> keys;
    /**
     * 自定义按键集合
     */
    private ArrayList<ExtendsKey> extendsKeys;

    /**
     * 遥控云码库方案ID,仅匹配的码库有效
     */
    private String rid;
    /**
     * 码库rc_command数据单元数组,仅匹配的码库有效
     */
    private ArrayList<RcCommand> rc_command;

    /**
     * 遥控器型号	仅匹配的码库有效
     */
    private String rmodel;
    /**
     * 遥控器版本，用于区别控制空调时选择V1还是V3方式
     */
    private String version;

    public Program(int index, String name, int brandId, int deviceType, ArrayList<StandardKey> keys,
                   ArrayList<ExtendsKey> extendsKeys, String rid, ArrayList<RcCommand> rc_command,
                   String rmodel, String version) {
        this.index = index;
        this.name = name;
        this.brandId = brandId;
        this.deviceType = deviceType;
        this.keys = keys;
        this.extendsKeys = extendsKeys;
        this.rid = rid;
        this.rc_command = rc_command;
        this.rmodel = rmodel;
        this.version = version;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<StandardKey> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<StandardKey> keys) {
        this.keys = keys;
    }

    public ArrayList<ExtendsKey> getExtendsKeys() {
        return extendsKeys;
    }

    public void setExtendsKeys(ArrayList<ExtendsKey> extendsKeys) {
        this.extendsKeys = extendsKeys;
    }


    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public ArrayList<RcCommand> getRc_command() {
        return rc_command;
    }

    public void setRc_command(ArrayList<RcCommand> rc_command) {
        this.rc_command = rc_command;
    }

    public String getRmodel() {
        return rmodel;
    }

    public void setRmodel(String rmodel) {
        this.rmodel = rmodel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 判断是否V3版本的空调,查看标准码是否包含下划线"_"
     *
     * @return 如果是V3版本的空调返回true
     */
    public boolean isV3AirCon() {
        if (keys != null) {
            for (StandardKey standardKey : keys) {
                if (standardKey.getKey().contains("_")) {
                    return true;
                }
            }
        }
        return false;
    }

}
