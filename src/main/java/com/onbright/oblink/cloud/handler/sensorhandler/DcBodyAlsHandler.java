package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.net.Transformation;

/**
 * 处理DC人体+光感
 * @author dky
 * 2019/8/15
 */
public abstract class DcBodyAlsHandler extends BodySensorHandler {
    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected DcBodyAlsHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.DC_BODY_ALS;
    }

    @Override
    protected void onStatusChange(String status) {
        super.onStatusChange(status);
        byte[] statusBytes = Transformation.hexString2Bytes(status);
        onLightLevel(MathUtil.validByte(statusBytes[3]));
    }

    /**
     * 得到光照等级
     *
     * @param lightLevel 从弱到强，1-5
     */
    protected abstract void onLightLevel(int lightLevel);

    @Override
    protected int getBatteryIndex() {
        return 7;
    }
}
