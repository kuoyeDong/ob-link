package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.cloud.bean.DeviceEnum;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;

import okhttp3.FormBody;

/**
 * use by:扫描设备，释放设备,修改名称
 * create by dky at 2019/7/3
 */
public abstract class DeviceHandler implements HttpRespond {

    /**
     * rf设备序列号
     */
    private String deviceSerId;

    /**
     *
     */
    private String pType;
    /**
     *
     */
    private String type;

    /**
     * 要添加设备的obox
     */
    private String oboxSerId;

    /**
     * 是否主动入网设备
     */
    private boolean isDc;

    /**
     * @param deviceSerId 操作rf设备的序列号
     */
    public DeviceHandler(String deviceSerId) {
        this.deviceSerId = deviceSerId;
    }

    /**
     * 添加设备
     *
     * @param deviceEnum 添加的设备类型
     * @param oboxSerId  通过哪个obox添加传该obox序列号
     * @param time       添加持续的时间，超过该时间无法再添加
     */
    public void addDevice(DeviceEnum deviceEnum, String oboxSerId, String time) {
        pType = String.valueOf(deviceEnum.getpType());
        type = String.valueOf(deviceEnum.getType());
        this.time = time;
        this.oboxSerId = oboxSerId;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEARCH_NEW_DEVICES);
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.MODIFY_DEVICE:

        }
    }


    /**
     * 扫描持续时间
     */
    private String time;

    @Override
    public FormBody.Builder getParamter(String action) {
        switch (action) {
            case CloudConstant.CmdValue.SEARCH_NEW_DEVICES:
                return GetParameter.onSearchNewDevicesOB(oboxSerId, isDc ? "03" : "02", time, "", pType, type);
        }
        return null;
    }
}
