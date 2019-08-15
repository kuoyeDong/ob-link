package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;

/**
 * 处理AC人体+光感
 *
 * @author dky
 * 2019/8/15
 */
public abstract class AcBodyAlsHandler extends DcBodyAlsHandler {
    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected AcBodyAlsHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.AC_BODY_ALS;
    }

}
