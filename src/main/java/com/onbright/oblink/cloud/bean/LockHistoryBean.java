package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * use by:开门记录单元的数据元，注意其中有包括删除用户
 * create by dky at 2019/7/18
 */
public class LockHistoryBean implements Serializable {
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 记录类型
     * 1,密码开锁
     * 2,指纹开锁
     * 3,卡开锁
     * 4,钥匙开锁
     * 5,遥控开锁
     * 6,远程授权开锁
     * 7,修改密码
     * 8,删除用户
     * 9,锁关闭
     * 10,注册成功
     */
    private int operation;
    /**
     * 时间戳
     */
    private String openTime;

    /**
     * 时间戳
     */
    private long timeStamp;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
