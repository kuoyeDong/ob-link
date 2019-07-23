package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;

/**
 * use by:删除Obox
 * create by dky at 2019/7/1
 */
public abstract class OboxHandler implements HttpRespond, NoSerialId {

    /**
     * obox序列号
     */
    private String oboxSerId;

    /**
     * @param oboxSerId 要删除的obox序列号
     */
    public OboxHandler(String oboxSerId) {
        this.oboxSerId = oboxSerId;
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.DELETE_OBOX:
                oboxDeleteSuc(oboxSerId);
                break;
            default:
                break;
        }
    }

    /**
     * 删除obox
     */
    public void deleteObox() {
        if (oboxSerId == null) {
            noSerialId();
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_OBOX, GetParameter.onDeleteObox(true, oboxSerId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.DELETE);
    }

    /**
     * 成功删除obox
     *
     * @param oboxSerId 被删除的obox序列号
     */
    protected abstract void oboxDeleteSuc(String oboxSerId);

}
