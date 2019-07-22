package com.onbright.oblink.cloud.net;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.onbright.oblink.cloud.ObInit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    public static final int POST = 0;
    public static final int PUT = 1;
    public static final int DELETE = 2;

    private static HttpRequst httpRequst;
    private OkHttpClient okHttpClient;

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

    /**
     * 发起请求
     *
     * @param httpRespond 接受请求返回的处理类
     * @param action      请求动作
     * @param builder     参数体
     * @param url         un
     * @param method      请求类型
     */
    public void request(final HttpRespond httpRespond, final String action, FormBody.Builder builder, String url, int method) {
        if (httpRespond == null || action == null || url == null) {
            return;
        }
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).
                    writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).
                    authenticator(new Authenticator() {
                        @Override
                        public Request authenticate(Route route, @NonNull Response response) {
                            return response.request().newBuilder().header("Authorization", Credentials.basic(ObInit.APP_SECRET, ObInit.APP_KEY)).build();
                        }
                    }).build();
        }
        builder.add(CloudConstant.ParameterKey.SYSTEM, "Android");
        builder.add(CloudConstant.ParameterKey.APP_ID, ObInit.APP_KEY);
        builder.add(CloudConstant.ParameterKey.ACCESS_TOKEN, ObInit.ACCESSTOKEN);
        Request.Builder requestBuilder = new Request.Builder().url(CloudConstant.Source.HTTPS + CloudConstant.Source.SERVER + url);
        switch (method) {
            case POST:
                requestBuilder.post(builder.build());
                break;
            case PUT:
                requestBuilder.put(builder.build());
                break;
            case DELETE:
                requestBuilder.delete(builder.build());
                break;
        }
        okHttpClient.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpRespond.onFaild(HttpRespond.ErrorCode.exceptionError, 0, null, action);
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        try {
                            final String json = response.body().string();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(json);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (jsonObject != null) {
                                if (jsonObject.has("status")) {
                                    int realCode = 200;
                                    try {
                                        realCode = jsonObject.getInt("status");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (realCode >= 200 && realCode < 300) {
                                        try {
                                            final String dataJson;
                                            if (jsonObject.has("data")) {
                                                dataJson = new JSONObject(json).getString("data");
                                            } else {
                                                dataJson = "{}";
                                            }
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    httpRespond.onSuccess(action, dataJson);
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        httpRespond.onFaild(HttpRespond.ErrorCode.operationFailed, 0, json, action);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                } else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            httpRespond.onSuccess(action, json);
                                        }
                                    });
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    httpRespond.onFaild(HttpRespond.ErrorCode.exceptionError, 0, null, action);
                                }
                            });
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpRespond.onFaild(HttpRespond.ErrorCode.responseNotOk, response.code(), null, action);
                        }
                    });
                }
            }
        });
    }
}