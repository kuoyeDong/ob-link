package com.onbright.oblink.cloud.handler.sensor;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.handler.basehandler.BooleanSensorHandler;

/**
 * 水浸处理类
 *
 * @author dky
 * 2019/8/7
 */
public abstract class FloodSensorHandler extends BooleanSensorHandler {
    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected FloodSensorHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    public void booleanTrue() {
        wet();
    }

    /**
     * 干的
     */
    protected abstract void wet();

    @Override
    public void booleanFalse() {
        dry();
    }

    /**
     * 湿的
     */
    protected abstract void dry();

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.FLOOD;
    }

}
