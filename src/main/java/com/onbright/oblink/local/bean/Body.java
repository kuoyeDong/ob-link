package com.onbright.oblink.local.bean;

/**
 * 人体传感器  雷达+红外
 * 网关自动上报(包含雷达上报和红外上报)  设置上报雷达等级 当前雷达等级  设置上报红外等级 当前红外等级  供电电压
 * Created by adolf_dong on 2016/6/23.
 */
public class Body extends ObSensor {
    /*  private int radarAutoRep;
      private int redAutoRep;
      private int radarRepStall;
      private int radarStall;
      private int redRepStall;
      private int redStall;
      private int voltage;
  */
    public static final int RADAR_AUTOREP = 0;
    public static final int RED_AUTOREP = 1;

    public static final int RADAR_REP_STALL = 1;
    public static final int RADAR_STALL = 2;
    public static final int RED_REP_STALL = 3;
    public static final int RED_STALL = 4;
    public static final int VOLTAGE = 5;


    public Body(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }
}
