package com.onbright.oblink.local.bean;

/**pm2.5传感器
 * 协议未提供
 * Created by adolf_dong on 2016/6/23.
 */
public class Pm25Sensor extends ObSensor {

    public Pm25Sensor(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
