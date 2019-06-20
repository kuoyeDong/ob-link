package com.onbright.oblink.local.bean;

/**空调 包含模式 温度 定时  风速 风向
 * Created by adolf_dong on 2016/6/23.
 */
public class Aircon extends ObNode {
/*    private int mode;
    private int temp;
    private int timing;
    private int windSpeed;
    private int windDirec;*/
    public static final int MODE =0;
    public static final int TEMP =1;
    public static final int TIMING =2;
    public static final int WIND_SPEED =3;
    public static final int WIND_DIREC =4;

    public Aircon(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
