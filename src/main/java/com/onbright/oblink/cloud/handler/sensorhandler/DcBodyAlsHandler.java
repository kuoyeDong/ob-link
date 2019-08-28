package com.onbright.oblink.cloud.handler.sensorhandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.handler.scenehandler.ConditionRule;
import com.onbright.oblink.local.net.Transformation;

/**
 * 处理DC人体+光感
 *
 * @author dky
 * 2019/8/15
 */
public abstract class DcBodyAlsHandler extends BodySensorHandler {
    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected DcBodyAlsHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.DC_BODY_ALS;
    }

    @Override
    protected void onStatusChange(String status) {
        super.onStatusChange(status);
        byte[] statusBytes = Transformation.hexString2Bytes(status);
        onLightLevel(MathUtil.validByte(statusBytes[3]));
    }

    /**
     * 得到光照等级
     *
     * @param lightLevel 从弱到强，1-5
     */
    protected abstract void onLightLevel(int lightLevel);

    @Override
    protected int getBatteryIndex() {
        return 7;
    }

    /**
     * 取得人体感应和光感条件对象
     *
     * @param aboutBody  是否和人体检测有关
     * @param someOnHere 有人为true
     * @param aboutLight 是否和光感检测有关
     * @param lightRule  光感条件规则
     * @param lightLevel 从弱到强1-5
     * @return 条件对象
     * @throws Exception 参见{@link com.onbright.oblink.cloud.bean.BeCondition#toCondition(String)}
     */
    public Condition bodyAlstoCondition(boolean aboutBody, boolean someOnHere, boolean aboutLight, ConditionRule lightRule, int lightLevel) throws Exception {
        byte[] condition = new byte[8];
        if (aboutBody) {
            condition[0] = 0x4A;
            condition[1] = (byte) (someOnHere ? 1 : 0);
        } else {
            condition[0] = 0x4C;
            condition[1] = (byte) 0xFF;
        }
        if (aboutLight) {
            condition[2] = (byte) lightRule.getVal();
            condition[3] = (byte) lightLevel;
        }
        return toCondition(Transformation.byteArryToHexString(condition));
    }
}
