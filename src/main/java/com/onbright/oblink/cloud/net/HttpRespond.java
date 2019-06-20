package com.onbright.oblink.cloud.net;

/**
 * 网络交互过程中的处理
 * Created by adolf_dongon 2016/1/6.
 */
public interface HttpRespond {

    /**
     * 请求时回调
     *
     * @param action 请求时行为，在完整交互中保持一致
     */
    void onRequest(String action);

    /**
     * 请求返回的时候调用，在{@link #onFaild(String, Exception)},{@link #onFaild(String, int)},{@link #onSuccess(String, String)}
     * {@link #operationFailed(String, String)} 之前被调用
     *
     * @param action 请求时行为，在完整交互中保持一致
     */
    void onRespond(String action);

    /**
     * 请求成功回调
     *
     * @param action 请求时行为，在完整交互中保持一致
     * @param json   返回数据
     */
    void onSuccess(String action, String json);

    /**
     * 请求发生异常的回调
     *
     * @param action 请求时行为，在完整交互中保持一致
     * @param e      异常
     */
    void onFaild(String action, Exception e);

    /**
     * 请求发生错误的回调，非异常
     *
     * @param action 请求时行为，在完整交互中保持一致
     * @param state  错误码，参见常规http状态码
     */
    void onFaild(String action, int state);


    /**请求成功，但是服务器返回失败回调
     * @param action 请求时行为，在完整交互中保持一致
     * @param json 失败返回数据
     */
    void operationFailed(String action, String json);
}