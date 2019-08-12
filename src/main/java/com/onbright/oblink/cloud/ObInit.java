package com.onbright.oblink.cloud;

import android.content.Context;

import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.WifiDevice;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.cloud.net.MqttHandler;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * sdk初始化,获取昂宝云交互令牌
 *
 * @author dky
 * 2019/6/25
 */
public abstract class ObInit implements HttpRespond {
    public static Context CONTEXT;

    /**
     * 下级用户标识
     */
    private String uniqueKey;

    /**
     * 口令
     */
    public static String ACCESSTOKEN;

    /**
     * key，昂宝分配的key
     */
    public static String APP_KEY;

    /**
     * secret，与key对应的secret
     */
    public static String APP_SECRET;

    private MqttHandler mqttHandler;

    /**
     * @param appKey    key，昂宝分配的key
     * @param appSecret secret，与key对应的secret
     * @param uniqueKey 下级用户唯一标识
     */
    public ObInit(String appKey, String appSecret, String uniqueKey, Context context) {
        APP_KEY = appKey;
        APP_SECRET = appSecret;
        this.uniqueKey = uniqueKey;
        CONTEXT = context;
    }

    /**
     * 初始化
     */
    public void init() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.INIT,
                GetParameter.onInit(), "/oauth/token?grant_type=client_credentials", HttpRequst.POST);
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.INIT:
                String cacheToken = CloudParseUtil.getJsonParm(json, CloudConstant.ParameterKey.ACCESS_TOKEN);
                HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.INIT_SECOND,
                        GetParameter.onInitSecond(), "/login/company?accessToken=" + cacheToken + "&" + "uniqueKey=" + uniqueKey,
                        HttpRequst.POST);
                break;
            case CloudConstant.CmdValue.INIT_SECOND:
                ACCESSTOKEN = CloudParseUtil.getJsonParm(json, CloudConstant.ParameterKey.ACCESS_TOKEN);
                mqttHandler = new MqttHandler(CONTEXT, ACCESSTOKEN, uniqueKey);
                HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_DEVICE,
                        GetParameter.onQueryDevice(0, 0), CloudConstant.Source.CONSUMER_OPEN,
                        HttpRequst.POST);
                break;
            case CloudConstant.CmdValue.QUERY_DEVICE:
                CloudParseUtil.initDevice(json, CloudDataPool.getDevices());
                HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_ALI_DEV,
                        GetParameter.queryAliDevice(), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
                break;
            case CloudConstant.CmdValue.QUERY_ALI_DEV:
                JSONArray jsonArray = CloudParseUtil.getJsonArryParm(json, CloudConstant.ParameterKey.CONFIGS);
                CloudDataPool.getWifiDevices().clear();
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    WifiDevice wifiDevice = null;
                    try {
                        wifiDevice = gson.fromJson(jsonArray.getString(i), WifiDevice.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CloudDataPool.addWifiDevice(wifiDevice);
                }
                onInitSuc();
                break;
        }
    }

    /**
     * 初始化成功
     */
    public abstract void onInitSuc();

    /**
     * 释放资源
     */
    public void destory() {
        CONTEXT = null;
        uniqueKey = null;
        ACCESSTOKEN = null;
        if (mqttHandler != null) {
            mqttHandler.shutDown();
        }
    }
}
