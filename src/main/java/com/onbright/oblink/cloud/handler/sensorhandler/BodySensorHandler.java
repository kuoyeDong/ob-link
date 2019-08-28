package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.bean.Condition;
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
     * 有人
     */
    protected abstract void someOneHere();

    /**
     * 取得有人状态条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition someOneHereToCondition() throws Exception {
        return booleanTrueToCondition();
    }

    @Override
    public void booleanFalse() {
        noOneHere();
    }

    /**
     * 无人
     */
    protected abstract void noOneHere();

    /**
     * 取得无人状态条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition noOneHereToCondition() throws Exception {
        return booleanFalseToCondition();
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.RED_SENSOR;
    }

}
