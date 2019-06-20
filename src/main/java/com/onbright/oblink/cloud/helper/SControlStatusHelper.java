package com.onbright.oblink.cloud.helper;

import android.util.Log;

import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.bean.Groups;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.net.Transformation;

/**
 * 服务器设置节点状态、组状态，请注意，虽然本类没做限制，调用本类设置方法，间隔请超过500ms，最好超过1000ms
 * 如果要设置组状态，请使用{@link #setGropStatus(byte[], Groups)},成功则回调{@link #onOperationSuc(String)}并携带设置成功后的状态，
 * 失败则回调{@link #onOperationFailed(String)},并携带出错代码。
 * <p>
 * 如果要设置节点状态，请使用{@link #setNodeStatus(byte[], DeviceConfig)},成功则回调{@link #onOperationSuc(String)}并携带设置成功后的状态，
 * 失败则回调{@link #onOperationFailed(String)},并携带出错代码。
 */

public abstract class SControlStatusHelper implements HttpRespond {

    private static final String TAG = "SControlStatusHelper";

    /**
     * 设置节点状态
     *
     * @param status       目标状态
     * @param deviceConfig 目标节点
     */
    public void setNodeStatus(byte[] status, DeviceConfig deviceConfig) {
        deviceConfig.setState(Transformation.byteArryToHexString(status));
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS, GetParameter.onSetNodeState(deviceConfig, false));
    }

    /**
     * 设置组状态
     *
     * @param status 目标状态
     * @param groups 目标组
     */
    public void setGropStatus(byte[] status, Groups groups) {
        groups.setState(Transformation.byteArryToHexString(status));
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SET_GROUP, GetParameter.onSetGroup(groups.getGroup_id(), "",
                Transformation.byteArryToHexString(status), "06", "", groups.getGroup_style(), groups.getObox_serial_num(), groups.getAddr()));
    }

    @Override
    public void onRequest(String action) {
        Log.d(TAG, "onRequest: " + action);
    }

    @Override
    public void onSuccess(String action, String json) {
        Log.d(TAG, "onSuccess: >>" + action);
        String status = null;
        switch (action) {
            case CloudConstant.CmdValue.SETTING_NODE_STATUS:
                status = CloudParseUtil.getJsonParm(json, "status");
                break;
            case CloudConstant.CmdValue.SET_GROUP:
                String gropsJstring = CloudParseUtil.getJsonParm(json, "groups");
                status = CloudParseUtil.getJsonParm(gropsJstring, "group_state");
                break;
        }
        if (status != null && !status.equals("null")) {
            onOperationSuc(status);
        }
    }

    /**
     * 设置成功
     *
     * @param status 成功后的状态
     */
    public abstract void onOperationSuc(String status);


    @Override
    public void onFaild(String action, Exception e) {
        Log.d(TAG, "onFaild: >>" + action);
    }

    @Override
    public void onFaild(String action, int state) {
        Log.d(TAG, "onFaild: >>" + action + "state>>" + state);
    }

    @Override
    public void onRespond(String action) {
        Log.d(TAG, "onRespond: " + action);
    }

    @Override
    public void operationFailed(String action, String json) {
        Log.d(TAG, "operationFailed: >>" + action);
        onOperationFailed(json);
    }

    /**
     * 操作失败回调
     *
     * @param json 失败原因代码
     */
    public abstract void onOperationFailed(String json);
}
