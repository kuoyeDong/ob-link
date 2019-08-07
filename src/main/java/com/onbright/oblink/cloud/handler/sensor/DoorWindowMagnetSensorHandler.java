package com.onbright.oblink.cloud.handler.sensor;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.handler.basehandler.BooleanSensorHandler;

/**
 * 门窗磁处理
 *
 * @author dky
 * 2019/8/7
 */
public abstract class DoorWindowMagnetSensorHandler extends BooleanSensorHandler {

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected DoorWindowMagnetSensorHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    public void booleanTrue() {
        doorOrWindowOpen();
    }

    /**
     * 门窗磁打开
     */
    protected abstract void doorOrWindowOpen();

    @Override
    public void booleanFalse() {
        doorOrWindowClose();
    }

    /**
     * 门窗磁关闭
     */
    protected abstract void doorOrWindowClose();

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.DOOR_WINDOW_MAGNET;
    }

}
