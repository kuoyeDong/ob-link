package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * use by:报警记录单元的数据元
 * create by dky at 2019/7/18
 */
public class LockAlarmBean implements Serializable {
    /**
     * 警告类型
     * 1.撬门
     * 2.胁迫开锁
     * 3.多次验证失败
     * 4.虚掩超过十秒
     * 5.反锁
     * 6.低电量
     * 7.反锁解除
     */
    private int operation;
    /**
     * 警告时间
     */
    private String warnTime;

    /**
     * 时间戳
     */
    private long timeStamp;

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getWarnTime() {
        return warnTime;
    }

    public void setWarnTime(String warnTime) {
        this.warnTime = warnTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
