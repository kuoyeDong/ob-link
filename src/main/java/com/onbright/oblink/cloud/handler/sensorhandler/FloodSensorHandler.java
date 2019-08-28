package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.bean.Condition;
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
     * 水浸
     */
    protected abstract void wet();

    /**
     * 取得水浸状态条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition wetToCondition() throws Exception {
        return booleanTrueToCondition();
    }

    @Override
    public void booleanFalse() {
        dry();
    }

    /**
     * 无水浸
     */
    protected abstract void dry();

    /**
     * 取得无水浸状态条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition dryToCondition() throws Exception {
        return booleanFalseToCondition();
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.FLOOD;
    }

}
