package com.onbright.oblink.cloud.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.bean.Groups;
import com.onbright.oblink.cloud.bean.User;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.Obox;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器模式初始化数据的类，此类提供完整的服务器模式指定账户的节点、组、情景等配置获取，服务器帮助包中除了{@link SregistHelper}之外的其他类之前都应确保使用过一次此类获取配置。
 * <p>
 * 注意：如果有账户，应该在第一步使用此类，如果没有账户请使用{@link SregistHelper#regist(String, String, String)}注册账户。
 * <p>
 * 使用方式：先调用{@link #setUser(String)#setPwd(String)}设置要登陆的账户和密码，再调用{@link #startInit()}触发服务器请求，
 * 请求成功后可在回调{@link #onFinish(List, List, List, List)}获取账户内数据;任何失败都会回调{@link #onOperationFailed(String, String)}
 */

public abstract class SInitConfigHelper implements HttpRespond {
    private static final String TAG = "SInitConfigHelper";
    private String name;
    private String pwd;

    private Gson gson;
    private List<DeviceConfig> deviceConfigs = new ArrayList<>();
    private List<Groups> groupses = new ArrayList<>();
    private List<CloudScene> cloudScenes = new ArrayList<>();
    private List<Obox> oboxes = new ArrayList<>();

    /**
     * @param name 用户名
     * @param pwd  密码
     */
    public SInitConfigHelper(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
        gson = new Gson();
    }

    /**
     * 设置用户名称
     *
     * @param name 用户名称
     */
    public void setUser(String name) {
        this.name = name;
    }

    /**
     * 设置用户密码
     *
     * @param pwd 用户密码
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * 开始登陆，获取服务器配置
     */
    public void startInit() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.LOGIN,
                GetParameter.onLogin(name, pwd));
    }

    /**
     * 登录和获取配置成功结束的回调
     *
     * @param deviceConfigs 装载单节点的容器
     * @param groupses      装载组的容器
     * @param cloudScenes   装载情景的容器
     * @param oboxes        装载obox的容器
     */
    public abstract void onFinish(List<DeviceConfig> deviceConfigs, List<Groups> groupses, List<CloudScene> cloudScenes, List<Obox> oboxes);

    @Override
    public void onRequest(String action) {
        Log.d(TAG, "onRequest:>> " + action);
    }

    @Override
    public void onSuccess(String action, String json) {
        Log.d(TAG, "onSuccess:>> " + action);
        switch (action) {
            case CloudConstant.CmdValue.LOGIN:
                GetParameter.ACCESSTOKEN = CloudParseUtil.getJsonParm(json, CloudConstant.ParameterKey.ACCESS_TOKEN);
                User user = User.getUser();
                user.setName(this.name);
                user.setWeight(CloudParseUtil.getJsonParm(json, CloudConstant.ParameterKey.WEIGHT));
                HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_DEVICE, GetParameter.onQueryDevice(null, 0, 0));
                break;
            case CloudConstant.CmdValue.QUERY_DEVICE:
                CloudParseUtil.initDevice(json, deviceConfigs);
                HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_GROUP, GetParameter.onQueryGroups());
                break;
            case CloudConstant.CmdValue.QUERY_GROUP:
                try {
                    JSONArray jsonArray = CloudParseUtil.getJsonArryParm(json, "groups");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Groups groups = gson.fromJson(jsonArray.getString(i), Groups.class);
                        groupses.add(groups);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_SCENES, GetParameter.onQueryScenes());
                break;
            case CloudConstant.CmdValue.QUERY_SCENES:
                try {
                    JSONArray jsonArray = CloudParseUtil.getJsonArryParm(json, "scenes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        CloudScene scenes = gson.fromJson(jsonArray.getString(i), CloudScene.class);
                        cloudScenes.add(scenes);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_OBOX, GetParameter.onQueryObox());
                break;
            case CloudConstant.CmdValue.QUERY_OBOX:
                try {
                    JSONArray jsonArray = CloudParseUtil.getJsonArryParm(json, "oboxs");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Obox oboxs = gson.fromJson(jsonArray.getString(i), Obox.class);
                        oboxes.add(oboxs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onFinish(deviceConfigs, groupses, cloudScenes, oboxes);
                break;
            default:
                break;
        }
    }

    @Override
    public void onFaild(String action, Exception e) {
        Log.d(TAG, "onFaild: >>" + action);
    }

    @Override
    public void onFaild(String action, int state) {
        Log.d(TAG, "onFaild: >>" + action + "state >>" + state);
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
     * 操作失败的回调
     *
     * @param action 失败操作时的动作
     * @param json   失败原因代码
     */
    public abstract void onOperationFailed(String action, String json);

}
