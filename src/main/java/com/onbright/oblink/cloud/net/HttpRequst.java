package com.onbright.oblink.cloud.net;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.onbright.oblink.cloud.ObInit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

@SuppressWarnings("deprecation")
public class HttpRequst {
    private static HttpRequst httpRequst;

    public static HttpRequst getHttpRequst() {
        if (httpRequst == null) {
            synchronized (HttpRequst.class) {
                if (httpRequst == null) {
                    httpRequst = new HttpRequst();
                }
            }
        }
        return httpRequst;
    }

    private Handler handler = new Handler();

    private HttpRequst() {

    }

    public void request(final HttpRespond httpRespond, final String action) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, @NonNull Response response) {
                return response.request().newBuilder().header("Authorization", Credentials.basic("webApp", "webApp")).build();
            }
        }).build();
        FormBody.Builder builder = httpRespond.getParamter(action);
        builder.add(CloudConstant.ParameterKey.SYSTEM, ObInit.SYSTEM_NAME);
        builder.add(CloudConstant.ParameterKey.APP_ID, ObInit.APPLICATION_NAME);
        builder.add(CloudConstant.ParameterKey.APPKEY, ObInit.UNIQUE_KEY);
        builder.add(CloudConstant.ParameterKey.ACCESS_TOKEN, ObInit.ACCESSTOKEN);
        Request request = new Request.Builder().url(CloudConstant.Source.HTTPS + CloudConstant.Source.SERVER + "/consumer/common?")
                .post(builder.build()).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpRespond.onFaild(HttpRespond.ErrorCode.exceptionError, 0, null,action);
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            final String json = response.body().string();
                            int realCode = 200;
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(json);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (jsonObject != null && jsonObject.has("status")) {
                                try {
                                    realCode = jsonObject.getInt("status");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (realCode >= 200 && realCode < 300) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObj = new JSONObject(json);
                                            if (jsonObj.has("data")) {
                                                String dataJson = new JSONObject(json).getJSONObject("data").toString();
                                                httpRespond.onSuccess(action, dataJson);
                                            } else {
                                                httpRespond.onSuccess(action, "{}");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                httpRespond.onFaild(HttpRespond.ErrorCode.operationFailed, 0, json,action);
                                            }
                                        });
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            httpRespond.onFaild(HttpRespond.ErrorCode.exceptionError, 0, null,action);
                        }
                    }
                } else {
                    httpRespond.onFaild(HttpRespond.ErrorCode.responseNotOk, response.code(), null,action);
                }
            }
        });
    }
}