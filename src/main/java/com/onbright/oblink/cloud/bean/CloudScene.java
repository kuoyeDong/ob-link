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
    /**
     * 保存在云端的场景
     */
    public static final String SERVER = "00";
    /**
     * 下发到obox的场景
     */
    public static final String LOCAL = "01";

    /**
     * 关闭场景，并非删除，使得场景暂时失效
     */
    public static final String DISABLE = "00";

    /**
     * 打开场景，并非新增，使得场景生效
     */
    public static final String ENABLE = "01";

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
     * 场景的使能
     * {}
     * Disable|Enable,
     * {@link #ENABLE#DISABLE}
     */
    private String scene_status = ENABLE;

    /**
     * 场景类型
     * {@link #SERVER#LOCAL}
     */
    private String scene_type = SERVER;

    /**
     * 条件组集合，请注意两层集合结构，每层最多三个条件，如下所示
     * [[condition,condition,condition],[condition,condition,condition],[condition,condition,condition]]
     * 内层集合中，三个条件同时满足才会触发场景
     * 外层集合中，三个条件组任一满足即可触发场景
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
     * 场景组,可自定规则组成链式表或容器集，也可不使用
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



