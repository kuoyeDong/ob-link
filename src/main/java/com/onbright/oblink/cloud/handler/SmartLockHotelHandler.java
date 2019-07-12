package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;

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
    public void editIntelligentRemoteUser(String serialId, String authToken, String nickName,
                                         String startTime, String endTime, String times, boolean isMax) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_INTELLIGENT_REMOTE_USER,
                GetParameter.addIntelligentRemoteUser(serialId, authToken, nickName,
                        startTime, endTime, times, null, false, isMax), CloudConstant.Source.CONSUMER_OPEN + "intelligentRemoteUser", HttpRequst.POST);
    }



}
