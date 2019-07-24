package com.onbright.oblink.cloud.bean;

import java.io.Serializable;

/**
 * use by:门锁临时用户
 * create by dky at 2019/7/17
 */
public class LockTempUser implements Serializable {
    /**
     * 临时用户id
     */
    private int id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 归属的门锁序列号
     */
    private String serialId;
    /**
     * 开始时间yyyy-MM-dd HH:mm:ss
     */
    private String start;
    /**
     * pin码
     */
    private String pin;
    /**
     * 结束时间yyyy-MM-dd HH:mm:ss
     */
    private String end;
    /**
     * 0 start,1 end,-1 no start
     */
    private int isEnd;

    /**
     * 推送手机号码
     */
    private String mobile;


    /**
     * 剩余时间，单位分
     */
    private String timeLeft;
    /**
     * 临时密码
     */
    private String pwd;
    /**
     * 可用次数
     */
    private int times;
    /**
     * 已使用次数
     */
    private int useTimes;

    /**
     * 是否无限次数，服务器定义是int，不知所以
     * 不是无数次 0，  是无数次 1
     */
    private int isMax;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(int isEnd) {
        this.isEnd = isEnd;
    }

    public int getTimes() {
        return times;
    }

    /**
     * 获取次数的显示
     *
     * @return
     */
    public String getShowTimes() {
        return times + "";
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(int useTimes) {
        this.useTimes = useTimes;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getIsMax() {
        return isMax;
    }

    public void setIsMax(int isMax) {
        this.isMax = isMax;
    }

    /**
     * @return 显示有效时间
     */
    public String getShowTime() {
        return start + "-" + end;
    }

    /**
     * 获取剩余次数
     */
    public String getLoseTime() {
        return (times - useTimes) + "";
    }

    /**
     * 获取显示剩余时间
     *
     * @return 剩余时间
     */
    public String getShowTimeLeft() {
        return timeLeft + "Min";
    }

    /**
     * @return 有效返回true
     */
    public boolean isEffective() {
        return isEnd != 1;
    }
}
