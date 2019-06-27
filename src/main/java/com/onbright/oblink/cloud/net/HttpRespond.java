package com.onbright.oblink.cloud.net;

import okhttp3.FormBody;

/**
 * 服务器请求接口
 * Created by adolf_dongon 2019/6/24.
 */
public interface HttpRespond {

    /**
     * 请求出错码
     */
    enum ErrorCode {
        /**
         * 发生异常
         */
        exceptionError,
        /**
         * 请求失败
         */
        responseNotOk,
        /**
         * 请求成功，但目标操作没成功
         */
        operationFailed
    }

    void onSuccess(String action, String json);

    /**
     * 请求失败
     *
     * @param errorCode             {@link ErrorCode}
     * @param responseNotOkCode     {@link ErrorCode#responseNotOk}时的http出错码
     * @param operationFailedReason {@link ErrorCode#operationFailed}时的出错原因
     * @param action                请求失败时的命令
     */
    void onFaild(ErrorCode errorCode, int responseNotOkCode, String operationFailedReason, String action);

    FormBody.Builder getParamter(String action);
}