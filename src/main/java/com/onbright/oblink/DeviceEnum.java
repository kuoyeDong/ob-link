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
    HOUSE_LOCK(OBConstant.NodeType.SMART_LOCK, OBConstant.NodeType.SMART_LOCK_OB_HOUSE);


//    单键单线开关，
//    双键单线开关，
//    双向开关+情景（所有的开关面板和情景面板），
//    窗帘，
//    人体感应，
//    智能插座，
//    温湿度传感器，
//    红外转发器

    private final int pType;
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
