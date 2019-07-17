package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.bean.LockPush;
import com.onbright.oblink.cloud.bean.LockStatus;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.local.net.Transformation;

import java.util.List;

/**
 * use by:智能酒店门锁处理，用户、临时用户、状态
 * create by dky at 2019/7/5
 */
public abstract class SmartLockHotelHandler extends DeviceHandler {

    /**
     * 是否有权限密码
     */
    private boolean isAuth;

    /**
     * 门锁通讯口令
     */
    private String authToken;

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
                                         String startTime, String endTime, String times) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_INTELLIGENT_REMOTE_USER,
                GetParameter.addIntelligentRemoteUser(serialId, authToken, nickName,
                        startTime, endTime, times, null, false, false), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
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
     * 查询门锁状态,成功后必然回调{@link #onStatusChange(String)}，可能回调{@link #batteryValue(int)}
     */
    public void queryIntelligentFingerhome() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_FINGERHOME, GetParameter.queryIntelligentFingerhome(deviceSerId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询OB智能门锁开门记录
     *
     * @param openRecordLsn
     */
    private void queryIntelligentOpenrecord(OpenRecordLsn openRecordLsn) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_OPENRECORD,
                GetParameter.queryIntelligentOpenrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询门锁警告记录
     */
    private void queryIntelligentWarningRecord() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_WARNINGRECORD,
                GetParameter.queryIntelligentWarningrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询OB智能门锁用户列表
     */
    public void queryIntelligentUseringRecord() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_USERINGRECORD,
                GetParameter.queryIntelligentUseringrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 发送验证码到胁迫时目标手机，此方法用于设定短信接受人时，获得接受人许可
     *
     * @param pin
     * @param phone
     */
    public void sendIntelligentValidateCode(String pin, String phone) {
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
    public void queryIntelligentAuthPwd(String verficationPwd) {
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
    public void addIntelligentAuthPwd(String pwd) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_INTELLIGENT_AUTHPWD,
                GetParameter.addIntelligentAuthpwd(deviceSerId, pwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    @Override
    public void onSuccess(String action, String json) {
        super.onSuccess(action, json);
        switch (action) {
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_FINGERHOME:
                Gson gson = new Gson();
                LockStatus lockStatus = gson.fromJson(json, LockStatus.class);
                isAuth = lockStatus.getIsAuth() > 0;
                freshlockStatusWithType((byte) lockStatus.getType());
                int betty = lockStatus.getBetty();
                showBattery(betty);
                break;
        }
    }

    /**
     * 开门上报
     */
    public static final int OPEN = 0xc3;
    /**
     * 刷卡开锁
     */
    public static final int CARD = 0xcd;

    /**
     * 关门上报
     */
    public static final int CLOSED = 0xc6;

    /**
     * 门锁状态变化
     *
     * @param lockStatusEnum {@link LockStatusEnum}
     */
    protected abstract void lockStatusChange(LockStatusEnum lockStatusEnum);

    @Override
    protected void onStatusChange(String status) {
        byte[] statusBytes = Transformation.hexString2Bytes(status);
        byte bettyByte = statusBytes[0];
        int betty = MathUtil.byteIndexValid(bettyByte, 0, 7);
        showBattery(betty);
        int cmd = MathUtil.validByte(statusBytes[1]);
        if (cmd == OPEN) {
            switch (statusBytes[5]) {
                case 0:
                    lockStatusChange(LockStatusEnum.fingerprint);
                    break;
                case 1:
                    lockStatusChange(LockStatusEnum.pwd);
                    break;
                case 2:
                    lockStatusChange(LockStatusEnum.card);
                    break;
                case 3:
                    lockStatusChange(LockStatusEnum.key_open_lock);
                    break;
                case 4:
                    lockStatusChange(LockStatusEnum.telecontrol);
                    break;
                case 5:
                    lockStatusChange(LockStatusEnum.temp_lock_user_open);
                    break;
            }
        } else if (cmd == CARD) {
            lockStatusChange(LockStatusEnum.card);
        } else if (cmd == CLOSED) {
            freshlockStatusWithType(statusBytes[2]);
        }
    }

    /**
     * 解析锁状态
     *
     * @param statusByte 状态字节
     */
    private void freshlockStatusWithType(byte statusByte) {
        switch (statusByte) {
            case 4:
                lockStatusChange(LockStatusEnum.back_lock);
                break;
            case 5:
                lockStatusChange(LockStatusEnum.lock_close);
                break;
            case 7:
                lockStatusChange(LockStatusEnum.overdoor_just);
                break;
            case 8:
                lockStatusChange(LockStatusEnum.lock_open);
                break;
            case 9:
                lockStatusChange(LockStatusEnum.back_lock_release);
                break;
        }
    }

    /**
     * 获取到电量值
     *
     * @param batteryValue 电量数据
     */
    private void showBattery(int batteryValue) {
        if (batteryValue <= 100) {
            batteryValue(batteryValue);
        }
    }

    /**
     * 获取到电量
     *
     * @param batteryValue 剩余电池百分比
     */
    protected abstract void batteryValue(int batteryValue);

    /**
     * 门锁状态枚举
     */
    public enum LockStatusEnum {

        /**
         * 指纹开锁
         */
        fingerprint,
        /**
         * 密码开锁
         */
        pwd,
        /**
         * 卡开锁
         */
        card,
        /**
         * 钥匙开锁
         */
        key_open_lock,
        /**
         * 遥控开锁
         */
        telecontrol,
        /**
         * 临时用户开锁
         */
        temp_lock_user_open,
        /**
         * 反锁
         */
        back_lock,
        /**
         * 关门
         */
        lock_close,
        /**
         * 虚掩
         */
        overdoor_just,
        /**
         * 开门
         */
        lock_open,
        /**
         * 反锁解除
         */
        back_lock_release
    }

    private OpenRecordLsn mOpenRecordLsn;

    /**
     * 查询OB智能门锁开门记录回调接口
     */
    public interface OpenRecordLsn {
        void openRecordLoad();
    }
}
