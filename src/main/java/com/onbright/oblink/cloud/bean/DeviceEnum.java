package com.onbright.oblink.cloud.bean;

import com.onbright.oblink.local.net.OBConstant;

/**
 * use by:
 * create by dky at 2019/7/3
 */
public enum DeviceEnum {
    SIMPLE_LAMP(OBConstant.NodeType.IS_LAMP, OBConstant.NodeType.IS_SIMPLE_LAMP),
    WARM_LAMP(OBConstant.NodeType.IS_LAMP, OBConstant.NodeType.IS_WARM_LAMP);

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
