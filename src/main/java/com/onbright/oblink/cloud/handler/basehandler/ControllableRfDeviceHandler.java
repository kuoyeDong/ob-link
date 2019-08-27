package com.onbright.oblink.cloud.handler.basehandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.cloud.CloudDataPool;
import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.BeAction;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.net.CloudConstant;

/**
 * 可控制Rf设备基类，实现控制的返回处理
 *
 * @author dky
 * 2019/8/6
 */
public abstract class ControllableRfDeviceHandler extends RfDeviceHandler implements BeAction {

    /**
     * 用于控制指令的发送
     */
    protected String sendStatus;

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected ControllableRfDeviceHandler(@Nullable String deviceSerId) {
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

    /**
     * 有些设备的控制协议中，要设置其中某个值，其他非计划设置的参数值没预留忽略值，
     * 所以这些值如果不按原值发送，极有可能会被改变，造成非预期控制结果，此方法将检查原值是否获取到，如无原值则回调
     *
     * @return 如无原始值返回true
     */
    protected boolean haveNotStatus() {
        if (status == null) {
            mustHaveStatus();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 必须要有原状态值回调，如有存储上次状态值请使用{@link #setStatus(String)},或使用查询状态方法{@link #queryDeviceStatus()}
     */
    protected abstract void mustHaveStatus();

    @Override
    public Action toAction(String actionProperty) throws Exception {
        Device device = CloudDataPool.getDeviceForSerId(deviceSerId);
        if (device == null) {
            throw new Exception("not find device,can not toAction");
        }
        Action action = new Action();
        action.setNode_type("00");
        action.setSerialId(deviceSerId);
        action.setAddr(device.getAddr());
        action.setObox_serial_id(device.getObox_serial_id());
        action.setDevice_type(device.getDevice_type());
        action.setDevice_child_type(device.getDevice_child_type());
        action.setActionName(device.getName());
        action.setAction(sendStatus);
        return action;
    }
}
