package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.EventMsg;
import com.onbright.oblink.cloud.CloudDataPool;
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
 * 管理wifi单品设备,删除wifi单品设备，例如wifi版本的红外转发器
 *
 * @author dky
 * 2019/7/2
 */
public abstract class WifiDeviceHandler implements HttpRespond {
    /**
     * wifi设备序列号
     */
    String wifiDeviceId;

    /**
     * @param wifiDeviceId wifi设备序列号
     */
    public WifiDeviceHandler(String wifiDeviceId) throws Exception {
        if (wifiDeviceId == null) {
            throw new Exception("wifiDeviceSerId is Null");
        }
        this.wifiDeviceId = wifiDeviceId;
        EventBus.getDefault().register(this);
    }

    /**
     * 删除wifi类设备
     */
    public void deleteWifiDevice(DeleteWifiDeviceLsn deleteWifiDeviceLsn) {
        mDeleteWifiDeviceLsn = deleteWifiDeviceLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_ALI_DEV, GetParameter.delWifiDev(wifiDeviceId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    private DeleteWifiDeviceLsn mDeleteWifiDeviceLsn;

    /**
     * 删除wifi设备成功接口
     */
    public interface DeleteWifiDeviceLsn {
        /**
         * 删除wifi设备成功
         *
         * @param wifiDeviceSerId 删除的wifi设备序列号
         */
        void deleteWifiDeviceSuc(String wifiDeviceSerId);

    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.DELETE_ALI_DEV:
                CloudDataPool.deleteWifiDevice(wifiDeviceId);
                if (mDeleteWifiDeviceLsn != null) {
                    mDeleteWifiDeviceLsn.deleteWifiDeviceSuc(wifiDeviceId);
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMsg eventMsg) {

    }
    /**
     * 解除监听
     */
    public void unRegist() {
        EventBus.getDefault().unregister(this);
    }

}
