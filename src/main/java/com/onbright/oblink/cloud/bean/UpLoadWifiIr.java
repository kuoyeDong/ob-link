package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * Name:com.ob.obsmarthouse.common.act.wifiir.bean-UpLoadWifiIr
 * Used: 用于上传服务器设备参数配置
 * Author:Adolf_Dong
 * Date:2018/11/19-18-00 18:00
 */
public class UpLoadWifiIr implements Serializable {
    /**
     * 设备唯一id
     */
    private String deviceId;

    public UpLoadWifiIr(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getJsonStr() {
        return "{\"deviceId\":\"" + deviceId + "\",\"state\":[],\"type\":\"51\",\"name\":\"IR Transp" +
                "onder\",\"action\":[{\"function\":\"send\",\"dataType\":\"raw\",\"data\":[\"\"],\"fun" +
                "ctionName\":\"send\",\"functionTag\":\"control\",\"functionId\":1,\"dataTranType\":" +
                "[\"download\"]},{\"function\":\"receive\",\"dataType\":\"raw\",\"data\":[\"\"],\"func" +
                "tionName\":\"receive learning\",\"functionTag\":\"control\",\"functionId\":2,\"data" +
                "TranType\":[\"upload\"]},{\"function\":\"receive\",\"dataType\":\"raw\",\"data\":[\"\"]," +
                "\"functionName\":\"receive pairing\",\"functionTag\":\"control\",\"functionId\":" +
                "3,\"dataTranType\":[\"upload\"]},{\"function\":\"learning\",\"dataType\":\"int\",\"" +
                "data\":[0],\"functionName\":\"learning\",\"functionTag\":\"config\",\"functionId\":4," +
                "\"dataTranType\":[\"upload\",\"download\"]},{\"function\":\"pairing\",\"dataType\":" +
                "\"int\",\"data\":[0],\"functionName\":\"pairing\",\"functionTag\":\"config\",\"functi" +
                "onId\":5,\"dataTranType\":[\"upload\",\"download\"]}]}";
    }

}
