package com.onbright.oblink.local.net;

/**
 * 数据类型转换工具
 * Created by adolf_dong on 2016/1/20.
 */
public class Transformation {


    /**
     * byte数组转为十六进制字符串
     *
     * @return 十六进制字符串
     */
    public static String byteArryToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 字节转16进制
     *
     * @param src 要转换的字节
     * @return 对应的十六进制字符
     */
    public static String byte2HexString(byte src) {
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src & 0xFF;
        String hv = Integer.toHexString(v);
        // FIXME: 2016/7/21 不补0
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    /**
     * 取目标字节后4位转换成16进制String
     *
     * @param src 目标字节
     * @return 字节低4位生成的字符串
     */
    public static String halfByte2HexString(byte src) {
        StringBuilder stringBuilder = new StringBuilder("");
        int v = src & 0x0F;
        String hv = Integer.toHexString(v);
        if (hv.length() < 2) {
            stringBuilder.append(0);
        }
        stringBuilder.append(hv);
        return stringBuilder.toString();
    }

    /**
     * 返回十六进制字符串的字节数组
     *
     * @param hexStr 十六进制字符串
     * @return 字节数组
     */

    public static byte[] hexString2Bytes(String hexStr) {
        int l = hexStr.length() / 2 + hexStr.length() % 2;
        byte[] bytes = new byte[l];
        for (int i = 0; i < bytes.length; i++) {
            int value;
            if (2 * (i + 1) < hexStr.length()) {
                value = Integer.valueOf(hexStr.substring(2 * i, 2 * (i + 1)), 16);
            } else {
                value = Integer.valueOf(hexStr.substring(2 * i), 16);
            }
            bytes[i] = (byte) value;
        }
        return bytes;
    }


}
