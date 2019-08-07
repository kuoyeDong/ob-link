package com.onbright.oblink.cloud.handler.basehandler;

/**
 * 电池类设备接口
 *
 * @author dky
 * 2019/8/7
 */
public interface BatteryDevice {
    /**
     * 电池设备电量
     *
     * @param batteryValue 电量值，门锁为具体百分比，上电池传感器为阶值
     */
    void batteryValue(int batteryValue);
}
