package com.onbright.oblink.cloud.net;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.CloudDataPool;
import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.bean.Group;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shifan_xiao on 2017/9/11.
 * 解析服务器长连接数据处理
 */

public class ParseServerListener {
    private Context context;
    private String dataPrefix = "";

    public ParseServerListener(Context context) {
        this.context = context;
    }

    public synchronized void parseServerData(String dataString) {
        String json = "";
        if (dataString.contains("STR") && dataString.contains("END")) {
            json = dataString;
        } else if (dataString.contains("STR") && !dataString.contains("END")) {
            dataPrefix = dataString;
            return;
        } else if (!dataString.contains("STR") && dataString.contains("END")) {
            json = dataPrefix + dataString;
        }
        json = json.substring(3, json.length() - 3);//去掉STR，END
        String type = CloudParseUtil.getJsonParm(json, "type");
        switch (type) {
            case "0":
                oboxRespon(json);
                break;
            case "1":
                httpRespon(json);
                break;
            case "2":
                oboxHeart(json);
                break;
            case "4":
                wifiHeart(json);
                break;
            case "99":
                lockAdminPwdReset(json);
                break;
            case "20":
                learnRemote(json);
                break;
            case "21":
                pairIrRemote(json);
                break;
            case "22":
                downIrRemote(json);
                break;
        }
    }

    /**
     * 单品wifi上下线推送
     *
     * @param json 收到的推送数据
     */
    private void wifiHeart(String json) {
        String onLine;
        String serialId = CloudParseUtil.getJsonParm(json, "serialId");
        onLine = CloudParseUtil.getJsonParm(json, "onLine");
        Intent intent = new Intent();
        intent.setAction(OBConstant.StringKey.WIFI_HEART_INFO);
        intent.putExtra("serialId", serialId);
        intent.putExtra("onLine", onLine);
        context.sendBroadcast(intent);
    }

    /**
     * @param json 待处理的推送数据
     */
    private void downIrRemote(String json) {
        Intent intent = new Intent();
        intent.putExtra(OBConstant.StringKey.DOWN_IR_REMOTE, json);
        intent.setAction(OBConstant.StringKey.DOWN_IR_REMOTE);
        context.sendBroadcast(intent);
    }

    /**
     * MQTT返回一键匹配测试码库结果
     *
     * @param json 待处理的推送数据
     */
    private void pairIrRemote(String json) {
        Intent intent = new Intent();
        intent.putExtra(OBConstant.StringKey.PAIR_IR_REMOTE, json);
        intent.setAction(OBConstant.StringKey.PAIR_IR_REMOTE);
        context.sendBroadcast(intent);
    }

    /**
     * MQTT返回按键学习结果
     *
     * @param json 待处理的推送数据
     */
    private void learnRemote(String json) {
        Intent intent = new Intent();
        intent.putExtra(OBConstant.StringKey.LEARN_REMOTE, json);
        intent.setAction(OBConstant.StringKey.LEARN_REMOTE);
        context.sendBroadcast(intent);
    }

    /**
     * 门锁重置密码
     */
    private void lockAdminPwdReset(String json) {
        String serialId = CloudParseUtil.getJsonParm(json, "serialId");
        String data = CloudParseUtil.getJsonParm(json, "data");
        Intent intent = new Intent();
        intent.putExtra("code", data);
        intent.putExtra("serial_id", serialId);
        intent.setAction(OBConstant.StringKey.LOCK_ADMIN_PWD_RESET);
        context.sendBroadcast(intent);
    }

