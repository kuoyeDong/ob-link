package com.onbright.oblink.local.bean;

/**六合一环境传感器
 * state12个字节
 * Created by adolf_dong on 2017/8/1.
 */
public class EnvironmentSensor extends ObNode {
    /**
     *
     * 三位小数
     */
    public static final int FORMALDEHYDE = 0;
    public static final int PM = 1;
    public static final int CO = 2;
    /**
     * 一位小数
     */
    public static final int TEMP = 3;
    public static final int HUMI = 4;
    public static final int CO2 = 5;

    public EnvironmentSensor(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }

    @Override
    public byte[] getState() {
        if (state != null) {
            return state;
        } else {
            state = new byte[12];
            return state;
        }
    }

}
