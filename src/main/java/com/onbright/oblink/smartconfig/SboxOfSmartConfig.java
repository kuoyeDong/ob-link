package com.onbright.oblink.smartconfig;

/**
 * 使用于smartconfig的加密类,加密部分数据以16字节为单位，不足部分补充0
 * Created by Adolf_Dong on 2018/3/15.
 */

public class SboxOfSmartConfig {
    private static byte[] key = new byte[]{0x38,0x38,0x38,0x38,0x38,0x38,0x38,0x38};
    private static final int[] sbox = {0x13, 0x51, 0x24, 0x67, 0xf1, 0xa9, 0x4b, 0x9c, 0xc8, 0x74, 0x62, 0x3d, 0xd0, 0x81, 0x7a, 0xe0};

    public static final boolean OBS = false;

    /**
     * @param result 加密数据
     */
    private static void obs(byte[] result, byte[] key) {
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 8; i++) {
                int value = key[i] & 0xff;
                int high = (value >> 4);
                int low = value & 0x0f;
                int highGoal = sbox[high] ^ result[2 * i + 16 * j];
                int lowGoal = sbox[low] ^ result[2 * i + 16 * j + 1];
                result[2 * i + 16 * j] = (byte) highGoal;
                result[2 * i + 16 * j + 1] = (byte) lowGoal;
            }
        }
    }

    /**
     * 加密数据
     *
     * @param bytes 待加密数据
     * @return 加密后的数据
     */
    public byte[] pack(byte[] bytes) {
//        byte[] compatiableBytes = getCompatiableBytes(bytes);
//        return compatiableBytes;
        byte[] goalBytes = new byte[bytes.length + 4];
        byte[] headBytes = "++++".getBytes();
        System.arraycopy(headBytes,0,goalBytes,0,headBytes.length);
        System.arraycopy(bytes,0,goalBytes,4,bytes.length);
        return goalBytes;
    }

    /**
     * 解密数据,加密部分数据以16字节为单位，不足部分补充0
     *
     * @param bytes 待解密数据，有全加密和除去前导码部分加密的区分
     * @return 解密后数据
     */
    public byte[] unPack(byte[] bytes) {
//        if (Transformation.byteArryToHexString(bytes).substring(0, 4).equals("++++")) {
//            byte[] compatiableBytes = getCompatiableBytes(bytes);
//            byte[] result = new byte[64];
//            System.arraycopy(bytes, 4, result, 0, 64);
////            obs();
//        } else {
//
//        }
        return bytes;
    }


    /**
     * 加解密算法以16字节为单位，不足部分补0，前导码为四个字节，
     *
     * @param bytes 原始数据数组
     * @return 补充后数组
     */
    private byte[] getCompatiableBytes(byte[] bytes) {
        int plus = bytes.length % 16;
        if (plus == 0) {
            return bytes;
        } else {
            byte[] plusBytes = new byte[bytes.length + 16 - plus];
            System.arraycopy(bytes, 0, plusBytes, 0, bytes.length);
            return plusBytes;
        }
    }


}
