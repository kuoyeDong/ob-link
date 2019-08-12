package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.handler.basehandler.BooleanSensorHandler;

/**
 * 处理烟雾传感器
 *
 * @author dky
 * 2019/8/7
 */
public abstract class SmokeSensorHandler extends BooleanSensorHandler {
    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected SmokeSensorHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    public void booleanTrue() {
        smoke();
    }

    /**
     * 有烟雾
     */
    protected abstract void smoke();

    @Override
    public void booleanFalse() {
        noSmoke();
    }

    /**
     * 干净了，没有烟雾
     */
    protected abstract void noSmoke();

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.SMOKE_SENSOR;
    }
}
