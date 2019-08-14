package com.onbright.oblink.cloud.bean.infraredtransponderbean;

import java.io.Serializable;

/**
 * 红外遥控器支持的设备类型
 *
 * @author dky
 * 2019/8/12
 */
public class DeviceType implements Serializable {

    /**
     * 设备类型,参见{@link com.onbright.oblink.local.net.OBConstant.IrType}
     */
    private int t;
    /**
     * 设备名称
     */
    private String name;

    public DeviceType(int t, String name) {
        this.t = t;
        this.name = name;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
