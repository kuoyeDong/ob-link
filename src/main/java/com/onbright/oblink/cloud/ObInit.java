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
 * use by:sdk初始化,获取昂宝云交互令牌
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
     * 下级用户标识
     */
    public static String UNIQUE_KEY;

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
     * @param uniqueKey  下级用户唯一标识
     */
    public ObInit(String appKey, String appSecret, String uniqueKey, Context context) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        UNIQUE_KEY = uniqueKey;
        CONTEXT = context;
    }

    /**
     * 初始化，换取token
     */
    public void init() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.INIT);
    }

    @Override
    public void onSuccess(String action, String json) {
        mqttHandler = new MqttHandler(CONTEXT, ACCESSTOKEN, UNIQUE_KEY);
        ACCESSTOKEN = CloudParseUtil.getJsonParm(json, "token");
        onInitSuc(ACCESSTOKEN);
    }

    @Override
    public FormBody.Builder getParamter(String action) {
        return GetParameter.onInit(appKey, appSecret);
    }

    /**
     * 初始化成功
     *
     * @param token 企业的访问令牌
     */
    public abstract void onInitSuc(String token);

    /**
     * 释放所有资源
     */
    public void destory() {
        CONTEXT = null;
        SYSTEM_NAME = null;
        APPLICATION_NAME = null;
        UNIQUE_KEY = null;
        ACCESSTOKEN = null;
        if (mqttHandler != null) {
            mqttHandler.shutDown();
        }
    }
}
