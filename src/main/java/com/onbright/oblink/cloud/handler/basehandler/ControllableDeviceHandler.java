package com.onbright.oblink.cloud.handler.basehandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.cloud.net.CloudConstant;

/**
 * 可控制设备基类，实现控制的返回处理
 *
 * @author dky
 * 2019/8/6
 */
public abstract class ControllableDeviceHandler extends UncontrollableDeviceHandler {

    /**
     * 用于控制指令的发送
     */
    protected String sendStatus;

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected ControllableDeviceHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    public void onSuccess(String action, String json) {
        super.onSuccess(action, json);
        switch (action) {
            case CloudConstant.CmdValue.SETTING_NODE_STATUS:
                onGetStatus(json);
                break;
        }
    }

}
