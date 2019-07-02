package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.Obox;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.smartconfig.SmartConnectDeviceHandler;

import java.util.TimeZone;

import okhttp3.FormBody;

/**
 * use by:连接到云，与云断开连接，扫描设备，释放设备
 * create by dky at 2019/7/1
 */
public class OboxHandler implements HttpRespond {
    private SmartConnectDeviceHandler smartConnectDeviceHandler;
    private String routePwd;
    private String type;
    private String deviceSecret;
    private String deviceName;
    private String kitCenter;
    private String productKey;

    public interface EditOboxLsn {

    }

    private Obox obox;
    private EditOboxLsn editOboxLsn;

    /**
     * 编辑已在网obox请使用此构造方法
     *
     * @param obox        目标obox设备
     * @param editOboxLsn 编辑Obox的回调类
     */
    public OboxHandler(Obox obox, EditOboxLsn editOboxLsn) {
        this.obox = obox;
        this.editOboxLsn = editOboxLsn;
    }

    public interface ConnectOboxLsn {

    }

    private ConnectOboxLsn connectOboxLsn;

    public OboxHandler(String routePwd, String type, ConnectOboxLsn connectOboxLsn) {
        this.routePwd = routePwd;
        this.type = type;
        this.connectOboxLsn = connectOboxLsn;
    }

    public void connect() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.REGIST_ALIDEV);
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.REGIST_ALIDEV:

        }
    }


    @Override
    public void onFaild(ErrorCode errorCode, int responseNotOkCode, String operationFailedReason, String action) {
        switch (action) {
            case CloudConstant.CmdValue.REGIST_ALIDEV:

        }
    }

    @Override
    public FormBody.Builder getParamter(String action) {
        switch (action) {
            case CloudConstant.CmdValue.REGIST_ALIDEV:
                return GetParameter.registAliDev(TimeZone.getDefault().getID(), "OBOX");
        }
        return null;
    }


}
