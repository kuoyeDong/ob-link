package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;

/**
 * use by:可控制设备基类，实现控制的返回处理
 * create by dky at 2019/8/6
 */
public abstract class ControllableDeviceHandler extends DeviceHandler {

    /**
     * 设备状态
     */
    protected String status;

    /**
     * 用于控制指令的发送
     */
    protected String sendStatus;

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String)}操作
     * @param status      初始化状态，如为null则会执行请求设备真实状态操作，请求操作不一定会成功
     */
    protected ControllableDeviceHandler(@Nullable String deviceSerId, String status) {
        super(deviceSerId);
        if (status != null) {
            this.status = status;
        }
        if (status == null) {
            queryDeviceStatus();
        }
    }

    /**
     * 请求设备状态，成功后回调{@link #onStatusChange(String)}
     */
    public void queryDeviceStatus() {
        if (isNoSerId()) {
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_NODE_REAL_STATUS,
                GetParameter.getNodeStatus(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    @Override
    public void onSuccess(String action, String json) {
        super.onSuccess(action, json);
        switch (action) {
            case CloudConstant.CmdValue.SETTING_NODE_STATUS:
            case CloudConstant.CmdValue.QUERY_NODE_REAL_STATUS:
                String status = CloudParseUtil.getJsonParm(json, "status");
                if (!TextUtils.isEmpty(status)) {
                    this.status = status;
                    onStatusChange(status);
                }
                break;
        }
    }

}
