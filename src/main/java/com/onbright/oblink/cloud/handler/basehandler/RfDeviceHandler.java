package com.onbright.oblink.cloud.handler.basehandler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.EventMsg;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.net.OBConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Rf类设备处理基础类，功能点：扫描设备，释放设备，状态回调
 *
 * @author dky
 * 2019/7/3
 */
public abstract class RfDeviceHandler implements HttpRespond, NoSerialId {

    /**
     * 设备序列号
     */
    protected String deviceSerId;

    /**
     * 扫描设备类型
     */
    protected String pType;
    /**
     * 扫描设备子类型
     */
    protected String type;

    /**
     * 要添加设备的obox
     */
    protected String oboxSerId;

    /**
     * 扫描持续时间
     */
    protected String time = "30";

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected RfDeviceHandler(@Nullable String deviceSerId) {
        this.deviceSerId = deviceSerId;
        EventBus.getDefault().register(this);
    }

    /**
     * 检查序列号
     *
     * @return 无序列号返回true
     */
    protected boolean isNoSerId() {
        if (deviceSerId == null) {
            noSerialId();
            return true;
        }
        return false;
    }

    /**
     * 获取控制的设备序列号
     *
     * @return 设备序列号
     */
    public String getDeviceSerId() {
        return deviceSerId;
    }

    /**
     * 设置要控制的设备序列号
     *
     * @param deviceSerId 设备序列号
     */
    public void setDeviceSerId(String deviceSerId) {
        this.deviceSerId = deviceSerId;
    }

    /**
     * 添加设备
     *
     * @param oboxSerId 通过哪个obox添加传该obox序列号
     * @param time      添加持续的时间，超过该时间无法再添加,默认30s
     */
    public void searchNewDevice(String oboxSerId, String time, SearchNewDeviceLsn searchNewDeviceLsn) {
        if (oboxSerId == null) {
            noSerialId();
            return;
        }
        mSearchNewDeviceLsn = searchNewDeviceLsn;
        DeviceEnum deviceEnum = getDeviceEnum();
        pType = String.valueOf(deviceEnum.getpType());
        type = String.valueOf(deviceEnum.getType());
        if (time != null) {
            this.time = time;
        }
        this.oboxSerId = oboxSerId;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SEARCH_NEW_DEVICES,
                GetParameter.onSearchNewDevicesOB(oboxSerId, isDc(deviceEnum.getpType(),
                        deviceEnum.getType()) ? "03" : "02", time, "", pType, type),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 启动扫描接口
     */
    public interface SearchNewDeviceLsn {
        /**
         * 启动扫描成功
         */
        void searchNewDeviceSuc();
    }

    private SearchNewDeviceLsn mSearchNewDeviceLsn;

    /**
     * 删除设备
     */
    public void deleteDevice(DeleteDeviceLsn deleteDeviceLsn) {
        if (isNoSerId()) {
            return;
        }
        mDeleteDeviceLsn = deleteDeviceLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_DEVICE, GetParameter.onModifyDevice(deviceSerId, "", true),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 删除设备接口
     */
    public interface DeleteDeviceLsn {
        /**
         * 删除设备成功
         */
        void deleteDeviceSuc();
    }

    private DeleteDeviceLsn mDeleteDeviceLsn;

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.SEARCH_NEW_DEVICES:
                if (mSearchNewDeviceLsn != null) {
                    mSearchNewDeviceLsn.searchNewDeviceSuc();
                }
                break;
            case CloudConstant.CmdValue.DELETE_DEVICE:
                if (mDeleteDeviceLsn != null) {
                    mDeleteDeviceLsn.deleteDeviceSuc();
                }
                break;
        }
    }

    /**
     * 获取设备入网是否为主动入网方式
     *
     * @param pType 设备类型
     * @param type  设备子类型
     * @return 如果是主动入网方式则返回true
     */
    private boolean isDc(int pType, int type) {
        switch (pType) {
            case OBConstant.NodeType.IS_SENSOR:
                switch (type) {
                    case OBConstant.NodeType.FLOOD:
                    case OBConstant.NodeType.TEMP_HUMID_SENSOR:
                    case OBConstant.NodeType.DC_RED_SENSOR:
                    case OBConstant.NodeType.DOOR_WINDOW_MAGNET:
                    case OBConstant.NodeType.DC_BODY_ALS:
                        return true;
                }
                break;
            case OBConstant.NodeType.IS_OBSOCKET:
                switch (type) {
                    case OBConstant.NodeType.ONE_BUTTON_WIRE_SOCKET:
                    case OBConstant.NodeType.TWO_BUTTON_WIRE_SOCKET:
                        return true;
                }
                break;
            case OBConstant.NodeType.SMART_LOCK:
                return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMsg eventMsg) {
        switch (eventMsg.getAction()) {
            case OBConstant.StringKey.UPDATE_SCAN_INFO:
                Device device = (Device) eventMsg.getExtra("newDevice");
                onNewDevice(device);
                break;
            case OBConstant.StringKey.STATUS_CHANGE_REPORT:
            case OBConstant.StringKey.CONTROL_STATUS_CHANGE:
                String serialId = (String) eventMsg.getExtra("serialId");
                String status = (String) eventMsg.getExtra("status");
                if (serialId.equals(this.deviceSerId)) {
                    onStatusChange(status);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设备状态变更，此方法在超类中被实现复写，转换为与各设备对应的属性变化方法
     *
     * @param status 设备状态
     */
    protected abstract void onStatusChange(String status);

    /**
     * 扫描到新设备
     *
     * @param device 新设备
     */
    protected abstract void onNewDevice(Device device);

    /**
     * @return 子类实现，获取对应的设备枚举
     */
    protected abstract DeviceEnum getDeviceEnum();

    /**
     * 解除监听
     */
    public void unRegist() {
        EventBus.getDefault().unregister(this);
    }

}
