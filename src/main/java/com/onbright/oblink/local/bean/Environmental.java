package com.onbright.oblink.local.bean;

/**
 * 环境传感器
 * 上报状态位 第一个字节  0bit光上报开关 1bit温度上报开关 2bit湿度上报开关
 * 光 温 湿
 * Created by adolf_dong on 2016/6/23.
 */
public class Environmental extends ObSensor {

    public static final int LIGHT_UTOREP = 0;
    public static final int TEMP_REP = 1;
    public static final int HUMI_REP = 2;

    private static final int LIGHT_REP_STALL = 1;
    private static final int LIGHT_STALL = 2;
    private static final int TEMP_REP_STALL = 3;
    private static final int TEM_STALL = 4;
    private static final int HUMI_REP_STALL = 5;
    private static final int HUMI_STALL= 6;
    private static final int VOLTAGE = 7;


    public Environmental(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
