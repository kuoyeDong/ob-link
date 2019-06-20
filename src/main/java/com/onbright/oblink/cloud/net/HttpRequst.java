package com.onbright.oblink.cloud.net;

import android.os.Handler;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpRequst {
    private static BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 20, 20, TimeUnit.SECONDS, queue);
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

    private HttpRequst() {
    }

    private Handler handler = new Handler();

    public void request(final HttpRespond httpRespond, final String action, final List<NameValuePair> nameValuePairs) {
        httpRespond.onRequest(action);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = HttpsFctry.getNewHttpClient();
                HttpPost httpPost = new HttpPost(CloudConstant.Source.Common);
                //noinspection deprecation
                httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpclient.execute(httpPost);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpRespond.onRespond(action);
                        }
                    });
                    final int stateCode = response.getStatusLine().getStatusCode();
                    if (stateCode == HttpStatus.SC_OK) {
                        final String json = EntityUtils.toString(response.getEntity(), "utf-8").trim();
                        if (!CloudParseUtil.isSucceful(json)) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    httpRespond.operationFailed(action, json);
                                }
                            });
                            return;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpRespond.onSuccess(action, json);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                httpRespond.onFaild(action, stateCode);
                            }
                        });
                    }
                } catch (final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            httpRespond.onFaild(action, e);
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }
}