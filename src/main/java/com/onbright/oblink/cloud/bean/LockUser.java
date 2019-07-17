package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * use by:门锁用户
 * create by dky at 2019/7/17
 */
public class LockUser implements Serializable {
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 用户身份
     * <p>
     * admin,
     * user,
     * guest
     */
    private int identity;

    /**
     * 电话号码
     */
    private String mobile;
    /**
     * pin码
     */
    private String pin;


    /**
     * 是否存在胁迫密码
     * 0 no exist,1 exist
     */
    private int exist;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    /**
     * 是否有设置胁迫指纹或者密码
     *
     * @return 有返回true
     */
    public boolean hasStressPwd() {
        return exist != 0;
    }
}
