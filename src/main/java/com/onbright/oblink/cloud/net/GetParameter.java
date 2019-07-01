package com.onbright.oblink.cloud.net;


import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.bean.LockPush;
import com.onbright.oblink.local.Obox;

import java.util.List;

import okhttp3.FormBody;

public class GetParameter {

    /**
     * 添加obox
     *
     * @return 控制数据集
     */
    public static FormBody.Builder onAddObox(Obox obox, String deviceName, String productKey, boolean isSmarConfig) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.ADD_OBOX);
        Gson gson = new Gson();
        String json = gson.toJson(obox);
        builder.add(CloudConstant.ParameterKey.OBOX, json);
        if (isSmarConfig) {
            builder.add(CloudConstant.ParameterKey.PRODUCT_KEY, productKey);
            builder.add(CloudConstant.ParameterKey.DEVICE_NAME, deviceName);
        }
        return builder;
    }

    /**
     * 设置单节点或者组节点的状态,会修改状态最后一个字节
     *
     * @param device 节点
     * @param isBili       是否闪烁
     */
    public static FormBody.Builder onSetNodeState(Device device, boolean isBili) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_NODE_STATUS);
        builder.add(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, device.getSerialId());
        builder.add(CloudConstant.ParameterKey.STATE, !isBili ?
                device.getState().substring(0, 12) + "02" : device.getState().substring(0, 12) + "ff");
        return builder;
    }

    /**
     * 设置节点状态不会修改状态的最后一个字节
     *
     * @param device 节点
     */
    public static FormBody.Builder onSetNodeState(Device device) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_NODE_STATUS);
        builder.add(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, device.getSerialId());
        builder.add(CloudConstant.ParameterKey.STATE, device.getState());
        return builder;
    }

    /**
     * 设置节点状态
     *
     * @param device 节点
     * @param status       要设置的状态
     */
    public static FormBody.Builder onSetNodeState(Device device, String status) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_NODE_STATUS);
        builder.add(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, device.getSerialId());
        builder.add(CloudConstant.ParameterKey.STATE, status);
        return builder;
    }

    /**
     * 查询obox中的所有情景   此方法查询后 ， 返回obox中情景的映射Map，要正常显示，则要进行转换到List处理
     * 分页查询
     *
     * @param isMulti 是否分页
     * @param start   起始数
     * @param count   长度
     * @return un
     */
    public static FormBody.Builder onQueryScenes(boolean isMulti, int start, int count) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_SCENES);
        if (isMulti) {
            builder.add("start", String.valueOf(start));
            builder.add("count", String.valueOf(count));
        }
        return builder;
    }

    /**
     * 查询OBOX信息
     */
    public static FormBody.Builder onQueryObox() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_OBOX);
        return builder;
    }

    /**
     * 扫描新节点设备
     */
    public static FormBody.Builder onSearchNewDevicesOB(String oboxSerialId, String state, String timeout, String serialId, String pType, String type) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SEARCH_NEW_DEVICES);
        builder.add(CloudConstant.ParameterKey.OBOX_SERIAL_ID, oboxSerialId);
        builder.add(CloudConstant.ParameterKey.STATES, state);
        builder.add(CloudConstant.ParameterKey.TIME_OUT, timeout);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.DEVICE_TYPE, pType);
        builder.add(CloudConstant.ParameterKey.DEVICE_CHILD_TYPE, type);
        return builder;
    }

    /**
     * 查询设备
     *
     * @param index 其实index
     * @param count 长度，为0则全部查询
     */
    public static FormBody.Builder onQueryDevice(int index, int count) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_DEVICE);
        builder.add(CloudConstant.ParameterKey.START_INDEX, String.valueOf(index));
        builder.add(CloudConstant.ParameterKey.COUNT, String.valueOf(count));
        return builder;
    }

    public static FormBody.Builder onQueryDeviceStatusHistory(String device_serial_id, String type, String from_date, String to_date,
                                                              String start_index, String count) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_DEVICE_STATUS_HISTORY);
        builder.add(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, device_serial_id);
        builder.add(CloudConstant.ParameterKey.TYPE, type);
        if (type.equals("00")) {//个数为单位
            builder.add(CloudConstant.ParameterKey.START, start_index);
            builder.add(CloudConstant.ParameterKey.COUNT, count);
        } else if (type.equals("01")) {//小时为单位
            builder.add(CloudConstant.ParameterKey.FROM_DATA, from_date);
            builder.add(CloudConstant.ParameterKey.TO_DATA, to_date);
        } else if (type.equals("02")) {//天为单位
            builder.add(CloudConstant.ParameterKey.FROM_DATA, from_date);
            builder.add(CloudConstant.ParameterKey.TO_DATA, to_date);
        }
        return builder;
    }

    public static FormBody.Builder onSetGroup(String group_id, String group_name, String group_state,
                                              String operate_type, String group_member, String group_style, String obox_serialid, String groupAddr) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SET_GROUP);
        builder.add(CloudConstant.ParameterKey.OPERATE_TYPE, operate_type);
        switch (operate_type) {
            case CloudConstant.ParameterValue.DELETE_GROUP:
                builder.add(CloudConstant.ParameterKey.GROUP_ID, group_id);
                break;
            case CloudConstant.ParameterValue.SETTING_GROUP:
                builder.add(CloudConstant.ParameterKey.GROUP_STYLE, group_style);
                if (group_style.equals("00")) {
                    builder.add(CloudConstant.ParameterKey.OBOX_SERIAL_ID, obox_serialid);
                }
                builder.add(CloudConstant.ParameterKey.GROUP_NAME, group_name);
                break;
            case CloudConstant.ParameterValue.COVER_MEMBER:
                builder.add(CloudConstant.ParameterKey.GROUP_MEMBER, group_member);
                builder.add(CloudConstant.ParameterKey.GROUP_ID, group_id);
                break;
            case CloudConstant.ParameterValue.ADD_MEMBER:
                builder.add(CloudConstant.ParameterKey.GROUP_MEMBER, group_member);
                builder.add(CloudConstant.ParameterKey.GROUP_ID, group_id);
                break;
            case CloudConstant.ParameterValue.DELETE_MEMBER:
                builder.add(CloudConstant.ParameterKey.GROUP_MEMBER, group_member);
                builder.add(CloudConstant.ParameterKey.GROUP_ID, group_id);
                break;
            case CloudConstant.ParameterValue.RE_NAME:
                builder.add(CloudConstant.ParameterKey.GROUP_NAME, group_name);
                builder.add(CloudConstant.ParameterKey.GROUP_ID, group_id);
                break;
            case CloudConstant.ParameterValue.EXUTE_GROUP:
                builder.add(CloudConstant.ParameterKey.GROUP_ID, group_id);
                builder.add(CloudConstant.ParameterKey.GROUP_STATE,
                        group_state.substring(0, 12) + "02");
                if (group_style.equals("00")) {
                    builder.add(CloudConstant.ParameterKey.GROUPADDR, groupAddr);
                }
                break;
        }
        return builder;
    }

    /**
     * 修改节点
     *
     * @param serialId 序号
     */
    public static FormBody.Builder onModifyDevice(String serialId, String name, boolean isDelete) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.MODIFY_DEVICE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.OPERATE_TYPE, isDelete ? "00" : "01");
        builder.add(CloudConstant.ParameterKey.NAME, name);
        return builder;
    }

    /**
     * 查询组信息
     */
    public static FormBody.Builder onQueryGroups() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_GROUP);
        return builder;
    }


    /**
     * 设置场景信息
     */
    public static FormBody.Builder onSetScInfo(CloudScene curentScene) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SETTING_SC_INFO);
        Gson gson = new Gson();
        String gsonStr = gson.toJson(curentScene);
        builder.add(CloudConstant.ParameterKey.SCENE, gsonStr);
        return builder;
    }

    /**
     * 删除obox
     *
     * @param force   是否强制删除，不建议使用强制删除
     * @param oboxSer 要删除的obox的序列号
     */
    public static FormBody.Builder onDeleteObox(boolean force, String oboxSer) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DELETE_OBOX);
        builder.add(CloudConstant.ParameterKey.OBOX_SERIAL_ID, oboxSer);
        builder.add(CloudConstant.ParameterKey.FORCE_DELETE, force ? "01" : "00");
        return builder;
    }

    /**
     * 获取设备真实状态
     *
     * @param device 目标设备
     */
    public static FormBody.Builder getNodeStatus(Device device) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_NODE_REAL_STATUS);
        builder.add(CloudConstant.ParameterKey.SERIALID, device.getSerialId());
        return builder;
    }

    /**
     * 注册阿里设备
     *
     * @param zone 时区
     * @param type 设备类型
     * @return 请求参数列表
     */
    public static FormBody.Builder registAliDev(String zone, String type) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.REGIST_ALIDEV);
        builder.add(CloudConstant.ParameterKey.ZONE, zone);
        builder.add(CloudConstant.ParameterKey.TYPE, type);
        return builder;
    }

    /**
     * 上传单品设备到云端
     *
     * @param deviceName 产品名字
     * @param productKey 产品key
     * @param configStr  标准化设备定义json字符串
     * @return 请求参数列表
     */
    public static FormBody.Builder uploadConfig(String deviceName, String productKey, String configStr) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.UPLOAD_CONFIG);
        builder.add(CloudConstant.ParameterKey.DEVICE_NAME, deviceName);
        builder.add(CloudConstant.ParameterKey.PRODUCT_KEY, productKey);
        builder.add(CloudConstant.ParameterKey.CONFIG, configStr);
        return builder;
    }

    /**
     * 查询OB智能门锁主页信息,返回电量，上下线，状态
     *
     * @param serialId 门锁序列号
     */
    public static FormBody.Builder queryIntelligentFingerhome(String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_INTELLIGENT_FINGERHOME);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 查询门锁开门记录
     *
     * @param serialId 门锁序列号
     */
    public static FormBody.Builder queryIntelligentOpenrecord(String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_INTELLIGENT_OPENRECORD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 查询门锁警告记录
     *
     * @param serialId 门锁序列号
     */
    public static FormBody.Builder queryIntelligentWarningrecord(String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_INTELLIGENT_WARNINGRECORD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 查询OB智能门锁用户列表
     *
     * @param serialId 门锁序列号
     */
    public static FormBody.Builder queryIntelligentUseringrecord(String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_INTELLIGENT_USERINGRECORD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 发送验证码到胁迫时目标手机
     *
     * @param serialId 门锁序列号
     * @param pin      门锁用户pin
     * @param phone    目标手机号码
     */
    public static FormBody.Builder sendIntelligentValidatecode(String serialId, String pin, String phone) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SEND_INTELLIGENT_VALIDATECODE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.PIN, pin);
        builder.add(CloudConstant.ParameterKey.MOBILE, phone);
        return builder;
    }

    /**
     * 编辑门锁用户
     *
     * @param serialId     门锁序列号
     * @param pin          门锁用户pin
     * @param nickName     门锁用户昵称
     * @param phone        门锁用户被胁迫推送的电话号码
     * @param validateCode 验证码
     * @param hasStressPwd 是否有胁迫指纹或密码
     */
    public static FormBody.Builder editIntelligentUser(String serialId, String pin, String nickName, String phone, String validateCode, boolean hasStressPwd) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.EDIT_INTELLIGENT_USER);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.PIN, pin);
        builder.add(CloudConstant.ParameterKey.NICKNAME, nickName);
        if (hasStressPwd) {
            builder.add(CloudConstant.ParameterKey.MOBILE, phone);
            builder.add(CloudConstant.ParameterKey.VALIDATE_CODE, validateCode);
        }
        return builder;
    }

    /**
     * 智能门锁验证权限密码
     *
     * @param serialId       门锁序列号
     * @param verficationPwd 门锁密码
     */
    public static FormBody.Builder queryIntelligentAuthpwd(String serialId, String verficationPwd) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_INTELLIGENT_AUTHPWD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.PASS_WORD, verficationPwd);
        return builder;
    }

    /**
     * 查询门锁临时用户
     *
     * @param serialId  门锁序列号
     * @param authToken 门锁token
     */
    public static FormBody.Builder queryIntelligentRemoteUnlocking(String serialId, String authToken) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_INTELLIGENT_REMOTE_UNLOCKING);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.AUTH_TOKEN, authToken);
        return builder;
    }

    /**
     * 删除临时门锁用户
     *
     * @param id        临时用户id
     * @param serialId  门锁序列号
     * @param authToken 门锁口令
     * @param pin       临时用户pin
     */
    public static FormBody.Builder delIntelligentRemote_user(int id, String serialId, String authToken, String pin) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DEL_INTELLIGENT_REMOTE_USER);
        builder.add(CloudConstant.ParameterKey.ID, id + "");
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.AUTH_TOKEN, authToken);
        builder.add(CloudConstant.ParameterKey.PIN, pin);
        return builder;
    }

    /**
     * @param serialId  门锁序列号
     * @param authToken 门锁口令
     * @param nickName  昵称
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param times     使用次数
     * @param mobile    电话号码
     * @param pushPhone 是否需要推送手机
     */
    public static FormBody.Builder addIntelligentRemoteUser(String serialId, String authToken, String nickName,
                                                            String startTime, String endTime, String times, String mobile, boolean pushPhone, boolean isMax) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.ADD_INTELLIGENT_REMOTE_USER);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.AUTH_TOKEN, authToken);
        builder.add(CloudConstant.ParameterKey.NICKNAME, nickName);
        builder.add(CloudConstant.ParameterKey.START_TIME, startTime);
        builder.add(CloudConstant.ParameterKey.END_TIME, endTime);
        builder.add(CloudConstant.ParameterKey.TIMES, times);
        if (pushPhone) {
            builder.add(CloudConstant.ParameterKey.MOBILE, mobile);
        }
        builder.add(CloudConstant.ParameterKey.ISMAX, isMax ? "1" : "0");
        return builder;
    }

    /**
     * 发送密码给临时用户
     *
     * @param serialId  门锁序列号
     * @param pin       临时用户pin
     * @param authToken 门锁口令
     * @param mobile    临时用户手机号
     */
    public static FormBody.Builder sendRemotePwd(String serialId, String pin, String authToken, String mobile) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.SEND_REMOTE_PWD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.PIN, pin);
        builder.add(CloudConstant.ParameterKey.AUTH_TOKEN, authToken);
        builder.add(CloudConstant.ParameterKey.MOBILE, mobile);
        return builder;
    }

    /**
     * 智能门锁忘记权限密码
     *
     * @param serialId 门锁序列号
     */
    public static FormBody.Builder forgetIntelligentPwd(String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.FORGET_INTELLIGENT_PWD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 智能门锁根据推送重置权限密码
     *
     * @param serialId 门锁序列号
     * @param pwd      密码
     */
    public static FormBody.Builder resetIntelligentPwdByCode(String serialId, String pwd) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.RESET_INTELLIGENT_PWD_BY_CODE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.PASS_WORD, pwd);
        return builder;
    }

    /**
     * 智能门锁修改权限密码
     *
     * @param serialId 门锁序列号
     * @param oldPwd   旧密码
     * @param newPwd   新密码
     */
    public static FormBody.Builder resetIntelligentPwd(String serialId, String oldPwd, String newPwd) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.RESET_INTELLIGENT_PWD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.OLD_PWD, oldPwd);
        builder.add(CloudConstant.ParameterKey.NEW_PWD, newPwd);
        return builder;
    }

    /**
     * 智能门锁修改远程用户
     *
     * @param id        用户序列号
     * @param serialId  门锁序列号
     * @param pin       远程用户pin
     * @param authToken 门锁口令
     * @param mobile    远程用户绑定手机
     * @param nickName  远程用户昵称
     * @param startTime 有效开始时间
     * @param endTime   有效结束时间
     * @param times     有效次数
     */
    public static FormBody.Builder modifyIntelligentRemoteUser(int id, String serialId, String pin, String authToken,
                                                               String mobile, String nickName, String startTime, String endTime, String times, boolean isMax) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.MODIFY_INTELLIGENT_REMOTE_USER);
        builder.add(CloudConstant.ParameterKey.ID, id + "");
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.PIN, pin);
        builder.add(CloudConstant.ParameterKey.AUTH_TOKEN, authToken);
        if (mobile != null && mobile.length() != 0) {
            builder.add(CloudConstant.ParameterKey.MOBILE, mobile);
        }
        builder.add(CloudConstant.ParameterKey.NICKNAME, nickName);
        builder.add(CloudConstant.ParameterKey.START_TIME, startTime);
        builder.add(CloudConstant.ParameterKey.END_TIME, endTime);
        builder.add(CloudConstant.ParameterKey.TIMES, times);
        builder.add(CloudConstant.ParameterKey.ISMAX, isMax ? "1" : "0");
        return builder;
    }

    /**
     * 智能门锁修改远程用户
     *
     * @param serialId 门锁序列号
     */
    public static FormBody.Builder queryIntelligentPushList(String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_INTELLIGENT_PUSH_LIST);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 修改推送设置
     *
     * @param serialId   门锁序列号
     * @param mobile     电话
     * @param lockPushes 推送数据集合
     */
    public static FormBody.Builder modifyIntelligentPush(String serialId, String mobile, List<LockPush> lockPushes) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.MODIFY_INTELLIGENT_PUSH);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.MOBILE, mobile);
        Gson gson = new Gson();
        String pushInfo = gson.toJson(lockPushes);
        builder.add(CloudConstant.ParameterKey.PUSH_INFO, pushInfo);
        return builder;
    }

    /**
     * 门锁创建权限密码
     *
     * @param serialId 门锁序列号
     * @param pwd      权限密码
     */
    public static FormBody.Builder addIntelligentAuthpwd(String serialId, String pwd) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.ADD_INTELLIGENT_AUTHPWD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.PASS_WORD, pwd);
        return builder;
    }

    /*以下为更新后的红外转发器对应的接口*/

    /**
     * 查询设备
     */
    public static FormBody.Builder queryAliDevice() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_ALI_DEV);
        return builder;
    }

    /**
     * 删除设备
     *
     * @param deviceId 设备序列号
     */
    public static FormBody.Builder delAliDev(String deviceId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DELETE_ALI_DEV);
        builder.add(CloudConstant.ParameterKey.DEVICEID, deviceId);
        return builder;
    }

    /**
     * wifi查询遥控器支持的设备类型
     *
     * @return un
     */
    public static FormBody.Builder onQueryWifiIrDeviceType() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_IR_DEVICE_TYPE);
        return builder;
    }

    /**
     * 获取遥控云品牌类型
     *
     * @param type 遥控云定义的设备类型
     * @return un
     */
    public static FormBody.Builder onQueryWifiIrBrand(String type) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_IR_BRAND);
        builder.add(CloudConstant.ParameterKey.DEVICETYPE, type);
        return builder;
    }

    /**
     * 获取红外遥控方案
     *
     * @param serialId 红外转发器序列号
     * @return un
     */
    public static FormBody.Builder onQueryWifiIrDevice(String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_IR_DEVICE);
        builder.add(CloudConstant.ParameterKey.DEVICE_SERIAL_ID, serialId);
        return builder;
    }

    /**
     * 删除红外遥控方案,同时解绑匹配的遥控云码库
     *
     * @param index    遥控索引ID
     * @param serialId 红外转发器序列号
     * @return un
     */
    public static FormBody.Builder onDeleteIrDevice(String index, String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DELETE_IR_DEVICE);
        builder.add(CloudConstant.ParameterKey.INDEX, index);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 重命名红外遥控方案
     *
     * @param serialId 红外转发器序列号
     * @param index    遥控索引ID
     * @param name     要设置的新名称
     * @return un
     */
    public static FormBody.Builder renameIrDevice(String serialId, String index, String name) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.RENAME_IR_DEVICE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.INDEX, index);
        builder.add(CloudConstant.ParameterKey.NAME, name);
        return builder;
    }

    /**
     * 控制转发命令
     *
     * @param serialId 红外转发器序列号
     * @param index    遥控索引ID
     * @param key      标准按键或拓展按键的按键名称key
     * @param keyType  0:标准按键
     *                 1:拓展按键
     *                 2:手动匹配测试按键
     * @return un
     */
    public static FormBody.Builder controlWifiIrDevice(String serialId, int index, String key, int keyType) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.CONTROL_IR_DEVICE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.INDEX, index + "");
        builder.add(CloudConstant.ParameterKey.KEYTYPE, keyType + "");
        builder.add(CloudConstant.ParameterKey.KEY, key);
        return builder;
    }

    /**
     * 删除方案中特定按键
     *
     * @param serialId 红外转发器序列号
     * @param index    遥控索引ID
     * @param keyType  0:标准按键
     *                 1:拓展按键
     *                 2:手动匹配测试按键
     * @param key      标准按键或拓展按键的按键名称key
     * @return un
     */
    public static FormBody.Builder deleteIrDeviceKey(String serialId, int index, int keyType, String key) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.DELETE_IR_DEVICE_KEY);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.INDEX, index + "");
        builder.add(CloudConstant.ParameterKey.KEYTYPE, keyType + "");
        builder.add(CloudConstant.ParameterKey.KEY, key);
        return builder;
    }

    /**
     * 学习遥控方案——进入按键学习模式
     *
     * @param serialId 红外转发器序列号
     * @param index    遥控索引ID
     * @param keyType  0:标准按键
     *                 1:拓展按键
     *                 2:手动匹配测试按键
     * @param key      标准按键或拓展按键的按键名称key
     * @param timeOut  超时时间/秒置0为提前取消,红外转发器只在此时间内处于接收原始码状态
     * @return un
     */
    public static FormBody.Builder learnIrDeviceKey(String serialId, int index, int keyType, String key, int timeOut) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.LEARN_IR_DEVICE_KEY);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.INDEX, index + "");
        builder.add(CloudConstant.ParameterKey.KEYTYPE, keyType + "");
        builder.add(CloudConstant.ParameterKey.KEY, key);
        builder.add(CloudConstant.ParameterKey.TIME_OUT, timeOut + "");
        return builder;
    }

    /**
     * 学习遥控方案——新建自定义遥控器
     *
     * @param serialId   红外转发器序列号
     * @param deviceType 学习的设备类型
     * @param brandId    学习的品牌，如无置0
     * @param name       用户自定义输入，默认为自定义遥控器+类型名
     * @return un
     */
    public static FormBody.Builder createIrDevice(String serialId, int deviceType, int brandId, String name) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.CREATE_IR_DEVICE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.DEVICETYPE, deviceType + "");
        builder.add(CloudConstant.ParameterKey.BRAND_ID, brandId + "");
        builder.add(CloudConstant.ParameterKey.NAME, name);
        return builder;
    }

    /**
     * 一键匹配遥控方案——进入空调对码模式
     *
     * @param serialId 红外转发器序列号
     * @param timeOut  超时时间/秒置0为提前取消,红外转发器只在此时间内处于接收原始码状态
     * @param brandId  匹配空调品牌
     * @return un
     */
    public static FormBody.Builder pairIrRemoteCode(String serialId, int timeOut, String brandId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.PAIR_IR_REMOTECODE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.TIME_OUT, "" + timeOut);
        builder.add(CloudConstant.ParameterKey.BRAND_ID, brandId);
        return builder;
    }

    /**
     * 根据品牌id，设备类型主动数据匹配,手动匹配遥控方案——测试码获取
     *
     * @param deviceType 匹配类型
     * @param brandId    匹配品牌
     * @param serialId   红外转发器序列号
     * @return un
     */
    public static FormBody.Builder queryIrTestCode(String deviceType, String brandId, String serialId) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.QUERY_IR_TESTCODE);
        builder.add(CloudConstant.ParameterKey.DEVICETYPE, deviceType);
        builder.add(CloudConstant.ParameterKey.BRAND_ID, brandId);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        return builder;
    }

    /**
     * 手动匹配/一键匹配遥控方案——绑定码库方案,完成手动匹配测试码库，绑定生成新的遥控方案对象
     *
     * @param serialId   红外转发器序列号
     * @param deviceType 匹配类型
     * @param brandId    匹配品牌
     * @param remoteId   遥控云码库id
     * @param name       可选，默认为类型加品牌名
     * @return un
     */
    public static FormBody.Builder bindIrRemotecode(String serialId, String deviceType, String brandId, String remoteId, String name) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.BIND_IR_REMOTECODE);
        builder.add(CloudConstant.ParameterKey.DEVICETYPE, deviceType);
        builder.add(CloudConstant.ParameterKey.BRAND_ID, brandId);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.REMOTEID, remoteId);
        builder.add(CloudConstant.ParameterKey.NAME, name);
        return builder;

    }

    /**
     * 下载码库方案至红外转发器
     *
     * @param serialId 红外转发器序列号
     * @param index    遥控索引ID
     * @param timeOut  超时时间/秒
     * @return un
     */
    public static FormBody.Builder localIrDeviceDownload(String serialId, String index, String timeOut) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.LOCAL_IR_DEVICE_DOWNLOAD);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.INDEX, index);
        builder.add(CloudConstant.ParameterKey.TIME_OUT, timeOut);
        return builder;
    }

    /**
     * 本地遥控方案——删除方案
     *
     * @param serialId 红外转发器序列号
     * @param index    遥控索引ID
     * @return un
     */
    public static FormBody.Builder localIrDeviceDelete(String serialId, String index) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(CloudConstant.ParameterKey.CMD, CloudConstant.CmdValue.LOCAL_IR_DEVICE_DELETE);
        builder.add(CloudConstant.ParameterKey.SERIALID, serialId);
        builder.add(CloudConstant.ParameterKey.INDEX, index);
        return builder;
    }

    /**
     * 初始化sdk，与昂宝云校验
     *
     * @param appKey    昂宝分配的key
     * @param appSecret key对应的secret
     */
    public static FormBody.Builder onInit(String appKey, String appSecret) {
        return null;
    }
}