    /**
     * obox回复透传
     *
     * @param json 返回数据
     */
    private void oboxRespon(String json) {
        String cmd = CloudParseUtil.getJsonParm(json, "cmd");
        String data = CloudParseUtil.getJsonParm(json, "data");
        String obox_serial_id = CloudParseUtil.getJsonParm(json, "serialId");
        String state;
        String nodeType;
        String nodeAddr;
        String groupAddr;
        String deviceType;
        String deviceChildType;
        String serialId = "";
        String deviceName;
        boolean isDeleteGroup;
        boolean isDeleteNode;
        String isSuccess;
        switch (cmd) {
            case "2500": {/*传感器上报数据*/
                state = data.substring(16, 30);
                nodeType = data.substring(10, 12);
                nodeAddr = data.substring(12, 14);
                Logger.d("2500设备上报数据" + "state =" + state + "nodeType =" + nodeType + "nodeAddr =" + nodeAddr + "state", state);
                String status;
                int pType, cType;
                for (int i = 0; i < CloudDataPool.getDevices().size(); i++) {
                    if (CloudDataPool.getDevices().get(i).getObox_serial_id().equals(obox_serial_id)) {
                        if (CloudDataPool.getDevices().get(i).getAddr().equals(nodeAddr)) {
                            serialId = CloudDataPool.getDevices().get(i).getSerialId();
                            pType = Integer.parseInt(CloudDataPool.getDevices().get(i).getDevice_type(), 16);
                            cType = Integer.parseInt(CloudDataPool.getDevices().get(i).getDevice_child_type(), 16);
                            switch (pType) {
                                case OBConstant.NodeType.IS_SENSOR:
                                    switch (cType) {
                                        case OBConstant.NodeType.ENVROMENT_SENSOR:
                                            status = state;
                                            CloudDataPool.getDevices().get(i).setState(status);
                                            return;
                                        default:
                                            status = state;
                                            break;
                                    }
                                    break;
                                case OBConstant.NodeType.IS_OBSOCKET:
                                    switch (cType) {
                                        case OBConstant.NodeType.CHAZUO:
                                        case OBConstant.NodeType.SINGLE_TOUCH_SWITCH:
                                        case OBConstant.NodeType.DOUBLE_TOUCH_SWITCH:
                                        case OBConstant.NodeType.THREE_TOUCH_SWITCH:
                                        case OBConstant.NodeType.ONE_BUTTON_WIRE_SOCKET:
                                        case OBConstant.NodeType.TWO_BUTTON_WIRE_SOCKET:
                                            // 0,1互换位置按键开关位置
                                            status = state.substring(2, 4) + state.substring(0, 2) + state.substring(4, 14);
                                            break;
                                        case OBConstant.NodeType.SINGLE_SWITCH_SCENE_PANEL:
                                        case OBConstant.NodeType.DOUBLE_SWITCH_SCENE_PANEL:
                                        case OBConstant.NodeType.THREE_SWITCH_SCENE_PANEL:
                                        case OBConstant.NodeType.THREE_SWITCH_RED_SCENE_PANEL:
                                            // 0,1互换情景开关位置，3,1互换按键开关位置
                                            status = state.substring(2, 4) + state.substring(6, 8) + state.substring(4, 6) + state.substring(0, 2) + state.substring(8, 14);
                                            break;
                                        default:
                                            status = state;
                                            break;
                                    }
                                    break;
                                default:
                                    status = state;
                                    break;
                            }
                            CloudDataPool.getDevices().get(i).setState(status);
                            break;
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("serialId", serialId);
                intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD_2500);
                context.sendBroadcast(intent);
                break;
            }
            case "a100": {/*设置状态回复*/
                state = data.substring(16, 30);
                nodeAddr = data.substring(14, 16);
                groupAddr = data.substring(12, 14);
                if (groupAddr.equals("00")) {
                    for (int i = 0; i < CloudDataPool.getDevices().size(); i++) {
                        if (CloudDataPool.getDevices().get(i).getObox_serial_id() != null) {
                            if (CloudDataPool.getDevices().get(i).getObox_serial_id().equals(obox_serial_id)) {
                                if (CloudDataPool.getDevices().get(i).getAddr().equals(nodeAddr)) {
                                    Device device = CloudDataPool.getDevices().get(i);
                                    // FIXME: 2017/11/26 飞机不进行广播更新，太频繁
                                    if (device.getDevice_type().equals("10")) {
                                        state = data.substring(16, 36);
                                        CloudDataPool.getDevices().get(i).setState(state);
                                        serialId = CloudDataPool.getDevices().get(i).getSerialId();
                                        Intent intent = new Intent();
                                        intent.putExtra("serialId", serialId);
                                        intent.setAction(OBConstant.StringKey.UPDATE_FLIGHT_CLOUD);
                                        context.sendBroadcast(intent);
                                        return;
                                    }
                                    CloudDataPool.getDevices().get(i).setState(state);
                                    serialId = CloudDataPool.getDevices().get(i).getSerialId();
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                        if (CloudDataPool.getGroups().get(i).getObox_serial_id() != null) {
                            if (CloudDataPool.getGroups().get(i).getObox_serial_id().equals(obox_serial_id)) {
                                if (CloudDataPool.getGroups().get(i).getGroupAddr().equals(groupAddr)) {
                                    Group group = CloudDataPool.getGroups().get(i);
                                    // FIXME: 2017/11/26 飞机不进行广播更新，太频繁
                                    if (group.getGroup_type().equals("10")) {
                                        state = data.substring(16, 36);
                                        CloudDataPool.getGroups().get(i).setGroup_state(state);
                                        Intent intent = new Intent();
                                        intent.putExtra("serialId", serialId);
                                        intent.setAction(OBConstant.StringKey.UPDATE_FLIGHT_CLOUD);
                                        context.sendBroadcast(intent);
                                        return;
                                    }
                                    CloudDataPool.getGroups().get(i).setGroup_state(state);
                                }
                            }
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("serialId", serialId);
                intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
                context.sendBroadcast(intent);
                break;
            }
            case "a004":/*删除节点、删除组、创建组、重命名节点、组,因为服务器回复不靠谱所以在obox透传中做*/
                isSuccess = data.substring(0, 2);
                if (isSuccess.equals("00")) {
                    return;
                }
                String operation = data.substring(2, 4);
                groupAddr = data.substring(14, 16);
                nodeAddr = data.substring(16, 18);
                String idString = data.substring(18, 50);
                byte[] id = Transformation.hexString2Bytes(idString);
                String name = getNodeId(id);
                switch (operation) {
                    case "00": {//delete
                        if (groupAddr.equals("00")) {
                            for (int i = 0; i < CloudDataPool.getDevices().size(); i++) {
                                if (CloudDataPool.getDevices().get(i).getObox_serial_id().equals(obox_serial_id)) {
                                    if (CloudDataPool.getDevices().get(i).getAddr().equals(nodeAddr)) {
                                        CloudDataPool.getDevices().remove(i);
                                    }
                                }
                            }
                            isDeleteGroup = true;
                            isDeleteNode = false;
                        } else {
                            for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                                if (CloudDataPool.getGroups().get(i).getObox_serial_id() != null) {
                                    if (CloudDataPool.getGroups().get(i).getObox_serial_id().equals(obox_serial_id)) {
                                        if (CloudDataPool.getGroups().get(i).getGroupAddr().equals(groupAddr)) {
                                            CloudDataPool.getGroups().remove(i);
                                        }
                                    }
                                }
                            }
                            isDeleteNode = true;
                            isDeleteGroup = false;
                        }
                        Intent intent = new Intent();
                        intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
                        intent.putExtra("isDeleteGroup", isDeleteGroup);
                        intent.putExtra("isDeleteNode", isDeleteNode);
                        intent.putExtra("cmd", cmd);
                        intent.putExtra("isModifyStruct", true);
                        intent.putExtra("operate_type", operation);
                        context.sendBroadcast(intent);
                        Logger.d("删除节点或删除组" + "isDeleteNode=" + isDeleteNode + "isDeleteGroup= " + isDeleteGroup);
                        break;
                    }
                    case "01": {//add
                        Intent intent = new Intent();
                        intent.setAction(OBConstant.StringKey.UPDATE_GROUPS_INFO);
                        intent.putExtra("cmd", cmd);
                        intent.putExtra("operate_type", operation);
                        context.sendBroadcast(intent);
                        Logger.d("添加节点到组");
//                    if (groupAddr.equals("00")) {
//
//                    } else {
//                        Groups groups = new Groups();
//                        groups.setGroup_addr(groupAddr);
//                        groups.setObox_serial_id(obox_serial_id);
//                        groups.setGroup_name(name);
//                        List<Device> group_member = new ArrayList<>();
//                        for (int i = 0; i < CloudDataPool.getDevices().size(); i++) {
//                            if (CloudDataPool.getDevices().get(i).getObox_serial_id().equals(obox_serial_id)) {
//                                if (CloudDataPool.getDevices().get(i).getAddr().equals(nodeAddr) && CloudDataPool.getDevices().get(i).getGroupAddr().equals(groupAddr)) {
//                                    group_member.add(CloudDataPool.getDevices().get(i));
//                                }
//                            }
//                        }
//                        groups.setGroup_member(group_member);
//                        CloudDataPool.addGroups(groups);
//                    }

                        break;
                    }
                    case "02": {//rename
                        if (groupAddr.equals("00")) {
                            for (int i = 0; i < CloudDataPool.getDevices().size(); i++) {
                                if (CloudDataPool.getDevices().get(i).getObox_serial_id().equals(obox_serial_id)) {
                                    if (CloudDataPool.getDevices().get(i).getAddr().equals(nodeAddr)) {
                                        CloudDataPool.getDevices().get(i).setName(name);
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                                if (CloudDataPool.getGroups().get(i).getObox_serial_id() != null) {
                                    if (CloudDataPool.getGroups().get(i).getObox_serial_id().equals(obox_serial_id)) {
                                        if (CloudDataPool.getGroups().get(i).getGroupAddr().equals(groupAddr)) {
                                            CloudDataPool.getGroups().get(i).setGroup_name(name);
                                        }
                                    }
                                }
                            }
                        }
                        Intent intent = new Intent();
                        intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
                        intent.putExtra("operate_type", operation);
                        context.sendBroadcast(intent);
                        Logger.d("节点或组重命名");
                        break;
                    }
                }
                break;
            case "a006": {//update group node(02 add/ 01 delete)
                isSuccess = data.substring(0, 2);
                if (isSuccess.equals("00")) {
                    return;
                }
                groupAddr = data.substring(12, 14);
                nodeAddr = data.substring(14, 16);
                String operatype = data.substring(16, 18);
                List<Device> groupMember;
                if (operatype.equals("01")) {//delete
                    for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                        if (groupAddr.equals(CloudDataPool.getGroups().get(i).getGroupAddr()) && obox_serial_id.equals(CloudDataPool.getGroups().get(i).getObox_serial_id())) {
                            groupMember = CloudDataPool.getGroups().get(i).getGroup_member();
                            for (int j = 0; j < groupMember.size(); j++) {
                                if (groupMember.get(i).getAddr().equals(nodeAddr)) {
                                    groupMember.remove(i);
                                }
                            }
                        }
                    }
                } else if (operatype.equals("02")) {//add
                    for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                        if (groupAddr.equals(CloudDataPool.getGroups().get(i).getGroupAddr()) && obox_serial_id.equals(CloudDataPool.getGroups().get(i).getObox_serial_id())) {
                            groupMember = CloudDataPool.getGroups().get(i).getGroup_member();
                            for (int j = 0; j < CloudDataPool.getDevices().size(); j++) {
                                if (CloudDataPool.getDevices().get(j).getAddr().equals(nodeAddr)) {
                                    groupMember.add(CloudDataPool.getDevices().get(j));
                                }
                            }
                        }
                    }
                }
                Intent intent = new Intent();
                intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
                intent.putExtra("cmd", cmd);
                intent.putExtra("obox_serial_id", obox_serial_id);
                intent.putExtra("group_addr", groupAddr);
                intent.putExtra("isDeleteGroup", false);
                intent.putExtra("isDeleteNode", false);
                context.sendBroadcast(intent);
                Logger.d("修改组节点，组操作");
                break;
            }
            case "a008":  //release
                isSuccess = data.substring(0, 2);
                if (isSuccess.equals("00")) {
                    return;
                }
                CloudDataPool.deleteOboxData(obox_serial_id);
                sendBroadCast();
                break;
            case "2003": //scan
                isSuccess = data.substring(0, 2);
                if (isSuccess.equals("00")) {
                    return;
                }
                String deviceTypeString = data.substring(2, 4);
                String deviceChildTypeString = data.substring(4, 6);
                byte[] device = Transformation.hexString2Bytes(deviceTypeString);
                byte[] child = Transformation.hexString2Bytes(deviceChildTypeString);
                byte a = (byte) MathUtil.byteIndexValid(device[0], 0, 7);
                byte b = (byte) MathUtil.byteIndexValid(child[0], 0, 7);
                deviceType = Transformation.byte2HexString(a);
                deviceChildType = Transformation.byte2HexString(b);
                Logger.d("扫描设备" + "deviceType = " + deviceType + "deviceChildType =" + deviceChildType);
                deviceName = getNodeId(Transformation.hexString2Bytes(data.substring(6, 38)));
                serialId = data.substring(38, 48);
                obox_serial_id = data.substring(48, 58);
                groupAddr = data.substring(58, 60);
                nodeAddr = data.substring(60, 62);
                if (serialId.equals("0000000000")) {
                    return;
                }
                Device newDevice = new Device();
                newDevice.setName(deviceName);
                newDevice.setDevice_child_type(deviceChildType);
                newDevice.setDevice_type(deviceType);
                newDevice.setSerialId(serialId);
                newDevice.setObox_serial_id(obox_serial_id);
                newDevice.setAddr(nodeAddr);
                newDevice.setGroupAddr(groupAddr);
                newDevice.setState("00000000000000");
                List<Device> devices = CloudDataPool.getDevices();
                /*去除重复处理*/
                for (int i = 0; i < devices.size(); i++) {
                    Device deviceIndex = devices.get(i);
                    if (newDevice.getSerialId().equals(deviceIndex.getSerialId())) {
                        return;
                    }
                }
                devices.add(newDevice);
                Intent intent = new Intent();
                intent.setAction(OBConstant.StringKey.UPDATE_SCAN_INFO);
                intent.putExtra("cmd", cmd);
                intent.putExtra("device_name", deviceName);
                intent.putExtra("obox_serial_id", obox_serial_id);
                context.sendBroadcast(intent);
                break;
            case "a00e":/*时间有限，先做重复请求统一刷新处理，后改为即时处理数据*/
                intent = new Intent();
                intent.setAction(OBConstant.StringKey.UPDATE_SCENE_LOCAL_SETTING);
                context.sendBroadcast(intent);
                break;
        }
    }

    /**
     * http请求的推送处理，数据会包含request和respond
     *
     * @param json 推送数据
     */
    private void httpRespon(String json) {
        String cmd = CloudParseUtil.getJsonParm(json, "cmd");
        JSONObject dataObject;
        JSONObject requestObject;
        JSONObject groupsObject = null;
        String operate_type = "";
        String obox_serial_id = "";
        int location;
        String location_d = "";
        String x_axis = "";
        String y_axis = "";
        String action = "";
        String serialId = "";
        String group_id = "";
        String group_state = "";
        String group_name = "";
        String group_type = "";
        String group_child_type = "";
        String group_addr = "";
        String group_style = "";
        String scene_name;
        String scene_status;
        int scene_number;
        List<Device> group_member = new ArrayList<>();
        List<List<Condition>> conditionList = new ArrayList<>();
        List<Action> actionList = new ArrayList<>();
        List<String> serialIdList = new ArrayList<>();
        switch (cmd) {
            case "delete_obox": {
                try {
                    String requestJson = CloudParseUtil.getJsonParm(json, "request");
                    requestJson = requestJson.replace("[\"{", "[{").replace("}\"]", "}]").replace("\\", "").replace("[", "").replace("]", "");
                    requestObject = new JSONObject(requestJson);
                    obox_serial_id = requestObject.getString("obox_serial_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CloudDataPool.deleteOboxData(obox_serial_id);
                deleteObox(obox_serial_id);
                Intent intent = new Intent();
                intent.putExtra("delete_obox", true);
                intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
                context.sendBroadcast(intent);
                break;
            }
            case "add_obox": {
                try {
                    Gson gson = new Gson();
                    String requestJson = CloudParseUtil.getJsonParm(json, "request");
                    requestJson = requestJson.replace("[\"{", "[{").replace("}\"]", "}]").replace("\\", "");
                    requestObject = new JSONObject(requestJson);
                    JSONArray jsonArray = requestObject.getJSONArray("obox");
                    String jsonConfig = jsonArray.toString();
                    jsonConfig = jsonConfig.substring(1, jsonConfig.length() - 1);
                    JSONObject jsonObject = new JSONObject(jsonConfig);
                    JSONArray jsonArray_device = jsonObject.getJSONArray("device_config");
                    if (jsonArray_device.length() > 0) {
                        for (int i = 0; i < jsonArray_device.length(); i++) {
                            Device dev = gson.fromJson(jsonArray_device.getString(i), Device.class);
                            CloudDataPool.getDevices().add(dev);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setAction(OBConstant.StringKey.UPDATE_ADD_OBOX);
                context.sendBroadcast(intent);
                break;
            }
            case "set_group": {
                /*把错误的json转成正确json*/
                String dataStr = CloudParseUtil.getJsonParm(json, "data");
                ParseWrongJson parseWrongJson = new ParseWrongJson(dataStr);
                String dataJson = parseWrongJson.getStr();
                try {
                    dataObject = new JSONObject(dataJson);
                    groupsObject = dataObject.getJSONObject("groups");
                    operate_type = dataObject.getString("operate_type");
                    group_id = groupsObject.getString("group_id");
                    group_state = groupsObject.getString("group_state");
                    group_name = groupsObject.getString("group_name");
                    group_type = groupsObject.optString("group_type");
                    obox_serial_id = groupsObject.optString("obox_serial_id");
                    group_child_type = groupsObject.optString("group_child_type");
                    group_style = groupsObject.optString("group_style");
                    // FIXME: 2017/11/26 飞机的控制指令不处理
                    if (group_type.equals("10") && operate_type.equals(CloudConstant.ParameterValue.EXUTE_GROUP)) {
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (operate_type) {
                    case "00": //删除组
                        for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                            if (group_id.equals(CloudDataPool.getGroups().get(i).getGroup_id())) {
                                CloudDataPool.getGroups().remove(i);
                                break;
                            }
                        }
                        break;
                    case "01": //设置 、相当于创建:
                        Group groups = new Group(group_member, group_id, group_name, group_type, group_state, group_child_type, group_style, obox_serial_id, group_addr);
                        groups.setGroupAddr(group_addr);
                        if (obox_serial_id != null && !obox_serial_id.equals("")) {
                            groups.setObox_serial_id(obox_serial_id);
                        }
                        boolean isHave = false;
                        for (Group g :
                                CloudDataPool.getGroups()) {
                            if (g.getGroup_id().equals(groups.getGroup_id())) {
                                isHave = true;
                                break;
                            }
                        }
                        if (!isHave) {
                            CloudDataPool.addGroups(groups);
                        }
                        break;
                    case "02": //覆盖成员
                    case "03": //添加成员
                        Gson gson = new Gson();
                        for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                            if (group_id.equals(CloudDataPool.getGroups().get(i).getGroup_id())) {
                                CloudDataPool.getGroups().get(i).setGroup_type(group_type);
                                CloudDataPool.getGroups().get(i).setGroup_child_type(group_child_type);
                                break;
                            }
                        }
                        try {
                            JSONArray jsonArray = groupsObject.optJSONArray("group_member");
                            if (jsonArray.length() > 0) {
                                for (int j = 0; j < CloudDataPool.getGroups().size(); j++) {
                                    if (group_id.equals(CloudDataPool.getGroups().get(j).getGroup_id())) {
                                        List<Device> groupMember = CloudDataPool.getGroups().get(j).getGroup_member();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            boolean isCanAdd = true;
                                            Device dev = gson.fromJson(jsonArray.getString(i), Device.class);
                                            for (int m = 0; m < groupMember.size(); m++) {
                                                if (dev.getSerialId().equals(groupMember.get(m).getSerialId())) {
                                                    isCanAdd = false;
                                                    break;
                                                }
                                            }
                                            if (isCanAdd) {
                                                groupMember.add(dev);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case "04": //删除成员
                        Gson gson1 = new Gson();
                        Device dev;
                        try {
                            JSONArray jsonArray1;
                            jsonArray1 = groupsObject.optJSONArray("group_member");
                            serialIdList.clear();
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                dev = gson1.fromJson(jsonArray1.getString(i), Device.class);
                                serialIdList.add(dev.getSerialId());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int k = 0; k < serialIdList.size(); k++) {
                            String serial_Id = serialIdList.get(k);
                            for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                                if (group_id.equals(CloudDataPool.getGroups().get(i).getGroup_id())) {
                                    for (int j = 0; j < CloudDataPool.getGroups().get(i).getGroup_member().size(); j++) {
                                        if (serial_Id.equals(CloudDataPool.getGroups().get(i).getGroup_member().get(j).getSerialId())) {
                                            CloudDataPool.getGroups().get(i).getGroup_member().remove(j);
                                        }
                                    }
                                }
                            }
                        }
                        break;

                    case "05": //改名
                        for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                            if (group_id.equals(CloudDataPool.getGroups().get(i).getGroup_id())) {
                                CloudDataPool.getGroups().get(i).setGroup_name(group_name);
                            }
                        }
                        break;
                    case "06": //执行
                        for (int i = 0; i < CloudDataPool.getGroups().size(); i++) {
                            if (group_id.equals(CloudDataPool.getGroups().get(i).getGroup_id())) {
                                CloudDataPool.getGroups().get(i).setGroup_state(group_state);
                            }
                        }
                        break;
                }
                Intent intent = new Intent();
                intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
                intent.putExtra("operateType", operate_type);
                intent.putExtra("cmd", cmd);
                context.sendBroadcast(intent);
                break;
            }
            case "setting_sc_info":
                try {
                    String dataJson = CloudParseUtil.getJsonParm(json, "data");
                    dataObject = new JSONObject(dataJson);
                    scene_number = dataObject.getInt("scene_number");
                    scene_name = dataObject.getString("scene_name");
                    String requestJson = CloudParseUtil.getJsonParm(json, "request");
                    requestJson = requestJson.replace("[\"{", "[{").replace("}\"]", "}]").replace("\\", "");
                    SceneBeanTest sceneBean = new Gson().fromJson(requestJson, SceneBeanTest.class);
                    CloudScene cloudScene = new CloudScene();
                    List<CloudScene> cloudSceneList = new ArrayList<>();
                    for (int j = 0; j < sceneBean.getScene().size(); j++) {
                        if (sceneBean.getScene().get(j).getActions() != null) {
                            for (int i = 0; i < sceneBean.getScene().get(j).getActions().size(); i++) {
                                actionList.add(sceneBean.getScene().get(j).getActions().get(i));
                                cloudScene.setActions(actionList);
                            }
                        }
                        if (sceneBean.getScene().get(j).getConditions() != null) {
                            for (int i = 0; i < sceneBean.getScene().get(j).getConditions().size(); i++) {
                                conditionList.add(sceneBean.getScene().get(j).getConditions().get(i));
                                cloudScene.setConditions(conditionList);
                            }
                        }
                        cloudScene.setScene_type(sceneBean.getScene().get(j).getScene_type());
                        cloudScene.setScene_group(sceneBean.getScene().get(j).getScene_group());
                        cloudScene.setScene_name(sceneBean.getScene().get(j).getScene_name());
                        cloudScene.setScene_number(sceneBean.getScene().get(j).getScene_number());
                        cloudScene.setScene_status(sceneBean.getScene().get(j).getScene_status());
                        cloudSceneList.add(cloudScene);
                    }
                    Intent intent = new Intent();
                    intent.setAction(OBConstant.StringKey.UPDATE_SCENE_CLOUD_SETTING);
                    intent.putExtra("sceneCloudList", (Serializable) cloudSceneList);
                    intent.putExtra("scene_number", scene_number);
                    intent.putExtra("scene_name", scene_name);
                    context.sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "execute_sc":
                try {
                    String dataJson = CloudParseUtil.getJsonParm(json, "data");
                    dataObject = new JSONObject(dataJson);
                    scene_number = dataObject.getInt("scene_number");
                    scene_status = dataObject.getString("scene_status");
                    Intent intent = new Intent();
                    intent.setAction(OBConstant.StringKey.UPDATE_SCENE_CLOUD_EXUTE);
                    intent.putExtra("scene_number", scene_number);
                    intent.putExtra("scene_status", scene_status);
                    context.sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "modify_user":
                String root_name = "";
                String admin_name = "";
                String user_name = "";
                String weight = "";
                String pwd = "";
                String modify_type = "";
                try {
                    String requestJson = CloudParseUtil.getJsonParm(json, "request");
                    requestJson = requestJson.replace("[", "").replace("]", "");
                    requestObject = new JSONObject(requestJson);
                    weight = requestObject.getString("weight");
                    modify_type = requestObject.getString("modify_type");
                    if (weight.equals("02")) {
                        root_name = requestObject.getString("root_name");
                    } else if (weight.equals("03")) {
                        admin_name = requestObject.getString("admin_name");
                    }
                    user_name = requestObject.getString("user_name");
                    if (!modify_type.equals("00")) {
                        pwd = requestObject.getString("pwd");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intentUser = new Intent();
                intentUser.setAction(OBConstant.StringKey.UPDATE_MODIFY_USER);
                intentUser.putExtra("root_name", root_name);
                intentUser.putExtra("admin_name", admin_name);
                intentUser.putExtra("user_name", user_name);
                intentUser.putExtra("weight", weight);
                intentUser.putExtra("pwd", pwd);
                intentUser.putExtra("modify_type", modify_type);
                context.sendBroadcast(intentUser);
                break;

            case "set_device_location": {
                try {
                    String dataJson = CloudParseUtil.getJsonParm(json, "data");
                    dataObject = new JSONObject(dataJson);
                    boolean suc = dataObject.getBoolean("success");
                    if (!suc) {
                        return;
                    }
                    String requestJson = CloudParseUtil.getJsonParm(json, "request");
//                    requestJson = requestJson.replace("[\"", "").replace("\"]", "");
                    requestJson = requestJson.replace("[", "").replace("]", "");
                    if (!requestJson.equals("")) {
                        requestObject = new JSONObject(requestJson);
                        location_d = requestObject.getString("location");
                        x_axis = requestObject.getString("x_axis");
                        y_axis = requestObject.getString("y_axis");
                        action = "01";
                        serialId = requestObject.getString("serialId");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                intent.setAction(OBConstant.StringKey.UPDATE_SET_DEVICE_LOCATION);
                intent.putExtra("serialId", serialId);
                intent.putExtra("location", location_d);
                intent.putExtra("x_axis", x_axis);
                intent.putExtra("y_axis", y_axis);
                intent.putExtra("action", action);
                context.sendBroadcast(intent);
                break;
            }
            case "create_location":
                try {
                    String dataJson = CloudParseUtil.getJsonParm(json, "data");
                    dataObject = new JSONObject(dataJson);
                    boolean suc = dataObject.getBoolean("success");
                    if (!suc) {
                        return;
                    }
                    String requestJson = CloudParseUtil.getJsonParm(json, "request");
                    requestObject = new JSONObject(requestJson);
                    action = requestObject.getString("action");
                    location = requestObject.getInt("location");
                    String building = requestObject.getString("building");
                    String room = requestObject.getString("room");
                    String download_url = requestObject.getString("download_url");
                    Intent intent = new Intent();
                    intent.setAction(OBConstant.StringKey.UPDATE_CREATE_LOCATION);
                    intent.putExtra("action", action);
                    intent.putExtra("location", location);
                    intent.putExtra("building", building);
                    intent.putExtra("room", room);
                    intent.putExtra("download_url", download_url);
                    context.sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "set_scene_location":
            case "delete_scene_location":
                try {
                    String requestJson = CloudParseUtil.getJsonParm(json, "request");
                    requestJson = requestJson.replace("[\"", "").replace("\"]", "");
//                    requestJson = requestJson.replace("[", "").replace("]", "");
                    requestObject = new JSONObject(requestJson);
                    scene_number = requestObject.getInt("scene_number");
                    location = requestObject.getInt("location");
                    action = requestObject.getString("CMD");
                    Intent intent = new Intent();
                    intent.setAction(OBConstant.StringKey.UPDATE_SCENE_LOCATION);
                    intent.putExtra("scene_number", String.valueOf(scene_number));
                    intent.putExtra("location", String.valueOf(location));
                    intent.putExtra("action", action);
                    context.sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * 处理obox上下线消息
     *
     * @param json 推送数据
     */
    private void oboxHeart(String json) {
        String onLine;
        String serialId = CloudParseUtil.getJsonParm(json, "serialId");
        onLine = CloudParseUtil.getJsonParm(json, "onLine");
        String status;
        if (onLine.equals("true")) {
            status = "1";
        } else {
            status = "0";
        }
        for (int i = 0; i < CloudDataPool.getAllOboxList().size(); i++) {
            if (serialId.equals(CloudDataPool.getAllOboxList().get(i).getObox_serial_id())) {
                CloudDataPool.getAllOboxList().get(i).setObox_status(status);
                break;
            }
        }
        Intent intent = new Intent();
        intent.setAction(OBConstant.StringKey.OBOX_HEART_INFO);
        intent.putExtra("serialId", serialId);
        intent.putExtra("onLine", onLine);
        context.sendBroadcast(intent);
    }


    private static void deleteObox(String obox_serial_id) {
        for (int i = 0; i < CloudDataPool.getAllOboxList().size(); i++) {
            if (obox_serial_id.equals(CloudDataPool.getAllOboxList().get(i).getObox_serial_id())) {
                CloudDataPool.getAllOboxList().remove(i);
            }
        }
    }

    private void sendBroadCast() {
        Intent intent = new Intent();
        intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
        context.sendBroadcast(intent);
    }

    /**
     * 返回字符串名称
     */
    public String getNodeId(byte[] id) {
        try {
            return new String(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
