package com.onbright.oblink.cloud.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务器情景的行为，情景触发时候的动作
 * 获取action后，依据唯一序列号和地址找到行为的具体设备实例，
 * 注意此类的set方法均只设定此对象的属性，而不触发网络交互
 *
 */
public class Action implements Serializable {

    /**
     * action类型：组"01" 单节点"00"
     */
    private String node_type;

    /**
     * 设备地址,如果是组则没有该参数
     */
    private String addr;

    /**
     * Obox序列号,如果是组没有次参数
     */
    private String obox_serial_id;

    /**
     * 设备类型
     */
    private String device_type;

    /**
     * 设备子类型
     */
    private String device_child_type;

    /**
     * 设备/组名字
     */
    private String actionName;

    /**
     * 如果是单节点，则没有此参数
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
     * 具体行为，七字节字符串
     */
    private String action;


    public Action() {

    }


    /**
     * @param action_id 设备序列号
     * @param action    具体的行为动作
     * @param node_type 设备类型,组"01" 单节点"00"{@link com.onbright.oblink.local.net.OBConstant.NodeType}
     */
    public Action(String action_id, String action, String node_type) {

        this.serialId = action_id;
        this.action = action;
        this.node_type = node_type;
    }

    /**
     * 获取序列号
     */
    public String getSerialId() {
        return serialId;
    }

    /**
     * 设置序列号
     */
    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    /**
     * 获取行为动作
     */
    public String getAction() {
        return action;
    }

    /**
     * 设置行为动作
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 获取action类型  组"01" 单节点"00"
     */
    public String getNode_type() {
        return node_type;
    }

    /**
     * 设置action类型
     * 组"01" 单节点"00"
     */
    public void setNode_type(String node_type) {
        this.node_type = node_type;
    }


    /**
     * 获取action的设备类型
     */
    public String getDevice_type() {
        return device_type;
    }

    /**
     * 设置action的设备类型
     */
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    /**
     * 获取action的obox序列号
     */
    public String getObox_serial_id() {
        return obox_serial_id;
    }

    /**
     * 设置obox的序列号
     */
    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
    }

    /**
     * 获取action的地址
     */
    public String getAddr() {
        return addr;
    }

    /**
     * 设置action的地址
     */
    public void setAddr(String addr) {
        this.addr = addr;
    }

    /**获取action的子类型
     */
    public String getDevice_child_type() {
        return device_child_type;
    }
    /**设置action的子类型
     */
    public void setDevice_child_type(String device_child_type) {
        this.device_child_type = device_child_type;
    }

    /**获取action名称
     */
    public String getActionName() {
        return actionName;
    }

    /**设置action名称
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**获取action的groupid，当action的类型为组即"01"时可用
     */
    public String getGroup_id() {
        return group_id;
    }
    /**设置action的groupid，当action的类型为组即"01"时可用
     */
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

    /**action是否在情景内
     * @param scene_number 情景序列号
     * @return true则表示存在
     */
    public boolean isInScene(String scene_number) {
        if (inSceneMap == null) {
            inSceneMap = new HashMap<>();
        }
        String key = scene_number + "";
        return inSceneMap.get(key) == null ? true : inSceneMap.get(key);
    }

    /**设置action是否在情景内
     * @param serisNum 情景序列号
     * @param isInScene true则表示存在
     */
    @SuppressWarnings("unused")
    public void putInScene(int serisNum, boolean isInScene) {
        if (inSceneMap == null) {
            inSceneMap = new HashMap<>();
        }
        String key = serisNum + "";
        inSceneMap.put(key, isInScene);
    }
}
