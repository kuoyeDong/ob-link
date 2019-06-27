package com.onbright.oblink.cloud.bean;

import android.widget.Checkable;

import com.onbright.oblink.local.net.Transformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 云版本情景
 * Created by adolf_dong on 2016/1/7.
 */
public class CloudScene implements Serializable, Checkable {
    public static final String SERVER = "00";
    public static final String LOCAL = "01";

    /**
     * 情景名称
     */
    private String scene_name;
    /**
     * 场景序列号
     */
    private String scene_number = "0";

    public String getObox_scene_number() {
        return obox_scene_number;
    }

    public void setObox_scene_number(String obox_scene_number) {
        this.obox_scene_number = obox_scene_number;
    }

    /**
     * obox本地场景序列号
     */
    private String obox_scene_number;
    /**
     * 条件场景使能，值为00| 01
     * 场景的使能状态 Disable|Enable, 在创建时候任何时候都必须传
     */
    private String scene_status;

    /**
     * 00|01  , 条件｜fail-safe
     * 如不传，则默认是条件
     */
    private String scene_type = "00";

    /**
     * 条件
     */
    private List<List<Condition>> conditions;

    private String obox_serial_id;
    /**
     * 行为列表,拿到action后，实例化
     */
    private List<Action> actions;

    public String getScene_group() {
        return scene_group;
    }

    public void setScene_group(String scene_group) {
        this.scene_group = scene_group;
    }

    /**
     * 场景组
     */
    private String scene_group = "00";


    /**
     * 推送类型0无推送/1APP 推送/2短信推送/3app+短信推送 ，不发该参数，默认为无推送
     */
    private String msg_alter = "0";
    public static final String NO_ALERT = "0";
    public static final String APP_ALERT = "1";
    public static final String SMS_ALERT = "2";
    public static final String APP_SMS_ALERT = "3";

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

    public void setScene_name(String scene_name) {
        this.scene_name = scene_name;
    }

    public String getScene_name() {
        return scene_name;
    }


    /**
     * 用于新建情景
     */
    public CloudScene() {
    }

    public String getScene_type() {
        return scene_type;
    }

    public void setScene_type(String scene_type) {
        this.scene_type = scene_type;
    }


    public String getScene_number() {
        return scene_number;
    }

    public void setScene_number(String scene_number) {
        this.scene_number = scene_number;
    }

    public String getMsg_alter() {
        return msg_alter;
    }

    public void setMsg_alter(String msg_alter) {
        this.msg_alter = msg_alter;
    }

    public List<List<Condition>> getConditions() {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        return conditions;
    }

    public void setConditions(List<List<Condition>> conditions) {
        this.conditions = conditions;
    }

    /**
     * 获取action后和本地操作一样 ，寻找具体的action
     */
    public List<Action> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getScene_status() {
        return Transformation.byte2HexString((byte) Integer.parseInt(scene_status));
    }

    public void setScene_status(String scene_status) {
        this.scene_status = scene_status;
    }


    public String getObox_serial_id() {
        return obox_serial_id;
    }

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

    @Override
    public void setChecked(boolean checked) {
        this.isCheckd = checked;
    }

    private transient boolean isCheckd;

    @Override
    public boolean isChecked() {
        return isCheckd;
    }

    @Override
    public void toggle() {
        isCheckd = !isCheckd;
    }
}



