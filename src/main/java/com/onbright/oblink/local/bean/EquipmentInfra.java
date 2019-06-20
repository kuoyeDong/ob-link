package com.onbright.oblink.local.bean;

/**红外遥控内设备
 * Created by adolf_dong on 2016/7/7.
 */
public class EquipmentInfra {
    /**
     * 主键id
     */
    private int id;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 设备类型
     */
    private int type;
    /**
     * 设备子类型
     */
    private int scdType;

    public EquipmentInfra(int id, String name, int scdType, int type) {
        this.id = id;
        this.name = name;
        this.scdType = scdType;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScdType() {
        return scdType;
    }

    public void setScdType(int scdType) {
        this.scdType = scdType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
