package com.onbright.oblink.cloud.handler.panel;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.handler.ControllableDeviceHandler;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import java.util.ArrayList;
import java.util.List;


/**
 * use by:面板处理基类、统一的开关、情景按钮触发、状态返回的处理
 * create by dky at 2019/8/6
 */
public abstract class BasePanelHandler extends ControllableDeviceHandler {
    /**
     * 情景按钮下标，纯开关时，此数据不会被用到
     */
    private static final int SCENE_INDEX = 0;

    /**
     * 开关按钮字节级下标,默认1
     */
    private int switchIndex = 1;

    /**
     * 开关按钮数最大限制
     */
    private int maxSwitchLen = 4;

    /**
     * 情景按钮最大限制
     */
    private int maxSceneLen = 8;

    private DeviceEnum deviceEnum;

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String)}操作
     */
    protected BasePanelHandler(@Nullable String deviceSerId, String status, DeviceEnum deviceEnum) throws Exception {
        super(deviceSerId, status);
        this.deviceEnum = deviceEnum;
        int type = deviceEnum.getpType();
        if (type != OBConstant.NodeType.IS_OBSOCKET) {
            throw new Exception("unSupport deviceType");
        }
        int childType = deviceEnum.getType();
        switch (deviceEnum) {
            case SWITCH:
                setValueAboutIndex(1, 0);
                break;
            case SINGLE_TOUCH_SWITCH:
                setValueAboutIndex(1, 0);
                break;
            case DOUBLE_TOUCH_SWITCH:
                setValueAboutIndex(2, 0);
                break;
            case THREE_TOUCH_SWITCH:
                setValueAboutIndex(3, 0);
                break;
            case FOUR_TOUCH_SWITCH:
                setValueAboutIndex(4, 0);
                break;
            case SINGLE_SWITCH_SCENE_PANEL:
                setValueAboutIndex(1, 3);
                break;
            case DOUBLE_SWITCH_SCENE_PANEL:
                setValueAboutIndex(2, 3);
                break;
            case THREE_SWITCH_SCENE_PANEL:
                setValueAboutIndex(3, 3);
                break;
            case ONE_BUTTON_WIRE_SOCKET:
                setValueAboutIndex(1, 0);
                break;
            case TWO_BUTTON_WIRE_SOCKET:
                setValueAboutIndex(2, 0);
                break;
            case THREE_SWITCH_RED_SCENE_PANEL:
                setValueAboutIndex(3, 4);
                break;
            case SIX_SCENE_PANEL:
                setValueAboutIndex(0, 6);
                break;
            case SIX_SCENE_RED_PANEL:
                setValueAboutIndex(0, 7);
                break;
            case SINGLE_SCENE_PANEL:
                setValueAboutIndex(0, 1);
                break;
            case DOUBLE_SCENE_PANEL:
                setValueAboutIndex(0, 2);
                break;
            case THREE_SCENE_PANEL:
                setValueAboutIndex(0, 3);
                break;
            default:
                throw new Exception("unSupport deviceType");
        }
    }

    /**
     * 设置开关、情景按键数量
     *
     * @param maxSwitchLen 开关按键数量
     * @param maxSceneLen  情景按键数量
     */
    private void setValueAboutIndex(int maxSwitchLen, int maxSceneLen) {
        this.maxSwitchLen = maxSwitchLen;
        this.maxSceneLen = maxSceneLen;
        if (maxSceneLen == 0) {
            switchIndex = 0;
        }
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return deviceEnum;
    }

    @Override
    protected void onStatusChange(String status) {
        byte[] bytes = Transformation.hexString2Bytes(status);
        List<SwtichStatusEnum> swtichStatusEnums = new ArrayList<>();
        for (int i = 0; i < maxSwitchLen; i++) {
            int indexVal = MathUtil.byteIndexValid(bytes[switchIndex], i * 2, 2);
            switch (indexVal) {
                case 0:
                    swtichStatusEnums.add(SwtichStatusEnum.OFF);
                    break;
                case 1:
                    swtichStatusEnums.add(SwtichStatusEnum.ON);
                    break;
                case 2:
                    swtichStatusEnums.add(SwtichStatusEnum.TOOGLE);
                    break;
                case 3:
                    swtichStatusEnums.add(SwtichStatusEnum.HOLD);
                    break;
            }
        }
        onSwtichStatus(swtichStatusEnums);
        /*没有情景按钮没必要继续*/
        if (switchIndex == 0) {
            return;
        }
        /*一次只能按下一个情景按钮*/
        for (int i = 0; i < maxSceneLen; i++) {
            if (MathUtil.byteIndexValid(bytes[SCENE_INDEX], i) == 1) {
                onScenePress(i);
                break;
            }
        }
    }

    /**
     * @param swtichStatusEnums 开关状态枚举列表，列表下标与开关实际下标对应
     */
    public abstract void onSwtichStatus(List<SwtichStatusEnum> swtichStatusEnums);

    /**
     * 开关状态枚举
     */
    public enum SwtichStatusEnum {

        /**
         * 关闭
         */
        OFF(0),
        /**
         * 开启
         */
        ON(1),
        /**
         * 置反
         */
        TOOGLE(2),
        /**
         * 保持不变
         */
        HOLD(3);
        /**
         * 数值
         */
        private final int val;

        public int getVal() {
            return val;
        }

        SwtichStatusEnum(int val) {
            this.val = val;
        }
    }

    /**
     * 情景按钮被按下，justHasSwtich为true时，此方法不会被回调
     *
     * @param index 被按下的情景按钮位置
     */
    public abstract void onScenePress(int index);

    /**
     * 关闭或者打开或者置反
     *
     * @param index            开关按钮位置
     * @param swtichStatusEnum 开关状态枚举
     */
    public void changeSwitchButton(int index, SwtichStatusEnum swtichStatusEnum) {
        if (isNoSerId()) {
            return;
        }
        byte[] staus = new byte[7];
        for (int i = 0; i < maxSwitchLen; i++) {
            if (index != i) {
                staus[switchIndex] = MathUtil.setMultiBitIndex(staus[switchIndex], i * 2, 2, SwtichStatusEnum.HOLD.getVal());
            } else {
                staus[switchIndex] = MathUtil.setMultiBitIndex(staus[switchIndex], i * 2, 2, swtichStatusEnum.getVal());
            }
        }
        sendStatus = Transformation.byteArryToHexString(staus);
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS, GetParameter.onSetNodeState(deviceSerId, sendStatus),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 模拟按下情景按钮
     *
     * @param index 要按下的情景按钮位置
     */
    public void touchSceneButton(int index) {
        if (isNoSerId()) {
            return;
        }
        byte[] staus = new byte[7];
        staus[SCENE_INDEX] = MathUtil.setBitIndex(staus[SCENE_INDEX], index, true);
        for (int i = 0; i < maxSwitchLen; i++) {
            staus[1] = MathUtil.setMultiBitIndex(staus[1], i * 2, 2, SwtichStatusEnum.HOLD.getVal());
        }
        sendStatus = Transformation.byteArryToHexString(staus);
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS, GetParameter.onSetNodeState(deviceSerId, sendStatus),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }
}
