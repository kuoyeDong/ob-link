package com.onbright.oblink.local.bean;

/**
 * 电饭煲 煮状态 煮类型  预约时间
 * Created by adolf_dong on 2016/6/23.
 */
public class Cooker extends ObNode {
    public static final int COOKSTATE = 0;
    public static final int COOKTYPE = 1;
    public static final int APPOINTMENT = 2;

    public Cooker(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
