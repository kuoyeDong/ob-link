package com.onbright.oblink.cloud.handler.basehandler;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;

/**
 * 不可控制设备基类,实现状态获取处理
 *
 * @author dky
 * 2019/8/7
 */
public abstract class UncontrollableDeviceHandler extends RfDeviceHandler {
    /**
     * 设备状态
     */
    protected String status;

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected UncontrollableDeviceHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 请求设备状态，成功后回调{@link #onStatusChange(String)}，
     * 电池类设备休眠时，请求不到设备状态，所以，建议自行保存设备状态
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
            case CloudConstant.CmdValue.QUERY_NODE_REAL_STATUS:
                onGetStatus(json);
                break;
        }
    }

    protected void onGetStatus(String json) {
        String status = CloudParseUtil.getJsonParm(json, "status");
        if (!TextUtils.isEmpty(status)) {
            this.status = status;
            onStatusChange(status);
        }
    }

}
