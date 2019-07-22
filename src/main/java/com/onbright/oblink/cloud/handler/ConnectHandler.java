package com.onbright.oblink.cloud.handler;

import android.content.Context;

import com.onbright.oblink.Obox;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.smartconfig.SmartConnectDeviceHandler;

import java.util.TimeZone;

/**
 * use by:连接obox和wifi设备到云,运行sdk的设备必须连接目标路由
 * create by dky at 2019/7/3
 */
public class ConnectHandler implements HttpRespond {
    private Context context;
    private boolean isObox;
    private SmartConnectDeviceHandler smartConnectDeviceHandler;
    private String routePwd;

    /**
     * 连接设备回调类
     */
    public interface ConnectOboxLsn {
        /**
         * 连接出错
         *
         * @param connectError 出错类型
         */
        void error(ConnectError connectError);

        /**
         * 连接网关成功
         *
         * @param obox 连接成功的网关
         */
        void connectOboxSuc(Obox obox);

        /**
         * 连接wifi单品设备成功
         *
         * @param configStr 连接成功的wifi单品设备配置
         */
        void connectWifiDeviceSuc(String configStr);
    }

    private ConnectOboxLsn connectOboxLsn;

    /**
     * @param context        un
     * @param routePwd       连接路由器的密码
     * @param connectOboxLsn 回调类
     * @param isObox         是否连接obox
     */
    public ConnectHandler(Context context, String routePwd, ConnectOboxLsn connectOboxLsn, boolean isObox) {
        this.context = context;
        this.routePwd = routePwd;
        this.connectOboxLsn = connectOboxLsn;
        this.isObox = isObox;
    }

    /**
     * 开始执行连接
     */
    public void start() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.REGIST_ALIDEV,
                GetParameter.registAliDev(TimeZone.getDefault().getID(), isObox ? "OBOX" : "DEVICE"), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 释放资源
     */
    private void destroy() {
        if (smartConnectDeviceHandler != null) {
            smartConnectDeviceHandler.releaseSource();
        }
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.REGIST_ALIDEV:
                String deviceName = CloudParseUtil.getJsonParm(json, "deviceName");
                String deviceSecret = CloudParseUtil.getJsonParm(json, "deviceSecret");
                String kitCenter = CloudParseUtil.getJsonParm(json, "kitCenter");
                String productKey = CloudParseUtil.getJsonParm(json, "productKey");
                smartConnectDeviceHandler = new SmartConnectDeviceHandler(context, routePwd, deviceSecret, deviceName, kitCenter, productKey) {
                    @Override
                    public void onSendDeviceToRoute() {

                    }

                    @Override
                    public void onScendDeviceToRouteSuc() {

                    }

                    @Override
                    public void onConnectRouteError() {
                        if (connectOboxLsn != null) {
                            connectOboxLsn.error(ConnectError.connectRouteError);
                            destroy();
                        }
                    }

                    @Override
                    public void onSendDeviceToCloud() {

                    }

                    @Override
                    public void onConnectCloudError() {
                        if (connectOboxLsn != null) {
                            connectOboxLsn.error(ConnectError.connectCloudError);
                            destroy();
                        }
                    }

                    @Override
                    protected void addWifiDeviceSuc(String configStr) {
                        if (connectOboxLsn != null) {
                            connectOboxLsn.connectWifiDeviceSuc(configStr);
                            destroy();
                        }
                    }

                    @Override
                    protected void addOboxSuc(Obox obox) {
                        if (connectOboxLsn != null) {
                            connectOboxLsn.connectOboxSuc(obox);
                            destroy();
                        }
                    }
                };
                smartConnectDeviceHandler.start();
        }
    }

    @Override
    public void onFaild(ErrorCode errorCode, int responseNotOkCode, String operationFailedReason, String action) {
        switch (action) {
            case CloudConstant.CmdValue.REGIST_ALIDEV:
                if (connectOboxLsn != null) {
                    connectOboxLsn.error(ConnectError.registError);
                    destroy();
                }
        }
    }

    /**
     * 连接错误类型
     */
    public enum ConnectError {
        /**
         * 此错误发生在连接路由器前注册设备到云失败
         */
        registError,
        /**
         * 连接路由器出错
         */
        connectRouteError,
        /**
         * 连接云出错
         */
        connectCloudError

    }
}
