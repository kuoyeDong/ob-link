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
     * 设备类型
     * 1 机顶盒
     * 2电视
     * 3DVD
     * 5投影仪
     * 6风扇
     * 7空调
     * 8智能灯
     * 10互联网机顶盒
     * 12扫地机
     * 13音响
     * 15空气净化器
     * 0其他(自定义学习)
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
