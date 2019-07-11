package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.net.OBConstant;

/**
 * use by:扫描设备，释放设备,状态回调
 * create by dky at 2019/7/3
 */
abstract class DeviceHandler implements HttpRespond {

    /**
     * rf设备序列号
     */
    private String deviceSerId;

    /**
     * 扫描设备类型
     */
    private String pType;
    /**
     * 扫描设备子类型
     */
    private String type;

    /**
     * 要添加设备的obox
     */
    private String oboxSerId;

    /**
     * 扫描持续时间
     */
    private String time = "30";

    /**
     * 操作现有设备用此方法
     *
     * @param deviceSerId 操作rf设备的序列号
     */
    public DeviceHandler(String deviceSerId) {
        this.deviceSerId = deviceSerId;
    }

    /**
     * 添加设备用此方法
     */
    public DeviceHandler() {

    }

    /**
     * 添加设备
     *
     * @param deviceEnum 添加的设备类型
     * @param oboxSerId  通过哪个obox添加传该obox序列号
     * @param time       添加持续的时间，超过该时间无法再添加,默认30s
     */
    public void searchNewDevice(DeviceEnum deviceEnum, String oboxSerId, String time) {
        pType = String.valueOf(deviceEnum.getpType());
        type = String.valueOf(deviceEnum.getType());
        if (time != null) {
            this.time = time;
        }
        this.oboxSerId = oboxSerId;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEARCH_NEW_DEVICES,
                GetParameter.onSearchNewDevicesOB(oboxSerId, isDc(deviceEnum.getpType(),
                        deviceEnum.getType()) ? "03" : "02", time, "", pType, type),
                CloudConstant.Source.CONSUMER_OPEN + "device", HttpRequst.POST);
    }


    /**
     * 删除设备
     *
     * @param deviceSerId 要删除的设备序列号
     */
    public void deleteDevice(String deviceSerId) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_DEVICE, GetParameter.deleteDevice(deviceSerId),
                CloudConstant.Source.CONSUMER_OPEN + "device", HttpRequst.DELETE);
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.SEARCH_NEW_DEVICES:
                searchNewDeviceSuc();
                break;
            case CloudConstant.CmdValue.DELETE_DEVICE:
                deleteDeviceSuc();
                break;
        }
    }

    /**
     * 删除设备成功
     */
    public abstract void deleteDeviceSuc();

    /**
     * 启动扫描成功
     */
    public abstract void searchNewDeviceSuc();


    /**
     * 获取设备入网是否为主动入网方式
     *
     * @param pType 设备类型
     * @param type  设备子类型
     * @return 如果是主动入网方式则返回true
     */
    private boolean isDc(int pType, int type) {
        switch (pType) {
            case OBConstant.NodeType.IS_SENSOR:
                switch (type) {
                    case OBConstant.NodeType.FLOOD:
                    case OBConstant.NodeType.TEMP_HUMID_SENSOR:
                    case OBConstant.NodeType.DC_RED_SENSOR:
                    case OBConstant.NodeType.DOOR_WINDOW_MAGNET:
                        return true;
                }
                break;
            case OBConstant.NodeType.IS_OBSOCKET:
                switch (type) {
                    case OBConstant.NodeType.ONE_BUTTON_WIRE_SOCKET:
                    case OBConstant.NodeType.TWO_BUTTON_WIRE_SOCKET:
                        return true;
                }
                break;
            case OBConstant.NodeType.SMART_LOCK:
                return true;
        }
        return false;
    }
}
