package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.handler.basehandler.ControllableRfDeviceHandler;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.local.net.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理插座，开关、设置上报
 *
 * @author dky
 * 2019/8/14
 */
public abstract class SocketHandler extends ControllableRfDeviceHandler {

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected SocketHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    /**
     * 打开开关
     */
    public void turnOnSocket() {
        if (isNoSerId()) {
            return;
        }
        sendStatus = "01000000000000";
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS,
                GetParameter.onSetNodeState(deviceSerId, sendStatus),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 取得打开开关行为对象
     *
     * @return 行为对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeAction#toAction(String)}
     */
    public Action turnOnSocketToAction() throws Exception {
        byte[] bytes = new byte[8];
        bytes[0] = 1;
        return toAction(Transformation.byteArryToHexString(bytes));
    }

    /**
     * 关闭开关
     */
    public void turnOffSocket() {
        if (isNoSerId()) {
            return;
        }
        sendStatus = "00000000000000";
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS,
                GetParameter.onSetNodeState(deviceSerId, sendStatus),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 取得关闭开关行为对象
     *
     * @return 行为对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeAction#toAction(String)}
     */
    public Action turnOffSocketToAction() throws Exception {
        byte[] bytes = new byte[8];
        return toAction(Transformation.byteArryToHexString(bytes));
    }

    /**
     * 打开指示灯
     */
    public void turnOnIndicatorLight() {
        if (isNoSerId()) {
            return;
        }
        if (haveNotStatus()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(status, 0, 2).append("000001000000");
        sendStatus = sb.toString();
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS,
                GetParameter.onSetNodeState(deviceSerId, sendStatus),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 取得打开指示灯开关行为对象
     *
     * @return 行为对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeAction#toAction(String)}
     */
    public Action turnOnIndicatorLightToAction() throws Exception {
        if (haveNotStatus()) {
            return null;
        }
        byte[] bytes = new byte[8];
        bytes[0] = (byte) Integer.parseInt(status.substring(0, 2), 16);
        bytes[3] = 1;
        return toAction(Transformation.byteArryToHexString(bytes));
    }

    /**
     * 关闭指示灯
     */
    public void turnOffIndicatorLight() {
        if (isNoSerId()) {
            return;
        }
        if (haveNotStatus()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(status, 0, 2).append("000002000000");
        sendStatus = sb.toString();
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS,
                GetParameter.onSetNodeState(deviceSerId, sendStatus),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }
    /**
     * 取得关闭指示灯开关行为对象
     *
     * @return 行为对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeAction#toAction(String)}
     */
    public Action turnOffIndicatorLightToAction() throws Exception {
        if (haveNotStatus()) {
            return null;
        }
        byte[] bytes = new byte[8];
        bytes[0] = (byte) Integer.parseInt(status.substring(0, 2), 16);
        bytes[3] = 2;
        return toAction(Transformation.byteArryToHexString(bytes));
    }
    /**
     * 修改过载功率上报阈值
     *
     * @param threshold 阈值，有效取值范围1-240,255代表关闭上报，单位50瓦，比如传入1则表示超过50瓦上报
     */
    public void changeThresholdOfPower(int threshold) {
        if (isNoSerId()) {
            return;
        }
        if (haveNotStatus()) {
            return;
        }
        String thresholdHexStr = Transformation.byte2HexString((byte) threshold);
        StringBuilder sb = new StringBuilder();
        sb.append(status, 0, 2).append("00").append(thresholdHexStr).append("00000000");
        sendStatus = sb.toString();
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS,
                GetParameter.onSetNodeState(deviceSerId, sendStatus),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    @Override
    protected void onStatusChange(String status) {
        byte[] statusBytes = Transformation.hexString2Bytes(status);
        if (MathUtil.byteIndexValid(statusBytes[0], 0, 4) == 0) {
            onSocketOff();
        } else {
            onSocketOn();
        }
        List<SocketException> socketExceptions = new ArrayList<>();
        if (MathUtil.byteIndexValid(statusBytes[0], 4, 4) == 0) {
            onSocketException(socketExceptions);
        } else {
            if (MathUtil.byteIndexValid(statusBytes[0], 4) == 1) {
                socketExceptions.add(SocketException.HIGH_VOLTAGE);
            }
            if (MathUtil.byteIndexValid(statusBytes[0], 5) == 1) {
                socketExceptions.add(SocketException.LOW_VOLTAGE);
            }
            if (MathUtil.byteIndexValid(statusBytes[0], 6) == 1) {
                socketExceptions.add(SocketException.OVERCURRENT);
            }
            if (MathUtil.byteIndexValid(statusBytes[0], 7) == 1) {
                socketExceptions.add(SocketException.CONTROL_MALFUNCTION);
            }
        }
        if (MathUtil.byteIndexValid(statusBytes[1], 7) == 0) {
            onIndicatorLightOff();
        } else {
            onIndicatorLightOn();
        }
        handleThresholdOfPower(MathUtil.validByte(statusBytes[2]));
        onPower((MathUtil.validByte(statusBytes[3]) << 8) + MathUtil.validByte(statusBytes[4]));
        onEnergyConsumption((MathUtil.validByte(statusBytes[5]) << 8) + MathUtil.validByte(statusBytes[6]));
    }

    /**
     * 插座打开
     */
    protected abstract void onSocketOn();

    /**
     * 插座关闭
     */
    protected abstract void onSocketOff();

    /**
     * 插座异常回调
     *
     * @param socketExceptions 异常列表，如列表长度为0，则表示没有异常
     */
    protected abstract void onSocketException(List<SocketException> socketExceptions);

    /**
     * 指示灯打开
     */
    protected abstract void onIndicatorLightOn();

    /**
     * 指示灯关闭
     */
    protected abstract void onIndicatorLightOff();

    /**
     * 得到当前功率，单位
     *
     * @param power 功率，单位0.1瓦
     */
    protected abstract void onPower(int power);

    /**
     * 得到当前能耗值
     *
     * @param energyConsumption 能耗值，单位0.1度
     */
    protected abstract void onEnergyConsumption(int energyConsumption);

    /**
     * 得到功率阈值,超过此功率，该插座会报警
     *
     * @param threshold 阈值，单位50瓦
     */
    protected abstract void onThresholdOfPower(int threshold);

    /**
     * 未设置过载报警
     */
    protected abstract void notSetOverLoadAlarm();

    /**
     * 过载报警已关闭
     */
    protected abstract void closeOverLoadAlarm();

    /**
     * 处理得到功率阈值
     *
     * @param threshold 阈值，单位50瓦
     */
    private void handleThresholdOfPower(int threshold) {
        if (threshold == 0) {
            notSetOverLoadAlarm();
        } else if (threshold == 255) {
            closeOverLoadAlarm();
        } else {
            onThresholdOfPower(threshold);
        }
    }

    public enum SocketException {

        /**
         * 电压过高
         */
        HIGH_VOLTAGE,

        /**
         * 电压过低
         */
        LOW_VOLTAGE,

        /**
         * 过流
         */
        OVERCURRENT,

        /**
         * 控制故障
         */
        CONTROL_MALFUNCTION;
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.SOCKET;
    }

}
