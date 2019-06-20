package com.onbright.oblink.cloud.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.Obox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器模式提供扫描节点、删除节点、和释放单个obox内所有节点功能的类。
 * <p>
 * 如果要扫描节点即使得节点与obox建立关系，obox为节点分配通讯地址，请使用{@link #startScan(Obox)}。
 * <p>
 * 如果要停止扫描节点请使用{@link #stopScan(Obox)},这两个方法请求成功后，会回调{@link #onStartOrStopScanSuc()}。
 * <p>
 * 直至下次成功扫面之前，调用{@link #rqNewDevice(Obox)}即可在回调{@link #onReqNewDeviceSuc(List)}获取到新入网的节点。
 * <p>
 * 如果要删除单个节点，请使用{@link #removeDevice(DeviceConfig)}，成功将回调{@link #onRemoveDeviceSuc()}。
 * <p>
 * 如果要释放一个obox内的所有节点，请使用{@link #freeAllDevice(Obox)},成功将回调{@link #onFreeAllDeviceSuc()}。
 * <p>
 * 本类所有的请求失败都将回调{@link #onOperationFailed(String, String)}
 */

public abstract class SScanAndReleaseHelper implements HttpRespond {
    private static final String TAG = "SScanAndReleaseHelper";

    /**
     * 指定某个obox开始扫描新节点
     *
     * @param obox 目标obox
     */
    public void startScan(Obox obox) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEARCH_NEW_DEVICES,
                GetParameter.onSearchNewDevices(obox.getObox_serial_id(), "02", "60"));
    }

    /**
     * 指定某个obox停止扫描
     *
     * @param obox 目标obox
     */
    public void stopScan(Obox obox) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEARCH_NEW_DEVICES,
                GetParameter.onSearchNewDevices(obox.getObox_serial_id(), "00", "60"));
    }

    /**
     * 在某个obox扫描的时候或者扫描结束后请求新找到的节点
     *
     * @param obox 目标obox
     */
    public void rqNewDevice(Obox obox) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.GET_NEW_DEVICES, GetParameter.onGetNewDevices(obox.getObox_serial_id()));
    }

    /**
     * 把某个节点移除出系统
     *
     * @param deviceConfig 目标节点
     */
    public void removeDevice(DeviceConfig deviceConfig) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.MODIFY_DEVICE, GetParameter.onModifyDevice(deviceConfig.getSerialId(),
                deviceConfig.getName(), true));
    }


    /**
     * 释放obox的所有设备，此操作会将obox内的组，情景数据也清空
     *
     * @param obox 目标obox
     */
    public void freeAllDevice(Obox obox) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.RELEASE_ALL_DEVICES, GetParameter.onReleaseDevice(obox.getObox_serial_id()));
    }

    @Override
    public void onRequest(String action) {
        Log.d(TAG, "onRequest: " + action);
    }

    @Override
    public void onSuccess(String action, String json) {
        Log.d(TAG, "onSuccess: >>" + action);
        switch (action) {
            case CloudConstant.CmdValue.SEARCH_NEW_DEVICES:
                onStartOrStopScanSuc();
                break;
            case CloudConstant.CmdValue.GET_NEW_DEVICES:
                Gson gson = new Gson();
                try {
                    List<DeviceConfig> deviceList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("nodes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DeviceConfig deviceConfig = gson.fromJson(jsonArray.getString(i), DeviceConfig.class);
                        deviceList.add(deviceConfig);
                        deviceList.add(deviceConfig);
                    }
                    onReqNewDeviceSuc(deviceList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CloudConstant.CmdValue.MODIFY_DEVICE:
                onRemoveDeviceSuc();
                break;
            case CloudConstant.CmdValue.RELEASE_ALL_DEVICES:
                onFreeAllDeviceSuc();
        }
    }

    /**
     * 释放obox节点成功回调
     */
    protected abstract void onFreeAllDeviceSuc();

    /**
     * 删除节点成功
     */
    public abstract void onRemoveDeviceSuc();

    /**
     * 请求新加入的节点成功回调
     *
     * @param deviceList 新加入的节点容器
     */
    public abstract void onReqNewDeviceSuc(List<DeviceConfig> deviceList);


    /**
     * 开始或者停止扫描成功回调
     */
    public abstract void onStartOrStopScanSuc();


    @Override
    public void onFaild(String action, Exception e) {
        Log.d(TAG, "onFaild: >>" + action);
        onFaild(action);
    }

    /**
     * 与服务器连接网络出错导致的失败
     *
     * @param action 出错时执行的动作
     */
    public abstract void onFaild(String action);

    @Override
    public void onFaild(String action, int state) {
        Log.d(TAG, "onFaild: >>" + action);
        onFaild(action);
    }

    @Override
    public void onRespond(String action) {
        Log.d(TAG, "onRespond: >>" + action);
    }

    @Override
    public void operationFailed(String action, String json) {
        Log.d(TAG, "operationFailed: >>" + action);
        onOperationFailed(action, json);
    }

    /**
     * 操作失败
     *
     * @param action 失败时候的动作
     * @param json   失败原因代码
     */
    public abstract void onOperationFailed(String action, String json);
}
