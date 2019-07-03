package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.cloud.CloudDataPool;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;

import okhttp3.FormBody;

/**
 * use by:删除Obox
 * create by dky at 2019/7/1
 */
public abstract class OboxHandler implements HttpRespond {

    /**
     * obox序列号
     */
    private String oboxSerId;

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.DELETE_OBOX:
                CloudDataPool.deleteOboxData(oboxSerId);
                oboxDeleteSuc(oboxSerId);
                break;
            default:
                break;
        }
    }

    @Override
    public FormBody.Builder getParamter(String action) {
        switch (action) {
            case CloudConstant.CmdValue.DELETE_OBOX:
                GetParameter.onDeleteObox(true, oboxSerId);
        }
        return null;
    }

    /**
     * 删除obox
     */
    public void deleteObox() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_OBOX);
    }

    /**
     * @param oboxSerId 要删除的obox序列号
     */
    public OboxHandler(String oboxSerId) {
        this.oboxSerId = oboxSerId;
    }

    /**
     * 成功删除obox
     *
     * @param oboxSerId 被删除的obox序列号
     */
    abstract void oboxDeleteSuc(String oboxSerId);

}
