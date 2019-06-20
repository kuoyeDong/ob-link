package com.onbright.oblink.local.bean;

/**窗帘、幕布 当前位置 当前状态
 * Created by adolf_dong on 2016/6/23.
 */
public class Curtain extends ObNode {
    public static final int POSITION = 0;
    public static final int STATE = 1;

    public Curtain(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
