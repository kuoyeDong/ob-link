package com.onbright.oblink.cloud.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 上传给阿里云的标准化定义
 * Created by adolf_dong on 2018/3/19.
 */

public class AliConfig implements Serializable {
    /**
     * 阿里对应设备唯一id，即以产品序列号
     */
    private String deviceId;

    /**
     * 设备名称，可获取ob设备的名称
     */
    private String name;
    /**
     * 设备类型，对应单品DEVICE还是obox OBOX,
     * 参照Wifi设备#{@link com.ob.obsmarthouse.common.constant.OBConstant.NodeType#WIFI_IR}
     */
    private String type;


    /**
     * 设备功能定义
     */
    private List<AliSpec> action;

    /**
     * 设备状态
     */
    private List<AliDevState> state;

    public AliConfig(String deviceId, String name, String type, List<AliSpec> action, List<AliDevState> state) {
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.action = action;
        this.state = state;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AliSpec> getAction() {
        return action;
    }

    public void setAction(List<AliSpec> action) {
        this.action = action;
    }

    public List<AliDevState> getState() {
        return state;
    }

    public void setState(List<AliDevState> state) {
        this.state = state;
    }
}
