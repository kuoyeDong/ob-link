package com.onbright.oblink.cloud.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 情景的条件信息
 * Created by shifan_xiao on 2016/9/2.
 * dky 2016年10月12日14:17:02修改
 */
public class Condition implements Serializable {

    public static final int TIMING = 0;
    public static final int SENSOR = 1;
    public static final int REMOTE = 2;
    public static final int FINGERPRINT_MACHINE = 3;
    /**
     * 时间/设备/遥控器 00/01/02
     */
    private String condition_type;

    /**
     * 设备序列号，当条件为时间时，没有该参数
     */
    private String serialId;
    /**
     * 设备地址
     */
    private String addr;
    /**
     * Obox序列号
     */
    private String obox_serial_id;
    /**
     * 设备名字
     */
    private String conditionID;
    /**
     * 设备类型
     */
    private String device_type;
    /**
     * 子设备类型
     */
    private String device_child_type;

    /**
     * Obox序列号
     */
    private List<String> oboxs;

    /**
     * 条件行为
     */
    private String condition;

    public Condition() {
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

    public String getObox_serial_id() {
        return obox_serial_id;
    }

    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
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

    public String getConditionID() {
        return conditionID;
    }

    public void setConditionID(String conditionID) {
        this.conditionID = conditionID;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<String> getOboxs() {
        return oboxs;
    }

    public void setOboxs(List<String> oboxs) {
        this.oboxs = oboxs;
    }

    public String getCondition_type() {
        return condition_type;
    }

    public void setCondition_type(String condition_type) {
        this.condition_type = condition_type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Condition) {
            Condition cdt = (Condition) o;
            /*此处全为null时候也可认为是相等，否则*/
            return (cdt.condition_type == null ? condition_type == null : cdt.condition_type.equals(condition_type)) &&
                    (cdt.serialId == null ? serialId == null : cdt.serialId.equals(serialId)) &&
                    (cdt.addr == null ? addr == null : cdt.addr.equals(addr)) &&
                    (cdt.obox_serial_id == null ? obox_serial_id == null : cdt.obox_serial_id.equals(obox_serial_id)) &&
                    (cdt.conditionID == null ? conditionID == null : cdt.conditionID.equals(conditionID)) &&
                    (cdt.device_type == null ? device_type == null : cdt.device_type.equals(device_type)) &&
                    (cdt.device_child_type == null ? device_child_type == null : cdt.device_child_type.equals(device_child_type)) &&
                    (cdt.oboxs == null ? oboxs == null : cdt.oboxs.equals(oboxs)) &&
                    (cdt.condition == null ? condition == null : cdt.condition.equals(condition));
        }
        return false;
    }
}
