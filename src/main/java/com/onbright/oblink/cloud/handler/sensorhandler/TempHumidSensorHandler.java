package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.handler.basehandler.BatteryDevice;
import com.onbright.oblink.cloud.handler.basehandler.UnControllableRfDeviceHandler;
import com.onbright.oblink.local.net.Transformation;

/**
 * 处理温湿度传感器
 *
 * @author dky
 * 2019/8/7
 */
public abstract class TempHumidSensorHandler extends UnControllableRfDeviceHandler implements BatteryDevice {

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected TempHumidSensorHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected void onStatusChange(String status) {
        byte[] stausBytes = Transformation.hexString2Bytes(status);
        if (stausBytes[0] != 0) {
            int tempVal = (MathUtil.validByte(stausBytes[1]) - 30);
            int humiVal = MathUtil.validByte(stausBytes[3]);
            onTempVal(tempVal);
            onHumiVal(humiVal);
        }
        batteryValue(MathUtil.validByte(stausBytes[5]));
    }

    /**
     * 获取到温度值
     *
     * @param tempVal 温度值，单位摄氏度
     */
    protected abstract void onTempVal(int tempVal);

    /**
     * 获取到湿度
     *
     * @param humiVal 湿度值，单位百分比，例如60则湿度为为百分之60
     */
    protected abstract void onHumiVal(int humiVal);

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.TEMP_HUMID_SENSOR;
    }

}
