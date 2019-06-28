package com.onbright.oblink;


import android.annotation.SuppressLint;
import android.util.Base64;

import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.bean.Groups;
import com.onbright.oblink.local.bean.EnvironmentSensor;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.net.OBConstant;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * 算法工具
 * Created by adolf_dong on 2019/6/28.
 */
public class MathUtil {
    /**
     * 获得数组中的有效数据
     *
     * @param arry 源数组
     * @return 去掉无用位置的有效数组
     */
    public static byte[] validArray(byte[] arry) {
        String cache = null;
        try {
            cache = new String(arry, OBConstant.StringKey.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] goal = null;
        try {
            if (cache != null) {
                goal = cache.trim().getBytes(OBConstant.StringKey.UTF8);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return goal;
    }

    /**
     * 获取字节数据的去符号位值
     */
    public static int validByte(byte src) {
        return src & 0xff;
    }

    /**
     * 获取字节中的特定一个位值
     *
     * @param src   byte
     * @param index 0-7 取第几个位
     * @return 第index位的值  0 1
     */
    public static int byteIndexValid(byte src, int index) {
        return ((src & 0xff) >> index) & 0x01;
    }

    /**
     * 从直接的startPos开始取len长度的bit的值
     *
     * @param src      字节
     * @param startPos 开始位置，注意bit7-bit0的bit顺序
     * @param len      取的长度
     */
    public static int byteIndexValid(byte src, int startPos, int len) {
        if (startPos + len > 8) {
            throw new IndexOutOfBoundsException();
        }
        int srcInt = src & 0xff;
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum += ((srcInt >> (startPos + i)) & 0x01) << i;
        }
        return sum;
    }

    /**
     * 判断字节数组值是否为0
     */
    public static boolean byteArrayIsZero(byte[] src) {
        for (byte aSrc : src) {
            if (aSrc != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字节数组值是否全为ff
     */
    public static boolean byteArrayIsff(byte[] src, int start, int len) {
        for (int i = 0; i < len; i++) {
            byte aSrc = src[i + start];
            if ((aSrc & 0xff) != 0xff) {
                return false;
            }
        }
        return true;
    }

    /**
     * 单节点寻找组地址
     *
     * @param obGroups   组列表
     * @param obNodes    单节点列表
     * @param wantRemove 是否需要从单节点数据中删除，并添加到obGroup中,建议为false
     */
    public static void nodeFindGroup(List<ObGroup> obGroups, List<ObNode> obNodes, boolean wantRemove) {
        for (int i = 0; i < obGroups.size(); i++) {
            ObGroup obGroup = obGroups.get(i);
            List<Integer> integers = obGroup.getIndexList();
            for (int j = 0; j < obNodes.size(); j++) {
                ObNode obNode = obNodes.get(j);
                obGroup.setRfAddrs(obNode.getRfAddr());
                int addr = validByte(obNode.getAddr());
                if (integers.contains(addr)) {
                    obNode.setGroupAddr(obGroup.getAddr());
                    obGroup.getObNodes().add(obNode);
                    if (wantRemove) {
                        obNodes.remove(obNode);
                    }
                }
            }
        }

    }

    /**
     * 改变某个字节的某个位
     *
     * @param src    目标字节
     * @param dst    要改变的位 0-7
     * @param isTure 1为true
     * @return 目标位置
     */
    public static byte setBitIndex(byte src, int dst, boolean isTure) {
        if (isTure) {
            src |= (1 << dst);
        } else {
            src &= ~(1 << dst);
        }
        return src;
    }

    /**
     * 反转字节的某个bit
     *
     * @param src 字节
     * @param dst 目标位置
     */
    public static byte toggleBitIndex(byte src, int dst) {
        src ^= (1 << dst);
        return src;
    }

    /**
     * 设定连续bit的数值
     *
     * @param src   原字节
     * @param start 起始位置
     * @param len   从起始位置开始的长度
     * @param value 具体数值,value过大则从低位开始取
     */
    public static byte setMultiBitIndex(byte src, int start, int len, int value) {
        for (int i = 0; i < len; i++) {
            if (((value >> i) & 1) == 1) {
                src |= (1 << start + i);
            } else {
                src &= ~(1 << start + i);
            }
        }
        return src;
    }

    /**
     * 计算环境传感器的某个位置的值
     *
     * @param index 目标位置
     * @param src   环境传感器的12字节状态
     * @return 计算后用于显示的数值
     */
    @SuppressLint("DefaultLocale")
    public static String getEvIndexStr(int index, byte[] src) {
        String showStr = null;
        int code = MathUtil.byteIndexValid(src[index], 0, 4) << 8 + MathUtil.validByte(src[index + 1]);
        switch (index) {
            case EnvironmentSensor.FORMALDEHYDE:
                showStr = String.format("%.3f", code * 0.001233 * 0.4);
                break;
            case EnvironmentSensor.PM:
                return String.valueOf(code * 2);
            case EnvironmentSensor.CO:
                return String.valueOf(code * 658);
            case EnvironmentSensor.TEMP:
                showStr = String.format("%.1f", 0.0429 * code - 46.85);
                break;
            case EnvironmentSensor.HUMI:
                return String.valueOf((int) (0.0305 * code - 6));
            case EnvironmentSensor.CO2:
                return String.valueOf(code * 4);
        }
        return showStr;
    }

    public static double getEvIndexValue(int index, byte[] src) {
        int code = MathUtil.byteIndexValid(src[index], 0, 4) + MathUtil.validByte(src[index + 1]);
        switch (index) {
            case EnvironmentSensor.FORMALDEHYDE:
                return code * 0.001233 * 0.4;
            case EnvironmentSensor.PM:
                return code * 2;
            case EnvironmentSensor.CO:
                return code * 658;
            case EnvironmentSensor.TEMP:
                return 0.0429 * code - 46.85;
            case EnvironmentSensor.HUMI:
                return 0.0305 * code - 6;
            case EnvironmentSensor.CO2:
                return code * 4;
        }
        return 0;
    }

    /**
     * 判断设备是否可以加入到组
     *
     * @param dev            要判断设备
     * @param groupMember    当前组内节点成员
     * @param group_style    组类型，服务器组或者通过服务器下发到本地组
     * @param obox_serial_id obox序列号
     * @param groupses       待判断组数据总集
     * @return 可以添加则返回true
     */
    public static boolean isCan(DeviceConfig dev, List<DeviceConfig> groupMember, String group_style,
                                String obox_serial_id, List<Groups> groupses) {
        boolean can = true;
        for (int j = 0; j < groupMember.size(); j++) {
            DeviceConfig decCache = groupMember.get(j);
            if (decCache.getSerialId().equals(dev.getSerialId())) {
                can = false;
                break;
            }
        }
        /*如果是服务器下发到本地组则还则需要判断
        1.此节点是否属于该obox
        2.此节点是否在任何当前obox的本地组内*/
        if (can && group_style.equals("00")) {
            if (!dev.getObox_serial_id().equals(obox_serial_id)) {
                can = false;
            }
            if (can) {
                for (int i = 0; i < groupses.size(); i++) {
                    Groups groups = groupses.get(i);
                    if (groups.getGroup_style() != null
                            && groups.getGroup_style().equals("00")
                            ) {
                        List<DeviceConfig> deviceConfigs = groups.getGroup_member();
                        if (deviceConfigs != null) {
                            for (int j = 0; j < deviceConfigs.size(); j++) {
                                DeviceConfig deviceConfig = deviceConfigs.get(j);
                                if (deviceConfig.getSerialId().equals(dev.getSerialId())) {
                                    can = false;
                                    break;
                                }
                            }
                        }
                    }
                }

            }
        }
        return can;
    }


}
