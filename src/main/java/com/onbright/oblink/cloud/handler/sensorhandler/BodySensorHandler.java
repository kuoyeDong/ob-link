package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.handler.basehandler.BooleanSensorHandler;

/**
 * 处理人体感应传感器
 *
 * @author dky
 * 2019/8/7
 */
public abstract class BodySensorHandler extends BooleanSensorHandler {
    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected BodySensorHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    public void booleanTrue() {
        someOneHere();
    }

    /**
     * 这里有人
     */
    protected abstract void someOneHere();

    @Override
    public void booleanFalse() {
        noOneHere();
    }

    /**
     * 这里没人
     */
    protected abstract void noOneHere();

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.RED_SENSOR;
    }

}
