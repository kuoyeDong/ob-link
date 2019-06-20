package com.onbright.oblink.local.bean;

/**空气净化器,开关，负离子开关，档位，空气质量，定时时间
 * Created by adolf_dong on 2016/6/23.
 */
public class AirClean extends ObNode {

/*private boolean isOpen;
private boolean anionIsOpen;
private int stall;
private int airQuality;
private int timing ;*/

    public static final int ISOPEN = 0;
    public static final int ANION_ISOPEN = 1;
    public static final int STALL = 2;
    public static final int AIR_QUALITY = 3;
    public static final int TIMING = 4;

    public AirClean(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}

