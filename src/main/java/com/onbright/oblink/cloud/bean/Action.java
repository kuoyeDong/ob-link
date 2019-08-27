package com.onbright.oblink.cloud.bean;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取玩action后，依据唯一序列号和地址找到行为的具体设备实例
 * <p>
 * Created by adolf_dong on 2016/1/7.
 */
public class Action implements Serializable {

    /**
     * 单节点
     */
    public static final String SINGLE = "00";
    /**
     * 组
     */
    public static final String GROUPS = "01";
    /**
     * 单品wifi节点
     */
    public static final String WIFI_SINGLE = "07";
    /**
     * 设备类型：组"01" 单节点"00"
     */
    private String node_type;

    /**
     * 设备地址,如果是组则不传
     */
    private String addr;

    /**
     * Obox序列号,如果是组则不传
     */
    private String obox_serial_id;

    /**
     * 设备类型
     */
    private String device_type;

    /**
     * 设备子类型
     */
    private String device_child_type = "00";

    /**
     * 设备/组名字
     */
    private String actionName;

    /**
     * 如果是单节点，则不传
     */
    private String group_id;
    private Map<String, Boolean> inSceneMap;

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }

    /**
     * 如果是本地组的情况下，传入本地组地址
     */
    private String groupAddr;
    /**
     * 设备序列号，如果是组则不传
     */
    private String serialId;
    /**
     * 具体行为
     */
    private String action;


    public Action() {

    }


    public Action(String action_id, String action, String node_type) {
        this.serialId = action_id;
        this.action = action;
        this.node_type = node_type;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    /**
     * @return 非红外转发行为，不能超过8字节，超出部分被sub
     */
    public String getAction() {
        if (action != null && action.length() > 16 && !action.contains("{")) {
            return action.substring(0, 16);
        } else {
            return action;
        }
    }

    /**
     * @param action 非红外转发行为，不能超过8字节，超出部分被sub
     */
    public void setAction(String action) {
        if (action != null && action.length() > 16 && !action.contains("{")) {
            this.action = action.substring(0, 16);
        } else {
            this.action = action;
        }
    }

    public String getNode_type() {
        return node_type;
    }

    public void setNode_type(String node_type) {
        this.node_type = node_type;
    }


    public String getDevice_type() {
        if (node_type.equals(Action.WIFI_SINGLE)) {
            return "51";
        }
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getObox_serial_id() {
        return obox_serial_id;
    }

    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    /**
     * @return 如果是wifi红外转发器就
     */
    public String getDevice_child_type() {
        if (node_type.equals(Action.WIFI_SINGLE)) {
            return "01";
        }
        return device_child_type;
    }

    public void setDevice_child_type(String device_child_type) {
        this.device_child_type = device_child_type;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Action) {
            Action action = (Action) o;
            return (action.node_type == null ? node_type == null : action.node_type.equals(node_type)) &&
                    (action.addr == null ? addr == null : action.addr.equals(addr)) &&
                    (action.obox_serial_id == null ? obox_serial_id == null : action.obox_serial_id.equals(obox_serial_id)) &&
                    (action.device_type == null ? device_type == null : action.device_type.equals(device_type)) &&
                    (action.device_child_type == null ? device_child_type == null : action.device_child_type.equals(device_child_type)) &&
                    (action.actionName == null ? actionName == null : action.actionName.equals(actionName)) &&
                    (action.group_id == null ? group_id == null : action.group_id.equals(group_id)) &&
                    (action.groupAddr == null ? groupAddr == null : action.groupAddr.equals(groupAddr)) &&
                    (action.serialId == null ? serialId == null : action.serialId.equals(serialId)) &&
                    (action.action == null ? this.action == null : action.action.equals(this.action));
        }
        return false;
    }

    public boolean isInScene(String scene_number) {
        if (inSceneMap == null) {
            inSceneMap = new HashMap<>();
        }
        String key = scene_number + "";
        return inSceneMap.get(key) == null ? true : inSceneMap.get(key);
    }

    @SuppressWarnings("unused")
    public void putInScene(int serisNum, boolean isInScene) {
        if (inSceneMap == null) {
            inSceneMap = new HashMap<>();
        }
        String key = serisNum + "";
        inSceneMap.put(key, isInScene);
    }
}
