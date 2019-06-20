package com.onbright.oblink.cloud.net;


import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.bean.User;
import com.onbright.oblink.local.Obox;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class GetParameter {
    /**
     * 执行动作
     */
    public static String ACTION;

    /**
     * 口令
     */
    public static String ACCESSTOKEN;
    public static int ACTION_TIME = 1;

    /**
     * 注册
     *
     * @param name 用户名
     * @param psw  密码
     */
    public static List<NameValuePair> onRegister(String name, String psw, String license) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.REGISTER));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.USERNAME, name));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PASS_WORD, psw));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.LICENSE, license));
        return nvps;

    }

    /**
     * 登录
     *
     * @param name 用户名
     * @param psw  密码
     */
    public static List<NameValuePair> onLogin(String name, String psw) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.LOGIN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.USERNAME, name));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PASS_WORD, psw));
        return nvps;
    }

    /**
     * 修改密码
     *
     * @param type 用户类型
     * @param pwd  密码
     */
    public static List<NameValuePair> setPassword(String type, String serialId, String pwd) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SET_PASSWORD));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, type));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PASS_WORD, pwd));
        if (type.equals("01")) {
            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, serialId));
        }
        return nvps;
    }

    /**
     * 上传之前的检测，如果返回true则存在以及绑定成功，如果返回false则不存在，可执行后续addobox（）
     *
     * @param obox 选中的obox
     */
    public static List<NameValuePair> onQueryOboxBind(Obox obox) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_OBOX_BIND));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        return nvps;
    }

    /**
     * 添加obox
     *
     * @return 控制数据集
     */
    public static List<NameValuePair> onAddObox(Obox obox) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.ADD_OBOX));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        Gson gson = new Gson();
        String json = gson.toJson(obox);
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX, json));
        return nvps;

    }

    /**
     * 查询摄像头
     */
    public static List<NameValuePair> queryCamera() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_CAMERA));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        return nvps;

    }

    /**
     * 查询摄像头直播地址
     */
    public static List<NameValuePair> queryCameraLiveAddress(String deviceSerial) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_CAMERA_LIVE_ADDR));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL, deviceSerial));
        return nvps;

    }

    /**
     * 摄像头抓拍
     */
    public static List<NameValuePair> setCameraCapture(String deviceSerial) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SET_CAMERA_CAPTURE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL, deviceSerial));
        return nvps;

    }

    /**
     * 设置摄像头云台
     */
    public static List<NameValuePair> setCameraPtz(String deviceSerial, int direction, int speed, int action) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SET_CAMERA_PTZ));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL, deviceSerial));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DIRECTION, String.valueOf(direction)));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SPEED, String.valueOf(speed)));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, String.valueOf(action)));
        return nvps;

    }


    /**
     * 设置单节点或者组节点的状态
     *
     * @param deviceConfig 节点
     * @param isBili       是否闪烁
     * @return
     */
    public static List<NameValuePair> onSetNodeState(DeviceConfig deviceConfig, boolean isBili) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_NODE_STATUS));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
