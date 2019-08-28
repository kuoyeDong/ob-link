package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * wifi红外转发器的action规格
 *
 * @author dky
 * 2019/8/28
 */
public class IrAction implements Serializable {
    /**
     * DeviceInfoList中遥控索引ID
     */
    private int index;
    /**
     * DeviceInfoList中遥控器名，用于界面直接显示
     */
    private String name;
    /**
     * 按键类型
     * 0:标准按键
     * 1:拓展按键
     */
    private int keyType;
    /**
     * 标准按键或拓展按键的按键名称key
     */
    private String key;

    /**
     * @param index   DeviceInfoList中遥控索引ID
     * @param name    DeviceInfoList中遥控器名，用于界面直接显示
     * @param keyType 按键类型
     *                0:标准按键
     *                1:拓展按键
     * @param key     标准按键或拓展按键的按键名称key
     */
    public IrAction(int index, String name, int keyType, String key) {
        this.index = index;
        this.name = name;
        this.keyType = keyType;
        this.key = key;
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

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
