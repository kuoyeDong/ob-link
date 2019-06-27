package com.onbright.oblink.local.bean;

/**
 * 灯类型节点
 */
public class Lamp extends ObNode {

    public static final int Light = 0;
    public static final int COOL = 1;
    public static final int WARM = 2;
    public static final int RED = 3;
    public static final int GREEN = 4;
    public static final int BLUE = 5;
    public static final int TIME = 6;
// FIXME: 2016/7/21 不能强转就new
    public Lamp(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}