//        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, deviceConfig.getObox_serial_id()));
//        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUPADDR, "00"));
//        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADDR, deviceConfig.getAddr()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, deviceConfig.getSerialId()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.STATE, !isBili ?
                deviceConfig.getState().substring(0, 12) + "02" : deviceConfig.getState().substring(0, 12) + "ff"));
        return nvps;
    }

    private static final String TAG = "GetParameter";


    /**
     * 删除obox
     *
     * @param obox 要删除的obox  在每次点击操作之前替换引用指向的实例
     */
    public static List<NameValuePair> onDelObox(Obox obox) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DELETE_OBOX));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.FORCE, CloudConstant.ParameterValue.FORCE_TRUE));
        return nvps;
    }

    /**
     * 组结构操作
     *
     * @param obox         当前操作的obox
     * @param isAdd        是添加还是删除
     * @param superId      组id
     * @param deviceConfig 当前操作的节点
     */
    public static List<NameValuePair> onOpreGoupMeb(Obox obox, boolean isAdd, String superId, DeviceConfig deviceConfig) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.OPERATE_GROUP_MEMBERS));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATE_TYPE, isAdd ? CloudConstant.ParameterValue.IS_ADD_MEMBER : CloudConstant.ParameterValue.IS_DEL_MEMBER));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SUPERID, superId));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_ID, deviceConfig.getName()));
        return nvps;
    }

    /**
     * @param obox         当前obox
     * @param deviceConfig 要删除的组中的节点，以此传入组id
     */
    public static List<NameValuePair> onDelGoup(Obox obox, DeviceConfig deviceConfig) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.OPERATE_GROUP_MEMBERS));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATE_TYPE, CloudConstant.ParameterValue.IS_DEL_MEMBER));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_ID, null));
        return nvps;
    }


    /**
     * 删除单节点
     *
     * @param obox         选中obox
     * @param deviceConfig 选中节点
     */
    public static List<NameValuePair> onDelNode(Obox obox, DeviceConfig deviceConfig) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DELETE_SINGLE_DEVICE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_ID, deviceConfig.getName()));
        return nvps;
    }

    /**
     * 给节点进行重命名
     *
     * @param obox         当前操作的obox
     * @param deviceConfig 当前操作设备
     * @param newID        新ID
     * @param isGroup      是否为组操作
     */
    public static List<NameValuePair> onRenameNode(Obox obox, DeviceConfig deviceConfig, String newID, boolean isGroup) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.UPDATE_NODE_NAME));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.NODE_TYPE, isGroup ? CloudConstant.NodeType.IS_GROUP : CloudConstant.NodeType.IS_SINGLE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.NEW_ID, newID));
        return nvps;
    }

    /**
     * 第一步
     * id与条件级别的情景操作
     *
     * @param obox       操作obox
     * @param action     动作， 包括 CREAT_SCENE DELETE_SCENE EXECUTE_SCENE RENAME_SCENE MODIFY_SCENE
     * @param cloudScene 新建或已经存在的scene
     */
    public static List<NameValuePair> onOpreaScene(Obox obox, String action, CloudScene cloudScene) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_SC_ID));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATE_TYPE, action));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_NUMBER, cloudScene.getScene_number()));
        /*modify没用，修改情景的是否执行状态也是用rename*/
        if (action.equals(CloudConstant.ParameterValue.CREAT_SCENE) || action.equals(CloudConstant.ParameterValue.RENAME_SCENE)) {
            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION_TYPE, cloudScene.getScene_status()));
            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_ID, cloudScene.getScene_name()));
        }

        return nvps;
    }


    /**
     * 第二步
     * 设置场景序号的条件信息
     *
     * @param obox       选中的obox
     * @param action     执行动作，添加  删除   修改
     * @param cloudScene 当前情景
     */
    public static List<NameValuePair> settingScCondition(Obox obox, String action, CloudScene cloudScene) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_SC_CONDITION));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATE_TYPE, action));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_NUMBER, cloudScene.getScene_number()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_TYPE, cloudScene.getScene_type()));
        //删除和改变为非传感器场景则不需要传condition相关参数,即只要是删除，或者情景模式不是传感器，都不传此参数
