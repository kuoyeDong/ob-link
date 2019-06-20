package com.onbright.oblink.cloud.helper;

import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;

/**
 * 注册帮助类、帮助用户注册账户。
 * <p>
 * 如果要注册，请使用{@link #regist(String, String, String)},成功则回调{@link #onRegistSuc()}.失败回调{@link #onRegistFailed(String, String)}
 * Created by adolf_dong on 2017/10/31.
 */

public abstract class SregistHelper implements HttpRespond {

    /**
     * @param name    要注册的用户名
     * @param pwd     要注册的用户密码
     * @param license 客户码，用于区分注册用户的权重，可为null
     * @return name和pwd不能为空，此方法会对二者判断，如没调用成功则返回true，否则返回false
     */
    public boolean regist(String name, String pwd, String license) {
        if (name != null && pwd != null) {
            HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.REGISTER,
                    GetParameter.onRegister(name, pwd, license));
            return true;
        }
        return false;
    }

    @Override
    public void onRequest(String action) {

    }

    @Override
    public void onSuccess(String action, String json) {
        onRegistSuc();
    }


    @Override
    public void onFaild(String action, Exception e) {

    }

    @Override
    public void onFaild(String action, int state) {

    }

    @Override
    public void onRespond(String action) {

    }

    @Override
    public void operationFailed(String action, String json) {
        onRegistFailed(action, json);
    }

    /**
     * 注册成功回调
     */
    public abstract void onRegistSuc();

    /**
     * 注册失败回调
     *
     * @param action 失败时候的动作
     * @param json   失败原因代码
     */
    public abstract void onRegistFailed(String action, String json);
}
