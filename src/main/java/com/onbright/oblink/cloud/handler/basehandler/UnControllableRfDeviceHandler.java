package com.onbright.oblink.cloud.handler.basehandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.cloud.CloudDataPool;
import com.onbright.oblink.cloud.bean.BeCondition;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.local.net.OBConstant;

/**
 * @author dky
 * 2019/8/27
 */
public abstract class UnControllableRfDeviceHandler extends RfDeviceHandler implements BeCondition {

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected UnControllableRfDeviceHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    @Override
    public Condition toCondition(String conditionProperty) throws Exception {
        Device device = CloudDataPool.getDeviceForSerId(deviceSerId);
        if (device == null) {
            throw new Exception("not find device,can not toCondition");
        }
        Condition cdt = new Condition();
        int pType = Integer.parseInt(device.getDevice_type(), 16);
        boolean isPrint = pType == OBConstant.NodeType.SMART_FINGER;
        cdt.setCondition_type(isPrint ? "03" : "01");
        cdt.setAddr(device.getAddr());
        cdt.setDevice_child_type(device.getDevice_child_type());
        cdt.setDevice_type(device.getDevice_type());
        cdt.setObox_serial_id(device.getObox_serial_id());
        cdt.setSerialId(device.getSerialId());
        cdt.setConditionID(device.getName() != null ? device.getName() : device.getSerialId());
        cdt.setOboxs(null);
        cdt.setCondition(conditionProperty);
        return cdt;
    }
}
