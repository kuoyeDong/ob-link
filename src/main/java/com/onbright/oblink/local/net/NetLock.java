package com.onbright.oblink.local.net;

/**
 * 网络锁，判定是否本机发送并且是否非心跳包
 * Created by adolf_dong on 2016/7/7.
 */
public class NetLock {

    private static byte[] send;


    public static void setSrc(byte[] pcmd) {
        send = pcmd;
    }

    /**
     * @return 是否适配
     */
    public static boolean isCompile(byte[] rec) {
        if (NetState.isBurn) {
            return (rec[0] & 0xff) == 0xb4;
        }
        if (NetState.isTranse) {
            return Transformation.halfByte2HexString(rec[0]).equals(Transformation.halfByte2HexString(send[0]));
        }
        return !((rec[4] & 0xff) == 0xa0
                && (rec[5] & 0xff) == 0x13
                && (rec[7]) == 0x02) && (((rec[5] & 0xff) == (send[5] & 0xff)) || ((rec[5] & 0xff) == 0x0f));
    }
}
