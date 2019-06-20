package com.onbright.oblink.local.net;


import java.util.Arrays;

/**
 * 对于接收线程端的读取的秘钥处理，拿到当前Home页面setofcmd指向的实例，拿到密码；
 * 算法类
 */
public class Sbox {

    static {
        System.loadLibrary("tcpsend");
    }
    private static final byte[] def = {0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38};
    private static final byte[] normal = {0x38, 0x38, 0x38, 0x38};
    private static final byte[] obsdefault = {0x31, 0x30, 0x30, 0x30};
    private static final byte[] obsmodify = {0x31, 0x30, 0x30, 0x31};
    private static final byte[] updatedefault = {0x31, 0x31, 0x30, 0x30};
    private static final byte[] updatemodify = {0x31, 0x31, 0x30, 0x31};
    private byte[] head = new byte[4];
    private byte[] cache = new byte[64];
    /**
     * 解密后数据
     */
    private byte[] result = new byte[64];
    /**
     * 发送数据
     */
    private byte[] goal = new byte[68];

    /**
     *是否要运行obos算法
     */
    private static final boolean NEED_OBS = true;


    private boolean needObs = true;

    /**
     * 解密算法
     *
     * @param res 要解密的68位数据
     * @param key 当前连接的密码
     * @return 解密后的64位数据
     */
    public byte[] unPack(byte[] res, byte[] key) {
        System.arraycopy(res, 0, head, 0, 4);
        System.arraycopy(res, 4, result, 0, 64);
        if (Arrays.equals(head, obsmodify) || Arrays.equals(head, updatemodify)) {
            obs(result, key);
        } else if (Arrays.equals(head, obsdefault) || Arrays.equals(head, updatedefault)) {
            obs(result, def);
        } else {
            needObs = false;
        }
        return result;
    }

    /**
     * 加密算法
     *
     * @param res            62位未携带校验码数据
     * @param key            当前连接的密码
     * @param encryptionType 加密类型 只有为0的时候表示不需要加密 ，1位默认密码加密，2为用户修改后密码加密
     * @param isUpdate       是否在升级状态
     * @return 加密后的68位数据
     */
    public byte[] pack(byte[] res, byte[] key, int encryptionType, boolean isUpdate) {
        int crc = CRC16(res, 62);
        if (needObs) {
            switch (encryptionType) {
                case OBConstant.PackType.ORIGINAL_ENCRYPTED:
                    if (isUpdate) {
                        System.arraycopy(updatedefault, 0, goal, 0, 4);
                    } else {
                        System.arraycopy(obsdefault, 0, goal, 0, 4);
                    }
                    break;
                case OBConstant.PackType.ACTIVATED_UNENCRYPTED:
                    if (isUpdate) {
                        System.arraycopy(updatedefault, 0, goal, 0, 4);
                    } else {
                        System.arraycopy(obsmodify, 0, goal, 0, 4);
                    }
                    break;

            }
        } else {
            System.arraycopy(normal, 0, goal, 0, 4);
        }
        System.arraycopy(res, 0, goal, 4, 62);
        goal[66] = (byte) (crc >> 8);
        goal[67] = (byte) crc;
        System.arraycopy(goal, 4, cache, 0, 64);
        //是否使用加密算法
        if (needObs) {
            if (encryptionType == OBConstant.PackType.ACTIVATED_UNENCRYPTED) {
                obs(cache, key);
            } else if (encryptionType == OBConstant.PackType.ORIGINAL_ENCRYPTED) {
                obs(cache, def);
            }
        }
        System.arraycopy(cache, 0, goal, 4, 64);
        return goal;
    }

    private native void ts(byte[] result,byte[] key);
    /**
     * @param result 加密数据
     */
    private  void obs(byte[] result, byte[] key) {
        if (!NEED_OBS) {
            return;
        }
        ts(result,key);
    }


    private native int tsSum(byte[] pBuffer,int length);
    /**
     * crc校验
     * * @return 返回byte数组的crc校验值
     */
    private  int CRC16(byte[] pBuffer, int length) {
        return tsSum(pBuffer,length);
    }
}