package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.bean.LockPush;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;

import java.util.List;

/**
 * use by:智能酒店门锁处理，用户、临时用户、状态
 * create by dky at 2019/7/5
 */
public abstract class SmartLockHotelHandler extends DeviceHandler {
    /**
     * 门锁状态枚举
     */
    public enum LockStatus {

    }

    public SmartLockHotelHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.HOTEL_LOCK;
    }

    /**
     * 添加门锁临时用户
     *
     * @param serialId  门锁序列号
     * @param authToken 门锁口令
     * @param nickName  昵称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param times     使用次数
     */
    public void addIntelligentRemoteUser(String serialId, String authToken, String nickName,
                                         String startTime, String endTime, String times, boolean isMax) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_INTELLIGENT_REMOTE_USER,
                GetParameter.addIntelligentRemoteUser(serialId, authToken, nickName,
                        startTime, endTime, times, null, false, isMax), CloudConstant.Source.CONSUMER_OPEN + "intelligentRemoteUser", HttpRequst.POST);
    }

    /**
     * 修改门锁临时用户
     *
     * @param serialId  门锁序列号
     * @param authToken 门锁口令
     * @param nickName  昵称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param times     使用次数
     */
    public void modifyIntelligentRemoteUser(int userId, String serialId, String userPin, String authToken, String nickName,
                                            String startTime, String endTime, String times, boolean isMax) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.MODIFY_INTELLIGENT_REMOTE_USER,
                GetParameter.modifyIntelligentRemoteUser(userId, serialId, userPin, authToken, null, nickName,
                        startTime, endTime, times, isMax), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询门锁状态
     */
    public void queryIntelligentFingerhome() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_FINGERHOME, GetParameter.queryIntelligentFingerhome(deviceSerId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询OB智能门锁开门记录
     */
    public void queryIntelligentOpenrecord() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_OPENRECORD,
                GetParameter.queryIntelligentOpenrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询门锁警告记录
     */
    public void queryIntelligentWarningrecord() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_WARNINGRECORD,
                GetParameter.queryIntelligentWarningrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询OB智能门锁用户列表
     */
    public void queryIntelligentUseringrecord() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_USERINGRECORD,
                GetParameter.queryIntelligentUseringrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 发送验证码到胁迫时目标手机
     *
     * @param pin
     * @param phone
     */
    public void sendIntelligentValidatecode(String pin, String phone) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEND_INTELLIGENT_VALIDATECODE,
                GetParameter.sendIntelligentValidatecode(deviceSerId, pin, phone), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 编辑门锁用户
     *
     * @param pin
     * @param nickName
     * @param phone
     * @param validateCode
     * @param hasStressPwd
     */
    public void editIntelligentUser(String pin, String nickName, String phone, String validateCode, boolean hasStressPwd) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.EDIT_INTELLIGENT_USER,
                GetParameter.editIntelligentUser(deviceSerId, pin, nickName, phone, validateCode, hasStressPwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }


    /**
     * 智能门锁验证权限密码
     *
     * @param verficationPwd 门锁密码
     */
    public void queryIntelligentAuthpwd(String verficationPwd) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_AUTHPWD,
                GetParameter.queryIntelligentAuthpwd(deviceSerId, verficationPwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询门锁临时用户
     *
     * @param authToken 门锁token
     */
    public void queryIntelligentRemoteUnlocking(String authToken) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_REMOTE_UNLOCKING,
                GetParameter.queryIntelligentRemoteUnlocking(deviceSerId, authToken), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 删除临时门锁用户
     *
     * @param id
     * @param authToken
     * @param pin
     */
    public void delIntelligentRemoteUser(int id, String authToken, String pin) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DEL_INTELLIGENT_REMOTE_USER,
                GetParameter.delIntelligentRemoteUser(id, deviceSerId, authToken, pin), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 发送密码给临时用户
     *
     * @param pin
     * @param authToken
     * @param mobile
     */
    public void sendRemotePwd(String pin, String authToken, String mobile) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEND_REMOTE_PWD,
                GetParameter.sendRemotePwd(deviceSerId, pin, authToken, mobile), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 智能门锁忘记权限密码
     */
    public void forgetIntelligentPwd() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.FORGET_INTELLIGENT_PWD,
                GetParameter.forgetIntelligentPwd(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 智能门锁根据推送重置权限密码
     *
     * @param pwd 密码
     */
    public void resetIntelligentPwdByCode(String pwd) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.RESET_INTELLIGENT_PWD_BY_CODE,
                GetParameter.resetIntelligentPwdByCode(deviceSerId, pwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 智能门锁修改权限密码
     *
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     */
    public void resetIntelligentPwd(String oldPwd, String newPwd) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.RESET_INTELLIGENT_PWD,
                GetParameter.resetIntelligentPwd(deviceSerId, oldPwd, newPwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询推送设置列表
     */
    public void queryIntelligentPushList() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_PUSH_LIST,
                GetParameter.queryIntelligentPushList(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 修改推送设置
     *
     * @param mobile     电话
     * @param lockPushes 推送数据集合
     */
    public void modifyIntelligentPush(String mobile, List<LockPush> lockPushes) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.MODIFY_INTELLIGENT_PUSH,
                GetParameter.modifyIntelligentPush(deviceSerId, mobile, lockPushes), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 门锁创建权限密码
     *
     * @param pwd 权限密码
     */
    public void addIntelligentAuthpwd(String pwd) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_INTELLIGENT_AUTHPWD,
                GetParameter.addIntelligentAuthpwd(deviceSerId, pwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    @Override
    public void onSuccess(String action, String json) {
        super.onSuccess(action, json);
        switch (action) {
        }
    }
}
