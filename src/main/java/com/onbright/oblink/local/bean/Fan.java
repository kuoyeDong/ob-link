package com.onbright.oblink.local.bean;

/**风扇 开关 风速  送风 摇头 静音 定时
 * Created by adolf_dong on 2016/6/23.
 */
public class Fan extends ObNode {
    public static final int IS_OPEN = 0;
    public static final int FAN_SPEED = 1;
    public static final int FAN_TYPE = 2;
    public static final int SCAN_STATE = 3;
    public static final int QUIT_STATE = 4;
    public static final int TIMING = 5;

    public Fan(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
