package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * use by:智能门锁的状态
 * create by dky at 2019/7/17
 */
public class LockStatus implements Serializable {

    private int betty;

    private boolean onLine;

    /**
     * 4,5关门，7虚掩，8开门，9
     */
    private int type;
    /**
     * 权限密码， 0不存在，大于0存在
     */
    private int isAuth;

    public int getBetty() {
        return betty;
    }

    public void setBetty(int betty) {
        this.betty = betty;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIsAuth() {
        return isAuth;
    }

    public void setIsAuth(int isAuth) {
        this.isAuth = isAuth;
    }
}
