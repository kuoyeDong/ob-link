package com.onbright.oblink.cloud.handler;

import com.onbright.oblink.cloud.net.HttpRespond;

import okhttp3.FormBody;

/**
 * use by:扫描设备，释放设备
 * create by dky at 2019/7/3
 */
public abstract class DeviceHandler implements HttpRespond {

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {

        }
    }

    public void requestNewDevice(String oboxSerId) {

    }



    @Override
    public FormBody.Builder getParamter(String action) {
        return null;
    }
}