//        if (!(action.equals(CloudConstant.DEL_CONDITION)||!cloudScene.getScene_type().equals(CloudConstant.SENSOR_SCENE))) {
//            nvps.add(new BasicNameValuePair(CloudConstant.CONDITION_ID,cloudScene.getCondition_id()));
//            nvps.add(new BasicNameValuePair(CloudConstant.CONDITION,cloudScene.getConditions()));
//        }
        if (action.equals(CloudConstant.ParameterValue.CREAT_CONDITION) || action.equals(CloudConstant.ParameterValue.UPDATE_CONDITION)) {
            // FIXME: 2016/10/12 待修复，暂时设置为取第一个condition的条件和id
            if (cloudScene.getScene_type().equals(CloudConstant.ParameterValue.TIM_SCENE)) {
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CONDITION, cloudScene.getConditions().get(0).get(0).getCondition()));
            } else if (cloudScene.getScene_type().equals(CloudConstant.ParameterValue.SENSOR_SCENE)) {
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CONDITION, cloudScene.getConditions().get(0).get(0).getCondition()));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CONDITION_ID, cloudScene.getConditions().get(0).get(0).getConditionID()));
            }
        }
        return nvps;
    }

    /**
     * 第三步
     * 设置场景序号的行为节点
     *
     * @param obox       当前选中的obox
     * @param action     操作类型 添加 删除
     * @param cloudScene 情景
     * @param actions    情景行为节点
     */
    public static List<NameValuePair> onOpretaScAction(Obox obox, String action, CloudScene cloudScene, Action actions) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_SC_ACTION));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_NUMBER, cloudScene.getScene_number()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATE_TYPE, action));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION_ID, actions.getSerialId()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, actions.getAction() + "0000"));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.NODE_TYPE, actions.getNode_type()));
        return nvps;
    }

    /**
     * 查询obox中的所有情景   此方法查询后 ， 返回obox中情景的映射Map，要正常显示，则要进行转换到List处理
     */
    public static List<NameValuePair> onQueryScenes() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_SCENES));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        return nvps;
    }

    /**
     * 改变obox的id
     *
     * @param obox     当前obox
     * @param oboxName obox新id
     */
    public static List<NameValuePair> onChangeOboxId(Obox obox, String oboxName) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.UPDATE_OBOX_NAME));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_NEW_NAME, oboxName));
        return nvps;
    }

    /**
     * 改变obox的控制密码
     *
     * @param obox    当前obox
     * @param oboxPsw obox新密码
     */
    public static List<NameValuePair> onChangeOboxPsw(Obox obox, String oboxPsw) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.UPDATE_OBOX_PASSWORD));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_OLD_PWD, obox.getObox_pwd()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_NEW_PWD, oboxPsw));
        return nvps;
    }

    /**
     * 重置obox的控制密码
     *
     * @param obox 当前obox
     */
    public static List<NameValuePair> onResetOboxPsw(Obox obox) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.RESET_OBOXPWD));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox.getObox_serial_id()));
        return nvps;
    }

    /**
     * 查询所有升级信息
     */
    public static List<NameValuePair> onQueryUpgrades() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_UPGRADES));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.QUERY_TYPE, "01"));
        return nvps;
    }

    /**
     * 设置升级
     */
    public static List<NameValuePair> onSetUpgrades(String type, String serialId, String request) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_UPGRADES));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.QUERY_TYPE, type));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SERIALID, serialId));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.REQUEST, serialId));
        return nvps;
    }

    /**
     * 查询OBOX信息
     */
    public static List<NameValuePair> onQueryObox() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_OBOX));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        //  nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADMIN_NAME, "OB"));
        return nvps;
    }

    /**
     * 扫描新节点设备
     */
    public static List<NameValuePair> onSearchNewDevices(String obox_serial_id, String state, String timeout) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SEARCH_NEW_DEVICES));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox_serial_id));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.STATES, state));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TIME_OUT, timeout));
        return nvps;
    }

    /**
     * 获取新节点设备
     */
    public static List<NameValuePair> onGetNewDevices(String obox_serial_id) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.GET_NEW_DEVICES));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox_serial_id));
        return nvps;
    }

    /**
     * 查询OBOX配置信息
     */
    public static List<NameValuePair> onQueryOboxConfig(String obox_serial_id) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_OBOX_CONFIG));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox_serial_id));
        return nvps;
    }

    /**
     * 获取节点状态信息
     */
    public static List<NameValuePair> getNodeStatus(String jsonArray) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.GET_STATUS));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.NODES, jsonArray));
        return nvps;
    }

    /**
     * 批量命名
     */
    public static List<NameValuePair> OnMultiRename(String name, String jsonArray) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.MULTI_RENAME));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.NAME, name));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICES, jsonArray));
        return nvps;
    }

    /**
     * 释放单个obox中的设备
     */
    public static List<NameValuePair> onReleaseDevice(String obx_serial_id) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.RELEASE_ALL_DEVICES));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obx_serial_id));
        return nvps;
    }

    /**
     * 查询设备
     *
     * @param user  暂时是查询自身所有设备
     * @param index 其实index
     * @param count 长度，为0则全部查询
     */
    public static List<NameValuePair> onQueryDevice(User user, int index, int count) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_DEVICE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        // FIXME: 2016/8/12 访客模式不传这些参数
