package com.onbright.oblink.local.bean;

/**插座、开关
 * Created by adolf_dong on 2016/6/23.
 */
public class Obsocket extends ObNode {
    public static final int ISOPEN = 0;

    public Obsocket(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
