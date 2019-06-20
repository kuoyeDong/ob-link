package com.onbright.oblink.cloud.helper;

import android.util.Log;

import com.onbright.oblink.StringUtil;
import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.bean.Groups;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.Obox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 服务器模式节点分组管理的类，提供包括创建组，删除组，把节点添加到组、从组内移除节点、节点重命名、组重命名等功能。
 * <p>
 * 使用方法:
 * 如果要创建仅基于服务器的组，请使用{@link #creatGroup(String)},成功则回调{@link #onCreatGroupSuc(Groups)}。
 * <p>
 * 如果创建基于服务器并下发到obox的组，请使用{@link #creatGroup(String, Obox)},成功则回调{@link #onCreatGroupSuc(Groups)}。
 * <p>
 * 如果要修改组名称，请使用{@link #renameGroup(Groups, String)},成功则回调{@link #onRenameGroupSuc()}。
 * <p>
 * 如果要修改节点名称，请使用{@link #renameDevice(DeviceConfig, String)},成功则回调{@link #onReNameDeviceSuc()}。
 * <p>
 * 需要注意的是：{@link #creatGroup(String)}{@link #creatGroup(String, Obox)}
 * {@link #renameGroup(Groups, String)}{@link #renameDevice(DeviceConfig, String)}
 * 传入的名称，不能包含特殊字符串和超过16个字符,否则不会触发请求并回调{@link #notLegitOption()}。
 * <p>
 * 如果要把节点添加到组，请使用{@link #addDeviceToGroup(DeviceConfig, Groups)},成功则回调{@link #onAddDeviceToGroupSuc()},
 * 需要注意的是，如果组为下发到obox的组，传入的节点必须和组属于同一个obox，否则不会触发请求并回调{@link #notLegitOption()}
 * <p>
 * 如果要把组内的某个节点删除，请使用{@link #removeDeviceFromGroup(DeviceConfig, Groups)},成功则回调{@link #onRemoveDeviceFromGroupSuc()}
 * <p>
 * 如果要删除组，请使用{@link #removeGroup(Groups)},成功则回调{@link #onRemoveGroupSuc()}。
 * <p>
 * 另外，所有的操作失败返回都会回调{@link #operationFailed(String, String)}
 */

public abstract class SOrganizationHelper implements HttpRespond {

    private static final String TAG = "SOrganizationHelper";
    private String opretype;

    /**
     * 创建基于服务器的组
     *
     * @param groupId 要设置的组id
     */
    public void creatGroup(String groupId) {
        opretype = CloudConstant.ParameterValue.SETTING_GROUP;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SET_GROUP,
                GetParameter.onSetGroup("", groupId, "", "01", "", "01", null, ""));
    }

    /**
     * 创建基于obox的组，即此组会通过服务器下发到obox本机配置中
     *
     * @param groupId 要设置的组名称,不能包含特殊字符串和超过16个字符，否则执行{@link #notLegitOption()}而不会进行网络交互
     * @param obox    要创建到组的那个obox，
     */
    public void creatGroup(String groupId, Obox obox) {
        if (!StringUtil.isLegit(groupId, 0, 16)) {
            notLegitOption();
            return;
        }
        opretype = CloudConstant.ParameterValue.SETTING_GROUP;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SET_GROUP,
                GetParameter.onSetGroup("", groupId, "", "01", "", "00", obox.getObox_serial_id(), ""));
    }

    /**
     * 删除组
     *
     * @param groups 要删除的组
     */
    public void removeGroup(Groups groups) {
        opretype = CloudConstant.ParameterValue.DELETE_GROUP;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SET_GROUP, GetParameter.onSetGroup(
                groups.getGroup_id(), groups.getGroup_name(), "", opretype, "", groups.getGroup_style(), groups.getObox_serial_num(), groups.getGroupAddr()
        ));
    }

    /**
     * 不符规则的操作，比如在下发到obox的本地组添加不属于此obox的节点，
     * 重命名或者创建到本地组不符合规范的回调
     */
    public abstract void notLegitOption();

    /**
     * 添加节点到目标组，需要注意的是，
     * 当目标组是基于obox创建的，则要添加的目标节点obox序列号必须与组obox序列号相同，即二者同属一个obox,
     * 当目标组不是基于obox创建的,则可任意操作
     * 函数会自动判断，当不符合条件时，不会进行任何网络交互，并回调{@link #notLegitOption()}
     *
     * @param deviceConfig 要添加的目标节点
     * @param groups       要添加到的目标组
     * @see DeviceConfig#getObox_serial_id(),Groups#getObox_serial_num()
     */
    public void addDeviceToGroup(DeviceConfig deviceConfig, Groups groups) {
        if (groups.getGroup_style().equals("00") && (!groups.getObox_serial_num().equals(deviceConfig.getObox_serial_id()))) {
            notLegitOption();
        } else {
            opretype = CloudConstant.ParameterValue.ADD_MEMBER;
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(deviceConfig.getSerialId());
            HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SET_GROUP,
                    GetParameter.onSetGroup(groups.getGroup_id(), groups.getGroup_name(), "", opretype,
                            jsonArray.toString(), groups.getGroup_style(), groups.getObox_serial_num(), groups.getGroupAddr()));
        }
    }


    /**
     * 从组内删除节点
     *
     * @param deviceConfig 必须是存在于组内的节点{@link Groups#getGroup_member()}
     * @param groups       目标组
     */
    public void removeDeviceFromGroup(DeviceConfig deviceConfig, Groups groups) {
        opretype = CloudConstant.ParameterValue.DELETE_MEMBER;
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(deviceConfig.getSerialId());
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SET_GROUP, GetParameter.onSetGroup(groups.getGroup_id(),
                groups.getGroup_name(), groups.getGroup_state(), opretype, jsonArray.toString(), groups.getGroup_style(), groups.getObox_serial_num(), groups.getGroupAddr()));
    }

    /**
     * 修改节点名称
     *
     * @param deviceConfig 目标节点
     * @param newName      新名称，不得超过16个字节并且不能包含特殊字符串，函数会自动判断，当不符合条件时，
     *                     不会进行任何网络交互，并回调{@link #notLegitOption()}
     */
    public void renameDevice(DeviceConfig deviceConfig, String newName) {
        if (!StringUtil.isLegit(newName, 0, 16)) {
            notLegitOption();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.MODIFY_DEVICE,
                GetParameter.onModifyDevice(deviceConfig.getSerialId(), newName, false));
    }

    /**
     * 修改组名称
     *
     * @param groups  目标组
     * @param newName 新名称，当组是基于本地的组时，不得超过16个字节并且不能包含特殊字符串，函数会自动判断，
     *                当不符合条件时，不会进行任何网络交互，并回调{@link #notLegitOption()}
     */
    public void renameGroup(Groups groups, String newName) {
        if (groups.getGroup_style().equals("00") && !StringUtil.isLegit(newName, 0, 16)) {
            notLegitOption();
            return;
        }
        opretype = CloudConstant.ParameterValue.RE_NAME;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SET_GROUP, GetParameter.onSetGroup(
                groups.getGroup_id(), newName, groups.getGroup_state(), opretype, null,
                groups.getGroup_style(), groups.getObox_serial_num(), groups.getGroupAddr()
        ));
    }

    @Override
    public void onRequest(String action) {
        Log.d(TAG, "onRequest: " + action);
    }

    @Override
    public void onSuccess(String action, String json) {
        Log.d(TAG, "onSuccess: >>" + action);
        switch (action) {
            case CloudConstant.CmdValue.SET_GROUP:
                switch (opretype) {
                    case CloudConstant.ParameterValue.SETTING_GROUP:
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(json);
                            JSONObject groupsObject = jsonObject.getJSONObject("groups");
                            String group_id = groupsObject.optString("group_id");
                            String group_name = groupsObject.optString("group_name");
                            String group_type = groupsObject.optString("group_type");
                            String group_state = groupsObject.optString("group_state");
                            String group_child_type = groupsObject.optString("group_child_type");
                            String group_style = groupsObject.optString("group_style");
                            String obox_serial_id = groupsObject.optString("obox_serial_id");
                            String group_addr = groupsObject.optString("groupAddr");
                            Groups groups = new Groups(new ArrayList<DeviceConfig>(), group_id, group_name, group_type, group_state, group_child_type, group_style, obox_serial_id, group_addr);
                            groups.setGroupAddr(group_addr);
                            groups.setObox_serial_id(obox_serial_id);
                            onCreatGroupSuc(groups);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case CloudConstant.ParameterValue.DELETE_GROUP:
                        onRemoveGroupSuc();
                        break;
                    case CloudConstant.ParameterValue.ADD_MEMBER:
                        onAddDeviceToGroupSuc();
                        break;
                    case CloudConstant.ParameterValue.DELETE_MEMBER:
                        onRemoveDeviceFromGroupSuc();
                        break;
                    case CloudConstant.ParameterValue.RE_NAME:
                        onRenameGroupSuc();
                        break;
                }
                break;
            case CloudConstant.CmdValue.MODIFY_DEVICE:
                onReNameDeviceSuc();
                break;
        }
    }

    /**
     * 重命名节点成功回调
     */
    public abstract void onReNameDeviceSuc();

    /**
     * 重命名组成功回调
     */
    public abstract void onRenameGroupSuc();

    /**
     * 从组内删除节点成功回调
     */
    public abstract void onRemoveDeviceFromGroupSuc();

    /**
     * 添加节点到组成功回调
     */
    public abstract void onAddDeviceToGroupSuc();

    /**
     * 删除组成功回调
     */
    public abstract void onRemoveGroupSuc();

    /**
     * 创建组成功回调
     *
     * @param groups 新创建的组
     */
    public abstract void onCreatGroupSuc(Groups groups);

    @Override
    public void onFaild(String action, Exception e) {
        Log.d(TAG, "onFaild: >>" + action);
        onFailed(action);
    }

    /**
     * 与服务器通讯失败的回调
     *
     * @param action 通讯失败时候的动作
     */
    public abstract void onFailed(String action);

    @Override
    public void onFaild(String action, int state) {
        Log.d(TAG, "onFaild: >>" + action);
        onFailed(action);
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