//        if (user.getAdminName()!=null) {
//            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADMIN_NAME, user.getAdminName()));
//        }
//
//        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GUEST_NAME, user.getGuestName()));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.START_INDEX, String.valueOf(index)));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.COUNT, String.valueOf(count)));
        return nvps;
    }

    /**
     * 此方法用于删除房间用
     *
     * @param building 建筑名称
     * @param room     房间名称
     * @param location 位置序号
     */
    public static List<NameValuePair> onDelPosition(String building, String room, String location) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.CREATE_LOCATION));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.BUILDING, building));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ROOM, room));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.LOCATION, location));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, "00"));
        return nvps;
    }

    /**
     * 查询位置信息
     *
     * @param guestName 如果是admin或者root就可以传入访客名字查询其位置信息
     * @param adminName 如果是root就可以传入管理者名字查询其位置信息
     */
    public static List<NameValuePair> onQueryLocation(String guestName, String adminName, String type) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_LOCATION));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        switch (type) {
            case CloudConstant.CloudDitalMode.GUEST:
                if (User.getUser().getWeight().equals(CloudConstant.CloudDitalMode.ROOT)
                        || User.getUser().getWeight().equals(CloudConstant.CloudDitalMode.ADMIN)) {
                    nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GUEST_NAME, guestName));
                }
                break;
            case CloudConstant.CloudDitalMode.ADMIN:
                if (User.getUser().getWeight().equals(CloudConstant.CloudDitalMode.ROOT)) {
                    nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADMIN_NAME, adminName));
                }
                break;

        }
        return nvps;
    }


    /**
     * 查询节点的历史记录
     *
     * @param device_serial_id 目标节点的序列号
     * @param type             {@link CloudConstant.ParameterValue#ONE_DAY,CloudConstant.ParameterValue#SOME_DAY}
     * @param from_date        查询当天则为确切的时间 ，如果是查询时间段则是起始日期
     * @param to_date          someday的时候要传，结束时间
     * @param start_index      查询oneday的时候要传
     * @param count            查询oneday的时候要传
     */
    public static List<NameValuePair> onQueryNodeHistory(String device_serial_id, String type, String from_date, String to_date,
                                                         String start_index, String count) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_NODE_HISTORY));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, device_serial_id));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, type));
        switch (type) {
            case CloudConstant.ParameterValue.ONE_DAY:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.FROM_DATE, from_date));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TO_DATE, to_date));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.START_INDEX, start_index));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.COUNT, count));
                break;
            case CloudConstant.ParameterValue.SOME_DAY:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.FROM_DATE, from_date));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TO_DATE, to_date));
                // nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TO_DATE, to_date));
                break;
            case CloudConstant.ParameterValue.WEEKLY_DAY:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.FROM_DATE, from_date));
                // nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TO_DATE, to_date));
                break;
        }
        return nvps;
    }

    public static List<NameValuePair> onQueryDeviceStatusHistory(String device_serial_id, String type, String from_date, String to_date,
                                                         String start_index, String count) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_DEVICE_STATUS_HISTORY));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, device_serial_id));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, type));
       if(type.equals("00")) {//个数为单位
           nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.START, start_index));
           nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.COUNT, count));
       } else if (type.equals("01")) {//小时为单位
           nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.FROM_DATA, from_date));
           nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TO_DATA, to_date));
       } else if (type.equals("02")) {//天为单位
           nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.FROM_DATA, from_date));
           nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TO_DATA, to_date));
       }
        return nvps;
    }

    /**
     * 设置组
     */

    public static List<NameValuePair> onSetGroup(String group_id, String group_name, String group_state,
                                                 String operate_type, String group_member, String group_style, String obox_serialid,String groupAddr) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SET_GROUP));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATE_TYPE, operate_type));
        switch (operate_type) {
            case CloudConstant.ParameterValue.DELETE_GROUP:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_ID, group_id));
                break;
            case CloudConstant.ParameterValue.SETTING_GROUP:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_STYLE, group_style));
                if (group_style.equals("00")) {
                    nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox_serialid));
                }
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_NAME, group_name));
                break;
            case CloudConstant.ParameterValue.COVER_MEMBER:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_MEMBER, group_member));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_ID, group_id));
                break;
            case CloudConstant.ParameterValue.ADD_MEMBER:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_MEMBER, group_member));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_ID, group_id));
                break;
            case CloudConstant.ParameterValue.DELETE_MEMBER:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_MEMBER, group_member));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_ID, group_id));
                break;
            case CloudConstant.ParameterValue.RE_NAME:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_NAME, group_name));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_ID, group_id));
                break;
            case CloudConstant.ParameterValue.EXUTE_GROUP:
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_ID, group_id));
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUP_STATE,
                        group_state.substring(0, 12) + "02"));
                if (group_style.equals("00")) {
                    nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.GROUPADDR, groupAddr));
                }
                break;
        }
        return nvps;
    }

    /**
     * 请求位置的节点
     *
     * @param location 位置序号
     */
    public static List<NameValuePair> onQueryDeviceLocation(String location, String room) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_DEVICE_LOCATION));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.LOCATION, location));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ROOM, room));
        return nvps;
    }

    /**
     * 修改节点
     *
     * @param serialId 序号
     */
    public static List<NameValuePair> onModifyDevice(String serialId, String name, boolean isDelete) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.MODIFY_DEVICE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SERIALID, serialId));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATE_TYPE, isDelete ? "00" : "01"));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.NAME, name));
        return nvps;
    }



    /**
     * 查询位置内绑定的情景
     *
     * @param location 位置序号
     */
    public static List<NameValuePair> onQueryLocationScene(String location) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_SCENE_LOCATION));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.LOCATION, location));
        return nvps;
    }

    /**
     * 查询组信息
     */
    public static List<NameValuePair> onQueryGroups() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_GROUP));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        return nvps;
    }

    /**
     * 绑定情景到位置
     *
     * @param location 位置序号
     * @param sceneNum 情景序号
     * @param action   01 绑定 00 解绑
     */
    public static List<NameValuePair> onSetLocationScene(String location, String sceneNum, String action) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SET_SCENE_LOCATION));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.LOCATION, location));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_NUMBER, sceneNum));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, action));
        return nvps;
    }

    /**
     * @param sceneNum 情景序号 ,暂时写定为执行
     * @param type 00| 01|02|03   Disable|Enable|Action|Delete,如果是条件场景，则传这个参数
     */
    public static List<NameValuePair> onExecuteLocationScene(String sceneNum, String type) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.EXECUTE_SC));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_NUMBER, sceneNum));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE_STATUS, type));
        return nvps;
    }

    /**
     * 绑定或者解除绑定电话号码
     *
     * @param phoneNum 电话号码
     * @param isBind   是否绑定，绑定传true，解除绑定传false
     */
    public static List<NameValuePair> onBindPhone(String phoneNum, boolean isBind) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.BIND_PHONE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PHONE, phoneNum));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, isBind ? "01" : "00"));
        return nvps;
    }

    /**
     * 设置场景信息
     */
    public static List<NameValuePair> onSetScInfo(CloudScene curentScene) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_SC_INFO));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        Gson gs = new Gson();
        String gson = gs.toJson(curentScene);
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SCENE, gson));
        return nvps;
    }

    /**
     * 查询遥控器
     */
    public static List<NameValuePair> onQueryRemote() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_REMOTE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        return nvps;
    }

    /**
     * 查询遥控器可用通道
     */
    public static List<NameValuePair> onQrRmtChanl(String remoteSer) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_REMOTER_CHANNEL));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.REMOTER, remoteSer));
        return nvps;
    }

    public static List<NameValuePair> onDetectRemote() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DETECT_REMOTE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        return nvps;
    }

    public static List<NameValuePair> onQueryAccessToken() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_YS_ACCESS_TOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        return nvps;
    }

    public static List<NameValuePair> onCreateCamera(String action, String deviceSerial, String validateCode) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.CREATE_YS_CAMERA));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, action));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL, deviceSerial));
        if (action.equals("01")) {
            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.VALIDATE_CODE, validateCode));
        }
        return nvps;
    }

    public static List<NameValuePair> onBindUser(String action, String phone) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.BIND_YS_USER));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, action));
        if (action.equals("01")) {
            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PHONE, phone));
        }
        return nvps;
    }

    public static List<NameValuePair> onQueryCapture(String deviceSerial) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_CAMERA_CAPTURE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICE_SERIAL, deviceSerial));
        return nvps;
    }

    /**
     * 删除obox
     *
     * @param force   是否强制删除，不建议使用强制删除
     * @param oboxSer 要删除的obox的序列号
     */
    public static List<NameValuePair> onDeleteObox(boolean force, String oboxSer) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DELETE_OBOX));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OBOX_SERIAL_ID, oboxSer));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.FORCE_DELETE, force ? "01" : "00"));
        return nvps;
    }

    /**
     * 找回密码
     *
     * @param phone        手机号码
     * @param code         验证码
     * @param countryPhone 国家电话号码
     */
    public static List<NameValuePair> onFindPwd(String phone, String code, String countryPhone, boolean isOther) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.FINDPWD));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PHONE, phone));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.APP_KEY, isOther ? "1c7cd49c673f6" : "16eff033f92d9"));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ZONE, countryPhone));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CODE, code));
        return nvps;
    }

    /**
     * 添加指纹机
     *
     * @param action        解绑／绑定
     * @param serialId      设备序列号
     */
    public static List<NameValuePair> addFingerPrint(String action, String serialId) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.ADD_FINGERPRINT));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, action));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SERIALID, serialId));
        return nvps;
    }

    /**
     * 查询指纹机
     *
     */
    public static List<NameValuePair> queryFingerPrint() {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_FINGERPRINT));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        return nvps;
    }

    /**
     * 查询智能门锁首页
     *
     */
    public static List<NameValuePair> querySmartLockHome(String serialId) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_FINGER_HOME));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SERIALID, serialId));
        return nvps;
    }

    /**
     * 查询指纹机下用户
     *
     * @param start        起始
     * @param serialId     设备序列号
     * @param count        数量
     * @param type        00/01	  指纹门禁机／门锁
     */
    public static List<NameValuePair> queryFingerPrintUser(String serialId,String start,String count,String type) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_FINGERPRINT_USER));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SERIALID, serialId));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.START, start));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.COUNT, count));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, type));
        return nvps;
    }

    /**
     * 查询指纹机下用户
     *
     * @param start        起始
     * @param serialId     设备序列号
     * @param count        数量
     * @param type        00 中控  01 门禁
     * @param operation   00/01/02/03/04	开门/删除用户/添加用户/修改密码/修改指纹
     */
    public static List<NameValuePair> queryFingerLog(String serialId,String start,String count,String type,String operation) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_FINGERPRINT_LOG));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SERIALID, serialId));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.START, start));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.COUNT, count));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, type));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.OPERATION, operation));
        return nvps;
    }

    /**
     * 绑定用户指纹
     *
     * @param user_name        需要分配的属下用户
     * @param serialId         设备序列号
     * @param action           解绑／绑定
     * @param pin              指纹机上的编号
     * @param type             00 指纹机 01 门锁
     * @param cover       0/1	不覆盖/覆盖
     */
    public static List<NameValuePair> addUserFinger(String user_name,String action,String type,String serialId,String pin,String cover) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.ADD_USER_FINGERPRINT));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.USERNAME, user_name));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, action));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, type));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.SERIALID, serialId));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PIN, pin));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.COVER, cover));
        return nvps;
    }

    /**
     * 修改子用户
     *
     * @param root_name        超级管理员名字  创建时不用传，weight 是2/3时才需要传
     * @param admin_name       管理员名字  创建时不用传，weight是3时才需要传
     * @param weight           01/02/03 root/admin/guest
     * @param user_name        用户名
     * @param pwd              密码  删除不用传
     * @param modify_type      00/01 删除/创建
     */
    public static List<NameValuePair> modifyUser(String root_name,String admin_name,String weight,String user_name,String pwd,String modify_type) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.MODIFY_USER));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));

        if (modify_type.equals("01")) {
            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.PASS_WORD, pwd));
            if (weight.equals("03")) {
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADMIN_NAME, admin_name));
            }
        } else if (modify_type.equals("00")) {
           if( weight.equals("02"))  {
               nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ROOTNAME, root_name));
               nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADMIN_NAME, admin_name));
           } else if (weight.equals("03")) {
                nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADMIN_NAME, admin_name));
            }
        }
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.USERNAME, user_name));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.WEIGHT, weight));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.MODIFY_TYPE, modify_type));
        return nvps;
    }

    /**
     * 设备权限分配
     *
     * @param action          00/01	add/delete
     * @param jsonArray       序列号数组	obox/节点/遥控器
     * @param user_name       需要分配的用户
     * @param admin_name      如果需要分配的用户是guest且分配人是root，需要传该参数
     * @param weight          02/03	   Admin/guset,需要分配的用户的权限
     */
    public static List<NameValuePair> bindDevice(String action,String jsonArray,String user_name,String admin_name,String weight) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.BIND_DEVICE));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACTION, action));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.DEVICES, jsonArray));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.USERNAME, user_name));
        if (weight.equals("01")) {
            nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ADMIN_NAME, admin_name));
        }
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.WEIGHT, weight));
        return nvps;
    }

    /**
     * 查询用户下的子用户
     *
     * @param type        01/02/03/04	root/admin/guest/both,
     */
    public static List<NameValuePair> queryUser(String type) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_USER));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.ACCESS_TOKEN, ACCESSTOKEN));
        nvps.add(new BasicNameValuePair(CloudConstant.ParameterKey.TYPE, type));
        return nvps;
    }
}
