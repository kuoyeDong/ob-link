package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.cloud.net.HttpRespond;

import okhttp3.FormBody;

/**
 * use by:组管理：创建、删除、添加删除组内的设备，设备类型和子设备类型相同的设备才能被放到同一组
 * create by dky at 2019/7/3
 */
public abstract class GroupHandler implements HttpRespond {


    @Override
    public void onSuccess(String action, String json) {

    }

}
