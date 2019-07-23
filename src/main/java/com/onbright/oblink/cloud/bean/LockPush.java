package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * 门锁推送键值对
 */
public class LockPush implements Serializable {

    /**
     * 1.撬门
     * 3.多次验证失败，
     * 4.门虚掩
     * 5.反锁
     * 6.低电量
     */
    private int value;
    /**
     * 是否开启此value相关推送
     * 0 关闭,
     * 1 开启
     */
    private int enable;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
