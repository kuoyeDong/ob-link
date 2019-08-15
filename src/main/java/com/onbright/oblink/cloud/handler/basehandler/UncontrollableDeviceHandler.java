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

    /**
     * 返回当前对象内的现有设备状态，并无网络交互，
     * 可通过此方法获取到状态值保存，下次使用本类时可取出该状态值使用{@link #setStatus(String)}初始化状态值
     *
     * @return 设备状态
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置初始状态，并无网络交互，会调用{@link #onStatusChange(String)}
     *
     * @param status 七个字节的初始状态
     */
    public void setStatus(String status) {
        this.status = status;
        onStatusChange(status);
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

    /**
     * 处理状态
     *
     * @param json 设置或获取状态json
     */
    protected void onGetStatus(String json) {
        String status = CloudParseUtil.getJsonParm(json, "status");
        if (!TextUtils.isEmpty(status)) {
            this.status = status;
            onStatusChange(status);
        }
    }

}
