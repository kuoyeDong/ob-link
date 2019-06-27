package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * 门锁推送键值对
 */
public class LockPush implements Serializable {


    /**
     * 序列号
     */
    private String serialId;
    /**
     * jimmy(撬门) 1，
     * stress 2,
     * multiple_validation_failed 3，
     * overdoor 4,
     * back_lock 5，
     * low_betty 6
     */
    private int value;
    /**
     * 0 close,1 open
     */
    private int enable;

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

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
