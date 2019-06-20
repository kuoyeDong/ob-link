package com.onbright.oblink.local.bean;

/**co传感器
 * 网关自动上报  设置上报浓度  当前浓度   供电电压
 * Created by adolf_dong on 2016/6/23.
 */
public class Co extends ObSensor {


    public Co(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
