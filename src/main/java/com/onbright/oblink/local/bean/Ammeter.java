package com.onbright.oblink.local.bean;

/**
 * 抄表器 暂不实现
 * Created by adolf_dong on 2016/6/23.
 */
public class Ammeter extends ObNode {
    public Ammeter(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
