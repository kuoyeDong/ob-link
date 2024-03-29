package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.EventMsg;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.bean.LockAlarm;
import com.onbright.oblink.cloud.bean.LockHistory;
import com.onbright.oblink.cloud.bean.LockPush;
import com.onbright.oblink.cloud.bean.LockStatus;
import com.onbright.oblink.cloud.bean.LockTempUser;
import com.onbright.oblink.cloud.bean.LockUser;
import com.onbright.oblink.cloud.handler.basehandler.BatteryDevice;
import com.onbright.oblink.cloud.handler.basehandler.UnControllableRfDeviceHandler;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 智能家居门锁处理，用户、临时用户、状态
 *
 * @author dky
 * 2019/7/5
 */
public abstract class SmartLockHouseHandler extends UnControllableRfDeviceHandler implements BatteryDevice {
    /**
     * 开门上报
     */
    private static final int OPEN = 0xc3;
    /**
     * 刷卡开锁
     */
    private static final int CARD = 0xcd;

    /**
     * 关门上报
     */
    private static final int CLOSED = 0xc6;
    /**
     * 是否有权限密码
     */
    private boolean hasAdminPwd;

    /**
     * 门锁通讯口令
     */
    private String mAuthToken;

    private Gson gson = new Gson();

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected SmartLockHouseHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.HOUSE_LOCK;
    }

    public boolean isHasAdminPwd() {
        return hasAdminPwd;
    }

    public void setHasAdminPwd(boolean hasAdminPwd) {
        this.hasAdminPwd = hasAdminPwd;
    }

    /**
     * 查询门锁状态,成功后必然回调{@link #lockStatusChange(LockStatusEnum)}，可能回调{@link #batteryValue(int)}，此方法可查询门锁有无设置权限密码的真实状态
     */
    public void queryLockStatus() {
        if (isNoSerId()) {
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_FINGERHOME, GetParameter.queryIntelligentFingerhome(deviceSerId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询用户列表
     *
     * @param queryUserLsn 回调
     */
    public void queryUser(QueryUserLsn queryUserLsn) {
        if (isNoSerId()) {
            return;
        }
        mQueryUserLsn = queryUserLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_USERINGRECORD,
                GetParameter.queryIntelligentUseringrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private QueryUserLsn mQueryUserLsn;

    /**
     * 查询用户回调接口
     */
    public interface QueryUserLsn {
        void userRecordLoad(List<LockUser> lockUsers);
    }

    /**
     * 发送验证码到胁迫时目标手机，此方法用于设定短信接受人时，获得接受人许可,(要使用此功能首先要在门锁设置用户胁迫指纹)
     * 并且，获得验证码后还需要调用{@link #modifyUser(LockUser, String, ModifyUserLsn)}完成推送手机的绑定
     *
     * @param lockUser    用户
     * @param phone       手机号
     * @param sendCodeLsn 回调
     */
    public void sendValidateCode(LockUser lockUser, String phone, SendCodeLsn sendCodeLsn) {
        if (isNoSerId()) {
            return;
        }
        mSendCodeLsn = sendCodeLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEND_INTELLIGENT_VALIDATECODE,
                GetParameter.sendIntelligentValidatecode(deviceSerId, lockUser.getPin(), phone), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private SendCodeLsn mSendCodeLsn;

    /**
     * 发送验证码到手机回调接口
     */
    public interface SendCodeLsn {
        /**
         * 发送验证码成功
         */
        void sendCodeOk();
    }

    /**
     * 修改用户，在未在门锁端设置胁迫指纹的情况下，传入的电话将无效
     *
     * @param lockUser      用户
     * @param validateCode  验证码，如无胁迫指纹可为null
     * @param modifyUserLsn 回调
     */
    public void modifyUser(LockUser lockUser, String validateCode, ModifyUserLsn modifyUserLsn) {
        if (isNoSerId()) {
            return;
        }
        mModifyUserLsn = modifyUserLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.EDIT_INTELLIGENT_USER,
                GetParameter.editIntelligentUser(deviceSerId, lockUser.getPin(), lockUser.getNickName(), lockUser.getMobile(), validateCode,
                        lockUser.hasStressPwd()), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private ModifyUserLsn mModifyUserLsn;

    /**
     * 修改用户回调接口
     */
    public interface ModifyUserLsn {
        void modifyUserOk();
    }


    /**
     * 在不合时宜的步骤调用权限密码相关接口出错的回调接口
     */
    private interface AdminPwdError {
        /**
         * 没有权限密码，请先创建权限密码
         */
        void noAdminPwd();

    }

    /**
     * 验证权限密码(要操作临时用户，必须验证权限密码，
     * 如没有在权限密码则此方法不会执行任何操作，请使用创建权限密码方法{@link #createAdminPwd(String, CreatAuthPwdLsn)})
     *
     * @param pwd             权限密码
     * @param queryAuthPwdLsn 回调
     */
    public void validateAdminPwd(String pwd, ValidateAdminPwdLsn queryAuthPwdLsn) {
        if (isNoSerId()) {
            return;
        }
        mValidateAdminPwdLsn = queryAuthPwdLsn;
        if (!hasAdminPwd) {
            mValidateAdminPwdLsn.noAdminPwd();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_AUTHPWD,
                GetParameter.queryIntelligentAuthpwd(deviceSerId, pwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private ValidateAdminPwdLsn mValidateAdminPwdLsn;

    /**
     * 验证权限密码回调接口
     */
    public interface ValidateAdminPwdLsn extends AdminPwdError {
        void validateAdminPwdOk();
    }

    /**
     * 创建权限密码
     *
     * @param adminPwd        权限密码,此密码请自行记录，不会在回调中回传
     * @param creatAuthPwdLsn 回调
     */
    public void createAdminPwd(String adminPwd, CreatAuthPwdLsn creatAuthPwdLsn) {
        if (isNoSerId()) {
            return;
        }
        mCreatAuthPwdLsn = creatAuthPwdLsn;
        if (hasAdminPwd) {
            mCreatAuthPwdLsn.areadyHasAdminPwd();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_INTELLIGENT_AUTHPWD,
                GetParameter.addIntelligentAuthpwd(deviceSerId, adminPwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private CreatAuthPwdLsn mCreatAuthPwdLsn;

    /**
     * 创建权限密码回调接口
     */
    public interface CreatAuthPwdLsn {
        /**
         * 创建权限密码成功
         */
        void creatAdminPwdOk();

        /**
         * 已经有权限密码，不能重复创建
         */
        void areadyHasAdminPwd();
    }

    /**
     * 根据推送重置权限密码
     *
     * @param pwd       密码
     * @param uniqueKey uniqueKey 下级用户唯一标识
     */
    public void resetAdminPwdByCode(String pwd, String uniqueKey, ResetPwdLsn resetPwdLsn) {
        if (isNoSerId()) {
            return;
        }
        mResetPwdLsn = resetPwdLsn;
        if (!hasAdminPwd) {
            mResetPwdLsn.noAdminPwd();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.RESET_INTELLIGENT_PWD_BY_CODE,
                GetParameter.resetIntelligentPwdByCode(deviceSerId, pwd, uniqueKey), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private ResetPwdLsn mResetPwdLsn;

    /**
     * 重置权限密码回调
     */
    public interface ResetPwdLsn extends AdminPwdError {

        /**
         * 等待门锁操作
         */
        void waitLockReset();

        /**
         * 重置权限密码成功
         */
        void resetPwdOk();
    }

    /**
     * 修改权限密码
     *
     * @param oldPwd       旧密码
     * @param newPwd       新密码
     * @param modifyPwdLsn 回调
     */
    public void modifyAdminPwd(String oldPwd, String newPwd, ModifyPwdLsn modifyPwdLsn) {
        if (isNoSerId()) {
            return;
        }
        mModifyPwdLsn = modifyPwdLsn;
        if (!hasAdminPwd) {
            mModifyPwdLsn.noAdminPwd();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.RESET_INTELLIGENT_PWD,
                GetParameter.resetIntelligentPwd(deviceSerId, oldPwd, newPwd), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private ModifyPwdLsn mModifyPwdLsn;

    /**
     * 修改权限密码回调
     */
    public interface ModifyPwdLsn extends AdminPwdError {
        void modifyPwdOk();
    }

    /**
     * 未验证权限密码获取token就进行token必须参数临时用户相关接口操作回调接口
     */
    private interface AuthTokenError {
        /**
         * 没有进行验证权限密码，不能进行临时用户相关接口操作
         */
        void noAuthToken();
    }

    /**
     * 查询临时用户
     *
     * @param queryTemporaryUserLsn 回调
     */
    public void queryTemporaryUser(QueryTemporaryUserLsn queryTemporaryUserLsn) {
        if (isNoSerId()) {
            return;
        }
        mQueryTemporaryUserLsn = queryTemporaryUserLsn;
        if (mAuthToken == null) {
            mQueryTemporaryUserLsn.noAuthToken();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_REMOTE_UNLOCKING,
                GetParameter.queryIntelligentRemoteUnlocking(deviceSerId, mAuthToken), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private QueryTemporaryUserLsn mQueryTemporaryUserLsn;

    /**
     * 查询临时用户回调
     */
    public interface QueryTemporaryUserLsn extends AuthTokenError {
        /**
         * @param lockTempUsers 临时用户list
         */
        void queryTemporaryUserOk(List<LockTempUser> lockTempUsers);
    }

    /**
     * 添加临时用户
     *
     * @param lockTempUser        要添加的临时用户
     * @param addTemporaryUserLsn 回调
     */
    public void addTemporaryUser(LockTempUser lockTempUser, AddTemporaryUserLsn addTemporaryUserLsn) {
        if (isNoSerId()) {
            return;
        }
        mAddTemporaryUserLsn = addTemporaryUserLsn;
        if (mAuthToken == null) {
            mAddTemporaryUserLsn.noAuthToken();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_INTELLIGENT_REMOTE_USER,
                GetParameter.addIntelligentRemoteUser(deviceSerId, mAuthToken, lockTempUser.getNickName(),
                        String.valueOf(getLongTime(lockTempUser.getStart())), String.valueOf(getLongTime(lockTempUser.getEnd())), lockTempUser.getShowTimes(),
                        lockTempUser.getMobile(), !TextUtils.isEmpty(lockTempUser.getMobile()), lockTempUser.getIsMax() == 1), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private AddTemporaryUserLsn mAddTemporaryUserLsn;

    /**
     * 添加临时用户回调
     */
    public interface AddTemporaryUserLsn extends AuthTokenError {
        /**
         * @param newLockTempUser 新创建的临时用户
         */
        void addTemporaryUserOk(LockTempUser newLockTempUser);
    }

    /**
     * 删除临时用户
     *
     * @param lockTempUser           要删除的临时用户
     * @param deleteTemporaryUserLsn 回调
     */
    public void deleteTemporaryUser(LockTempUser lockTempUser, DeleteTemporaryUserLsn deleteTemporaryUserLsn) {
        if (isNoSerId()) {
            return;
        }
        mDeleteTemporaryUserLsn = deleteTemporaryUserLsn;
        if (mAuthToken == null) {
            mDeleteTemporaryUserLsn.noAuthToken();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DEL_INTELLIGENT_REMOTE_USER,
                GetParameter.delIntelligentRemoteUser(lockTempUser.getId(), deviceSerId, mAuthToken, lockTempUser.getPin()), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private DeleteTemporaryUserLsn mDeleteTemporaryUserLsn;

    /**
     * 删除临时用户回调
     */
    public interface DeleteTemporaryUserLsn extends AuthTokenError {
        void deleteTemporaryUserOk();
    }

    /**
     * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    private long getLongTime(String time) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? date.getTime() : 0;
    }

    /**
     * 修改临时用户
     *
     * @param lockTempUser           要修改的临时用户
     * @param modifyTemporaryUserLsn
     */
    public void modifyTemporaryUser(LockTempUser lockTempUser, ModifyTemporaryUserLsn modifyTemporaryUserLsn) {
        if (isNoSerId()) {
            return;
        }
        mModifyTemporaryUserLsn = modifyTemporaryUserLsn;
        if (!lockTempUser.isEffective()) {
            mModifyTemporaryUserLsn.temporaryUserExpire();
            return;
        }
        if (mAuthToken == null) {
            mModifyTemporaryUserLsn.noAuthToken();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.MODIFY_INTELLIGENT_REMOTE_USER,
                GetParameter.modifyIntelligentRemoteUser(lockTempUser.getId(), deviceSerId, lockTempUser.getPin(), mAuthToken, lockTempUser.getMobile(), lockTempUser.getNickName(),
                        String.valueOf(getLongTime(lockTempUser.getStart())), String.valueOf(getLongTime(lockTempUser.getEnd())),
                        lockTempUser.getShowTimes(), lockTempUser.getIsMax() == 1), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private ModifyTemporaryUserLsn mModifyTemporaryUserLsn;

    /**
     * 修改临时用户回调
     */
    public interface ModifyTemporaryUserLsn extends AuthTokenError {
        /**
         * @param lockTempUser 被修改后的临时用户
         */
        void modifyTemporaryUserOk(LockTempUser lockTempUser);

        /**
         * 临时用户到期，不能被修改
         */
        void temporaryUserExpire();
    }

    /**
     * 发送密码给临时用户
     *
     * @param lockTempUser            临时用户
     * @param sendTemporaryUserPwdLsn 回调
     */
    public void sendTemporaryUserPwd(LockTempUser lockTempUser, SendTemporaryUserPwdLsn sendTemporaryUserPwdLsn) {
        if (isNoSerId()) {
            return;
        }
        mSendTemporaryUserPwdLsn = sendTemporaryUserPwdLsn;
        if (mAuthToken == null) {
            mSendTemporaryUserPwdLsn.noAuthToken();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEND_REMOTE_PWD,
                GetParameter.sendRemotePwd(deviceSerId, lockTempUser.getPin(), mAuthToken, lockTempUser.getMobile()),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private SendTemporaryUserPwdLsn mSendTemporaryUserPwdLsn;

    /**
     * 发送临时用户的密码到临时用户
     */
    public interface SendTemporaryUserPwdLsn extends AuthTokenError {
        void sendTemporaryUserPwdOk();
    }

    /**
     * 查询开门记录
     *
     * @param openRecordLsn 回调
     */
    public void queryLockOpenRecord(OpenRecordLsn openRecordLsn) {
        if (isNoSerId()) {
            return;
        }
        mOpenRecordLsn = openRecordLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_OPENRECORD,
                GetParameter.queryIntelligentOpenrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private OpenRecordLsn mOpenRecordLsn;

    /**
     * 查询开门记录回调接口
     */
    public interface OpenRecordLsn {
        void openRecordLoad(List<LockHistory> lockHistories);
    }

    /**
     * 查询警告记录
     */
    public void queryLockWarnRecord(WarnRecordLsn warnRecordLsn) {
        if (isNoSerId()) {
            return;
        }
        mWarnRecordLsn = warnRecordLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_WARNINGRECORD,
                GetParameter.queryIntelligentWarningrecord(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private WarnRecordLsn mWarnRecordLsn;

    /**
     * 查询警报记录回调接口
     */
    public interface WarnRecordLsn {
        void warnRecordLoad(List<LockAlarm> lockAlarms);
    }

    /**
     * 查询推送设置列表
     */
    public void queryPush(QueryPushLsn queryPushLsn) {
        if (isNoSerId()) {
            return;
        }
        mQueryPushLsn = queryPushLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_INTELLIGENT_PUSH_LIST,
                GetParameter.queryIntelligentPushList(deviceSerId), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private QueryPushLsn mQueryPushLsn;

    /**
     * 查询推送设置列表回调
     */
    public interface QueryPushLsn {

        /**
         * @param mobile     电话号码
         * @param lockPushes 推送列表
         */
        void queryPushOk(String mobile, List<LockPush> lockPushes);
    }

    /**
     * 修改推送设置
     *
     * @param mobile     电话
     * @param lockPushes 推送数据集合
     */
    public void modifyPush(String mobile, List<LockPush> lockPushes, ModifyPushLsn modifyPushLsn) {
        if (isNoSerId()) {
            return;
        }
        mModifyPushLsn = modifyPushLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.MODIFY_INTELLIGENT_PUSH,
                GetParameter.modifyIntelligentPush(deviceSerId, mobile, lockPushes), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private ModifyPushLsn mModifyPushLsn;

    /**
     * 修改推送回调
     */
    public interface ModifyPushLsn {
        void modifyPushOk();
    }

    @Override
    public void onSuccess(String action, String json) {
        super.onSuccess(action, json);
        switch (action) {
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_FINGERHOME:
                LockStatus lockStatus = gson.fromJson(json, LockStatus.class);
                hasAdminPwd = lockStatus.getIsAuth() > 0;
                freshlockStatusWithType((byte) lockStatus.getType());
                int betty = lockStatus.getBetty();
                showBattery(betty);
                break;
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_OPENRECORD:
                List<LockHistory> lockHistories = new ArrayList<>();
                JSONArray recordsJA = CloudParseUtil.getJsonArryParm(json, "records");
                for (int i = 0; i < recordsJA.length(); i++) {
                    try {
                        String historyJson = recordsJA.getString(i);
                        LockHistory lockHistory = gson.fromJson(historyJson, LockHistory.class);
                        lockHistories.add(lockHistory);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mOpenRecordLsn != null) {
                    mOpenRecordLsn.openRecordLoad(lockHistories);
                }
                break;
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_WARNINGRECORD:
                List<LockAlarm> lockAlarms = new ArrayList<>();
                JSONArray warnRecordJA = CloudParseUtil.getJsonArryParm(json, "warnRecord");
                for (int i = 0; i < warnRecordJA.length(); i++) {
                    try {
                        String lockAlarmStr = warnRecordJA.getString(i);
                        LockAlarm lockAlarm = gson.fromJson(lockAlarmStr, LockAlarm.class);
                        lockAlarms.add(lockAlarm);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mWarnRecordLsn != null) {
                    mWarnRecordLsn.warnRecordLoad(lockAlarms);
                }
                break;
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_USERINGRECORD:
                List<LockUser> lockUsers = new ArrayList<>();
                JSONArray userJA = CloudParseUtil.getJsonArryParm(json, "list");
                for (int i = 0; i < userJA.length(); i++) {
                    try {
                        String lockUserStr = userJA.getString(i);
                        LockUser lockUser = gson.fromJson(lockUserStr, LockUser.class);
                        lockUsers.add(lockUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mQueryUserLsn != null) {
                    mQueryUserLsn.userRecordLoad(lockUsers);
                }
                break;
            case CloudConstant.CmdValue.SEND_INTELLIGENT_VALIDATECODE:
                if (mSendCodeLsn != null) {
                    mSendCodeLsn.sendCodeOk();
                }
                break;
            case CloudConstant.CmdValue.EDIT_INTELLIGENT_USER:
                if (mModifyUserLsn != null) {
                    mModifyUserLsn.modifyUserOk();
                }
                break;
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_AUTHPWD:
                String authToken = CloudParseUtil.getJsonParm(json, "authToken");
                if (mValidateAdminPwdLsn != null) {
                    mAuthToken = authToken;
                    mValidateAdminPwdLsn.validateAdminPwdOk();
                }
                break;
            case CloudConstant.CmdValue.ADD_INTELLIGENT_AUTHPWD:
                if (mCreatAuthPwdLsn != null) {
                    hasAdminPwd = true;
                    mCreatAuthPwdLsn.creatAdminPwdOk();
                }
                break;
            case CloudConstant.CmdValue.RESET_INTELLIGENT_PWD_BY_CODE:
                if (mResetPwdLsn != null) {
                    mResetPwdLsn.waitLockReset();
                }
                break;
            case CloudConstant.CmdValue.RESET_INTELLIGENT_PWD:
                if (mModifyPwdLsn != null) {
                    mModifyPwdLsn.modifyPwdOk();
                }
                break;
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_REMOTE_UNLOCKING:
                List<LockTempUser> lockTempUsers = new ArrayList<>();
                JSONArray tempUserJA = CloudParseUtil.getJsonArryParm(json, "list");
                for (int i = 0; i < tempUserJA.length(); i++) {
                    try {
                        String tempUserStr = tempUserJA.getString(i);
                        LockTempUser lockTempUser = gson.fromJson(tempUserStr, LockTempUser.class);
                        lockTempUsers.add(lockTempUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mQueryTemporaryUserLsn != null) {
                    mQueryTemporaryUserLsn.queryTemporaryUserOk(lockTempUsers);
                }
                break;
            case CloudConstant.CmdValue.ADD_INTELLIGENT_REMOTE_USER:
                String authCode = CloudParseUtil.getJsonParm(json, "authCode");
                LockTempUser newLockTempUser = gson.fromJson(CloudParseUtil.getJsonParm(json, "remoteUser"), LockTempUser.class);
                newLockTempUser.setPwd(authCode);
                if (mAddTemporaryUserLsn != null) {
                    mAddTemporaryUserLsn.addTemporaryUserOk(newLockTempUser);
                }
                break;
            case CloudConstant.CmdValue.DEL_INTELLIGENT_REMOTE_USER:
                if (mDeleteTemporaryUserLsn != null) {
                    mDeleteTemporaryUserLsn.deleteTemporaryUserOk();
                }
                break;
            case CloudConstant.CmdValue.MODIFY_INTELLIGENT_REMOTE_USER:
                LockTempUser lockTempUser = gson.fromJson(CloudParseUtil.getJsonParm(json, "remoteUser"), LockTempUser.class);
                if (mModifyTemporaryUserLsn != null) {
                    mModifyTemporaryUserLsn.modifyTemporaryUserOk(lockTempUser);
                }
                break;
            case CloudConstant.CmdValue.SEND_REMOTE_PWD:
                if (mSendTemporaryUserPwdLsn != null) {
                    mSendTemporaryUserPwdLsn.sendTemporaryUserPwdOk();
                }
                break;
            case CloudConstant.CmdValue.QUERY_INTELLIGENT_PUSH_LIST:
                String mobile = CloudParseUtil.getJsonParm(json, "mobile");
                JSONArray pushJA = CloudParseUtil.getJsonArryParm(json, "list");
                List<LockPush> lockPushes = new ArrayList<>();
                for (int i = 0; i < pushJA.length(); i++) {
                    try {
                        String pushStr = pushJA.getString(i);
                        LockPush lockPush = gson.fromJson(pushStr, LockPush.class);
                        lockPushes.add(lockPush);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mQueryPushLsn != null) {
                    mQueryPushLsn.queryPushOk(mobile, lockPushes);
                }
                break;
            case CloudConstant.CmdValue.MODIFY_INTELLIGENT_PUSH:
                if (mModifyPushLsn != null) {
                    mModifyPushLsn.modifyPushOk();
                }
                break;
        }
    }

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
            switch (statusBytes[2]) {
                case 0:
                    lockStatusChange(LockStatusEnum.FINGER_PRINT);
                    break;
                case 1:
                    lockStatusChange(LockStatusEnum.PWD);
                    break;
                case 2:
                    lockStatusChange(LockStatusEnum.CARD);
                    break;
                case 3:
                    lockStatusChange(LockStatusEnum.KEY_OPEN_LOCK);
                    break;
                case 4:
                    lockStatusChange(LockStatusEnum.TELECONTROL);
                    break;
                case 5:
                    lockStatusChange(LockStatusEnum.TEMP_LOCK_USER_OPEN);
                    break;
                case 7:
                    lockStatusChange(LockStatusEnum.INDOOR_UNLOCKING);
                    break;
            }
        } else if (cmd == CARD) {
            lockStatusChange(LockStatusEnum.CARD);
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
                lockStatusChange(LockStatusEnum.BACK_LOCK);
                break;
            case 5:
                lockStatusChange(LockStatusEnum.LOCK_CLOSE);
                break;
            case 7:
                lockStatusChange(LockStatusEnum.OVERDOOR_JUST);
                break;
            case 8:
                lockStatusChange(LockStatusEnum.LOCK_OPEN);
                break;
            case 9:
                lockStatusChange(LockStatusEnum.BACK_LOCK_RELEASE);
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
     * 门锁状态枚举
     */
    public enum LockStatusEnum {

        /**
         * 指纹开锁
         */
        FINGER_PRINT,
        /**
         * 密码开锁
         */
        PWD,
        /**
         * 卡开锁
         */
        CARD,
        /**
         * 钥匙开锁
         */
        KEY_OPEN_LOCK,
        /**
         * 遥控开锁
         */
        TELECONTROL,
        /**
         * 临时用户开锁
         */
        TEMP_LOCK_USER_OPEN,
        /**
         * 反锁
         */
        BACK_LOCK,
        /**
         * 上锁
         */
        LOCK_CLOSE,
        /**
         * 虚掩
         */
        OVERDOOR_JUST,
        /**
         * 开锁
         */
        LOCK_OPEN,
        /**
         * 反锁解除
         */
        BACK_LOCK_RELEASE,

        /**
         * 室内开锁
         */
        INDOOR_UNLOCKING
    }

    @Override
    public void onMessage(EventMsg eventMsg) {
        super.onMessage(eventMsg);
        switch (eventMsg.getAction()) {
            case OBConstant.StringKey.LOCK_ADMIN_PWD_RESET:
                String serialId = (String) eventMsg.getExtra("serialId");
                if (serialId.equals(deviceSerId)) {
                    if (mResetPwdLsn != null) {
                        mResetPwdLsn.resetPwdOk();
                    }
                }
                break;
        }
    }

    /**
     * 设定用户开锁为场景条件
     *
     * @param lockUser 开锁用户
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition toUserCondition(LockUser lockUser) throws Exception {
        if (isNoSerId()) {
            return null;
        }
        int pinInt = Integer.valueOf(lockUser.getPin());
        String condition;
        if (pinInt <= 255) {
            condition = "4ac34a" + Transformation.byte2HexString((byte) pinInt) + "00000000";
        } else {
            condition = "4ac352" + Transformation.byte2HexString((byte) pinInt) + Transformation.byte2HexString((byte) (pinInt >> 8)) + "000000";
        }
        return toCondition(condition);
    }

    /**
     * 设定临时用户开锁为场景条件
     *
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition toTemporaryUserCondition() throws Exception {
        if (isNoSerId()) {
            return null;
        }
        int pinInt = Integer.valueOf("1500");
        String condition = "4ac351" + Transformation.byte2HexString((byte) pinInt) + Transformation.byte2HexString((byte) (pinInt >> 8)) + "000000";
        return toCondition(condition);
    }


}
