package com.onbright.oblink.local.bean;

/**
 * 光敏传感器 自动上报  上报强度   光照强度  供电电压
 * Created by adolf_dong on 2016/6/23.
 */
public class Als extends ObSensor {

    public Als(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
