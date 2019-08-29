package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.handler.basehandler.BatteryDevice;
import com.onbright.oblink.cloud.handler.basehandler.UnControllableRfDeviceHandler;
import com.onbright.oblink.cloud.handler.scenehandler.ConditionRule;
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

    /**
     * 设置温度湿度场景条件
     *
     * @param aboutTemp  是否和温度检测有关
     * @param tempRule   温度规则
     * @param tempVal    对应温度值(零下49-80摄氏度)
     * @param aboutHumid 是否和湿度检测有关
     * @param humidRule  湿度规则
     * @param humidVal   对应湿度值(0-100百分比)
     * @return 场景条件
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition tempHumidtoCondition(boolean aboutTemp, ConditionRule tempRule, int tempVal,
                                          boolean aboutHumid, ConditionRule humidRule, int humidVal) throws Exception {
        byte[] condition = new byte[8];
        if (aboutTemp) {
            condition[0] = (byte) tempRule.getVal();
            condition[1] = (byte) (tempVal + 30);
        } else {
            condition[0] = (byte) 0x4C;
            condition[1] = (byte) 0xFF;
        }
        if (aboutHumid) {
            condition[2] = (byte) humidRule.getVal();
            condition[3] = (byte) humidVal;
        }
        return toCondition(Transformation.byteArryToHexString(condition));
    }

}
