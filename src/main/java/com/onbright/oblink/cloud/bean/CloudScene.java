package com.onbright.oblink.cloud.bean;


import com.onbright.oblink.local.net.Transformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 云版本情景，在预设条件下触发预设节点动作
 * 此函数的set方法不会触发任何网络交互，仅设置属性
 */
public class CloudScene implements Serializable {
    /**
     * 设置情景类型的时候参照，仅存在服务器
     */
    public static final String SERVER = "00";
    /**
     * 设置情景类型的时候参照，同时存在于obox
     */
    public static final String LOCAL = "01";

    /**
     * 情景名称
     */
    private String scene_name;
    /**
     * 场景序列号
     */
    private String scene_number = "0";

    /**
     * 获取情景在obox内的情景序列号
     */
    public String getObox_scene_number() {
        return obox_scene_number;
    }

    /**
     * 设置情景在obox内的情景序列号
     */
    public void setObox_scene_number(String obox_scene_number) {
        this.obox_scene_number = obox_scene_number;
    }

    /**
     * obox本地场景序列号
     */
    private String obox_scene_number;
    /**
     * 条件场景使能，值为0| 1
     * 场景的使能状态 Disable|Enable, 在创建时候任何时候都必须传
     */
    private String scene_status;

    /**
     * 情景类型
     * 00|01  , 仅存在服务器｜下发到obox
     * 如不传，则默认是条件
     */
    private String scene_type = "00";

    /**
     * 条件
     */
    private List<List<Condition>> conditions;

    /**
     * 情景对应的obox序列号，当此序列号不存在时则为服务器情景
     */
    private String obox_serial_id;
    /**
     * 行为列表,拿到action后，实例化
     */
    private List<Action> actions;

    /**
     * 获取情景组，高阶用法，用于将情景形成链式表
     */
    public String getScene_group() {
        return scene_group;
    }

    /**
     * 设置情景组，高阶用法，用于将不用情景形成链式表
     */
    public void setScene_group(String scene_group) {
        this.scene_group = scene_group;
    }

    /**
     * 场景组
     */
    private String scene_group = "00";

    /**
     * @param scene_name     情景名称
     * @param scene_type     情景类型  00|01  , 仅存在服务器｜下发到obox
     * @param scene_number   情景序列号
     * @param conditions     情景条件集合
     * @param actions        情景行为集合
     * @param scene_status   场景使能，值为"0"不使能 "1"使能
     * @param obox_serial_id 情景所在的obox序列号
     */
    public CloudScene(String scene_name, String scene_type, String scene_number,
                      List<List<Condition>> conditions, List<Action> actions, String scene_status, String obox_serial_id) {
        this.scene_name = scene_name;
        this.scene_type = scene_type;
        this.scene_number = scene_number;
        this.conditions = conditions;
        this.actions = actions;
        this.scene_status = scene_status;
        this.obox_serial_id = obox_serial_id;
    }

    /**
     * 设置情景名称
     */
    public void setScene_name(String scene_name) {
        this.scene_name = scene_name;
    }

    /**
     * 获取情景名称
     */
    public String getScene_name() {
        return scene_name;
    }


    public CloudScene() {
    }

    /**
     * 获取情景类型
     * 00|01  , 仅存在服务器｜下发到obox
     * 如不传，则默认是服务器场景即不下发到obox
     */
    public String getScene_type() {
        return scene_type;
    }

    /**
     * 设置情景类型
     * 00|01  , 仅存在服务器｜下发到obox
     * 如不传，则默认是服务器即不下发到obox
     */
    public void setScene_type(String scene_type) {
        this.scene_type = scene_type;
    }


    /**
     * 获取情景序列号
     */
    public String getScene_number() {
        return scene_number;
    }

    /**
     * 设置情景序列号
     */
    public void setScene_number(String scene_number) {
        this.scene_number = scene_number;
    }

    /**
     * 获取情景的条件列表
     */
    public List<List<Condition>> getConditions() {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        return conditions;
    }

    /**
     * 设置情景的条件列表
     */
    public void setConditions(List<List<Condition>> conditions) {
        this.conditions = conditions;
    }

    /**
     * 获取情景触发后的具体行为列表
     */
    public List<Action> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    /**
     * 设置情景触发后的具体行为列表
     */
    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    /**
     * 获取情景的使能状态 "0"不使能  "1"使能
     */
    public String getScene_status() {
        return Transformation.byte2HexString((byte) Integer.parseInt(scene_status));
    }

    /**
     * 获取情景的使能状态 "0"不使能  "1"使能
     */
    public void setScene_status(String scene_status) {
        this.scene_status = scene_status;
    }


    /**获取情景所属obox的序列号
     */
    public String getObox_serial_id() {
        return obox_serial_id;
    }

    /**设置情景所属obox的序列号
     */
    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
    }

    /**
     * 设置整个类型设备的action
     *
     * @param modifyActionPtype 类型
     * @param modifyActionType  子类型
     * @param action            新action
     */
    public void setActionForType(int modifyActionPtype, int modifyActionType, String action) {
        for (int i = 0; i < actions.size(); i++) {
            Action action1 = actions.get(i);
            if (Integer.parseInt(action1.getDevice_type(), 16) == modifyActionPtype
                    && Integer.parseInt(action1.getDevice_child_type(), 16) == modifyActionType) {
                action1.setAction(action);
            }
        }
    }
}



