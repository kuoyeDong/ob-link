package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.handler.basehandler.BooleanSensorHandler;
import com.onbright.oblink.local.net.Transformation;

/**
 * 处理烟雾传感器
 *
 * @author dky
 * 2019/8/7
 */
public abstract class SmokeSensorHandler extends BooleanSensorHandler {
    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected SmokeSensorHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    public void booleanTrue() {
        smoke();
    }

    /**
     * 有烟雾
     */
    protected abstract void smoke();

    /**
     * 取得有烟雾条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition smokeToCondition() throws Exception {
        return booleanTrueToCondition();
    }

    @Override
    public void booleanFalse() {
        noSmoke();
    }

    /**
     * 干净了，没有烟雾
     */
    protected abstract void noSmoke();

    /**
     * 取得没有烟雾条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition noSmokeToCondition() throws Exception {
        return booleanFalseToCondition();
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.SMOKE_SENSOR;
    }

}
