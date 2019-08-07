package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.cloud.CloudDataPool;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;

/**
 * 管理wifi单品设备，例如wifi版本的红外转发器,删除wifi单品设备
 *
 * @author dky
 * 2019/7/2
 */
public abstract class WifiDeviceHandler implements HttpRespond {
    /**
     * wifi设备序列号
     */
    private String wifiDeviceId;

    /**
     * @param wifiDeviceId wifi设备序列号
     */
    public WifiDeviceHandler(String wifiDeviceId) {
        this.wifiDeviceId = wifiDeviceId;
    }

    public void removeWifiDevice() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_ALI_DEV, GetParameter.delWifiDev(wifiDeviceId), "", HttpRequst.POST);
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.DELETE_ALI_DEV:
                CloudDataPool.deleteWifiDevice(wifiDeviceId);
                deleteWifiDeviceSuc(wifiDeviceId);
                break;
        }
    }

    /**
     * 删除wifi设备成功
     *
     * @param wifiDeviceSerId 删除的wifi设备序列号
     */
    public abstract void deleteWifiDeviceSuc(String wifiDeviceSerId);

}
