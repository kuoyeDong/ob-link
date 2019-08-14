package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.handler.basehandler.RfDeviceHandler;

/**
 * 处理插座
 *
 * @author dky
 * 2019/8/14
 */
public abstract class SocketHandler extends RfDeviceHandler {

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected SocketHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected void onStatusChange(String status) {

    }

    @Override
    protected void onNewDevice(Device device) {

    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.SOCKET;
    }

}
