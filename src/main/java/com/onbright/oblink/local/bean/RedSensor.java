package com.onbright.oblink.local.bean;

/**
 * 人体红外
 * Created by adolf_dong on 2016/6/23.
 */
public class RedSensor extends ObSensor {

    public RedSensor(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
