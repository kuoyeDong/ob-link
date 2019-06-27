package com.onbright.oblink.cloud;

import android.content.Context;

import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.cloud.net.MqttHandler;

import okhttp3.FormBody;


/**
 * use by:sdk初始化
 * create by dky at 2019/6/25
 */
public abstract class ObInit implements HttpRespond {
    public static Context CONTEXT;
    /**
     * 系统名称版本号，如Android26
     */
    public static String SYSTEM_NAME;

    /**
     * app名称,如obsmart
     */
    public static String APPLICATION_NAME;

    /**
     * Android设备序列号
     */
    public static String DEVICE_ID;

    public static String ACCESSTOKEN;

    /**
     * key，昂宝分配的key
     */
    private String appKey;

    /**
     * secret，与key对应的secret
     */
    private String appSecret;

    private MqttHandler mqttHandler;

    /**
     * @param appKey    key，昂宝分配的key
     * @param appSecret secret，与key对应的secret
     * @param deviceId  Android设备序列号
     */
    public ObInit(String appKey, String appSecret, String deviceId, Context context) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        DEVICE_ID = deviceId;
        CONTEXT = context;
    }

    /**
     * 初始化
     */
    public void init() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.INIT);
    }

    @Override
    public void onSuccess(String action, String json) {
        mqttHandler = new MqttHandler(CONTEXT, ACCESSTOKEN, DEVICE_ID);
        ACCESSTOKEN = CloudParseUtil.getJsonParm(json, "token");
        onInitSuc(ACCESSTOKEN);
    }

    @Override
    public FormBody.Builder getParamter(String action) {
        return GetParameter.onInit(appKey, appSecret);
    }

    public abstract void onInitSuc(String token);

    /**
     * 释放所有资源
     */
    public void destory() {
        CONTEXT = null;
        SYSTEM_NAME = null;
        APPLICATION_NAME = null;
        DEVICE_ID = null;
        ACCESSTOKEN = null;
        if (mqttHandler != null) {
            mqttHandler.shutDown();
        }
    }
}
