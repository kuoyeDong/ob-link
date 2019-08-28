package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.bean.Condition;
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

    /**
     * 取得门窗磁打开状态条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition doorOrWindowOpenToCondition() throws Exception {
        return booleanTrueToCondition();
    }

    @Override
    public void booleanFalse() {
        doorOrWindowClose();
    }

    /**
     * 门窗磁关闭
     */
    protected abstract void doorOrWindowClose();

    /**
     * 取得门窗磁关闭状态条件对象
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition doorOrWindowCloseToCondition() throws Exception {
        return booleanFalseToCondition();
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.DOOR_WINDOW_MAGNET;
    }

}
