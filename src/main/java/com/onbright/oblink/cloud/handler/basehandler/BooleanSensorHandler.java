package com.onbright.oblink.cloud.handler.basehandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

/**
 * 属性为布尔类传感器处理类
 *
 * @author dky
 * 2019/8/7
 */
public abstract class BooleanSensorHandler extends UnControllableRfDeviceHandler implements BatteryDevice {

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected BooleanSensorHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected void onStatusChange(String status) {
        byte[] stausBytes = Transformation.hexString2Bytes(status);
        int openVal = MathUtil.validByte(stausBytes[1]);
        if (openVal == 0) {
            booleanFalse();
        } else {
            booleanTrue();
        }
        if (getDeviceEnum().getType() != OBConstant.NodeType.RED_SENSOR
                && getDeviceEnum().getType() != OBConstant.NodeType.AC_BODY_ALS) {
            batteryValue(MathUtil.validByte(stausBytes[getBatteryIndex()]));
        }
    }

    /**
     * 不同设备电量下标不同，子类可复写此方法返回正确电量字节下标
     *
     * @return 电量在状态的字节下标
     */
    protected int getBatteryIndex() {
        return 3;
    }

    /**
     * 有状态
     */
    public abstract void booleanTrue();

    /**
     * 无状态
     */
    public abstract void booleanFalse();

}
