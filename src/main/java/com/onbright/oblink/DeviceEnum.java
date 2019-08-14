package com.onbright.oblink;

import com.onbright.oblink.local.net.OBConstant;

/**
 * use by:设备枚举，目前只提供智能门锁（酒店），
 * create by dky at 2019/7/3
 */
public enum DeviceEnum {
    /**
     * 智能门锁(酒店)
     */
    HOTEL_LOCK(OBConstant.NodeType.SMART_LOCK, OBConstant.NodeType.SMART_LOCK_OB_HOTEL),
    /**
     * 智能门锁（家居）
     */
    HOUSE_LOCK(OBConstant.NodeType.SMART_LOCK, OBConstant.NodeType.SMART_LOCK_OB_HOUSE),

    /*以下为面板设备*/

    /**
     * 单线开关
     */
    SWITCH(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.SWITCH),
    /**
     * 1路开关
     */
    SINGLE_TOUCH_SWITCH(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.SINGLE_TOUCH_SWITCH),
    /**
     * 2路开关
     */
    DOUBLE_TOUCH_SWITCH(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.DOUBLE_TOUCH_SWITCH),

    /**
     * 3路开关
     */
    THREE_TOUCH_SWITCH(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.THREE_TOUCH_SWITCH),

    /**
     * 4路开关
     */
    FOUR_TOUCH_SWITCH(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.FOUR_TOUCH_SWITCH),

    /**
     * 1路开关 + 3路情景面板
     */
    SINGLE_SWITCH_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.SINGLE_SWITCH_SCENE_PANEL),

    /**
     * 2路开关 + 3路情景面板
     */
    DOUBLE_SWITCH_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.DOUBLE_SWITCH_SCENE_PANEL),

    /**
     * 3路开关 + 3路情景面板
     */
    THREE_SWITCH_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.THREE_SWITCH_SCENE_PANEL),

    /**
     * 一键单线开关
     */
    ONE_BUTTON_WIRE_SOCKET(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.ONE_BUTTON_WIRE_SOCKET),

    /**
     * 二键单线开关
     */
    TWO_BUTTON_WIRE_SOCKET(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.TWO_BUTTON_WIRE_SOCKET),

    /**
     * 红外对管、3路开关 + 3路情景面板
     */
    THREE_SWITCH_RED_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.THREE_SWITCH_RED_SCENE_PANEL),

    /**
     * 六键情景
     */
    SIX_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.SIX_SCENE_PANEL),

    /**
     * 六键情景+红外对管
     */
    SIX_SCENE_RED_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.SIX_SCENE_RED_PANEL),

    /**
     * 一路情景面板
     */
    SINGLE_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.SINGLE_SCENE_PANEL),

    /**
     * 两路情景面板
     */
    DOUBLE_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.DOUBLE_SCENE_PANEL),

    /**
     * 3路情景面板
     */
    THREE_SCENE_PANEL(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.THREE_SCENE_PANEL),

    /*以下为传感器*/
    /**
     * 门磁
     */
    DOOR_WINDOW_MAGNET(OBConstant.NodeType.IS_SENSOR, OBConstant.NodeType.DOOR_WINDOW_MAGNET),

    /**
     * 水浸传感器
     */
    FLOOD(OBConstant.NodeType.IS_SENSOR, OBConstant.NodeType.FLOOD),
    /**
     * 烟雾传感器
     */
    SMOKE_SENSOR(OBConstant.NodeType.IS_SENSOR, OBConstant.NodeType.SMOKE_SENSOR),
    /**
     * 红外
     */
    RED_SENSOR(OBConstant.NodeType.IS_SENSOR, OBConstant.NodeType.RED_SENSOR),
    /**
     * 温度湿度传感器
     */
    TEMP_HUMID_SENSOR(OBConstant.NodeType.IS_SENSOR, OBConstant.NodeType.TEMP_HUMID_SENSOR),

    /*以下为窗帘幕布*/

    /**
     * 窗帘
     */
    THE_CURTAINS(OBConstant.NodeType.IS_CURTAIN, OBConstant.NodeType.THE_CURTAINS),

    /**
     * RF插座
     */
    SOCKET(OBConstant.NodeType.IS_OBSOCKET, OBConstant.NodeType.SOCKET),

    /**
     * 红外转发器
     */
    INFRARED_TRANSPONDER(OBConstant.NodeType.WIFI_IR, 0);
    /**
     * 设备类型
     */
    private final int pType;
    /**
     * 设备子类型
     */
    private final int type;

    public int getpType() {
        return pType;
    }

    public int getType() {
        return type;
    }

    DeviceEnum(int pType, int type) {
        this.pType = pType;
        this.type = type;
    }
}
