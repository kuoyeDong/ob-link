package com.onbright.oblink.local.bean;

/**
 * com.ob.obsmarthouse.common.bean.localbean   ElectricCard
 * author:Adolf_Dong  time: 2018/12/6 17:18
 * use:
 */

public class ElectricCard extends ObSensor{
    public ElectricCard(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
