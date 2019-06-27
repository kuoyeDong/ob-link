package com.onbright.oblink.cloud.net;

import android.content.Context;
import android.util.Log;

import com.onbright.oblink.cloud.ObInit;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * mqtt处理
 * topic是token，clientId是appKey设备序列号
 * mqtt端口
 * tcp://://aliiot.on-bright.com：183：1833
 * 登录时创建topic，topic格式“ob-smart.”+appKey
 * Created by Adolf_Dong on 2018/9/12.
 */

public class MqttHandler {

    private final static String serverUri = "tcp://" + CloudConstant.Source.SERVER + ":1883";

    private static final String TAG = "MqttHandler";

    /**
     * mqtt是否连接上
     */
    public static boolean isConnect;
    /**
     * 发布订阅主题
     */
    private String pubAndSubTopic;

    private MqttAndroidClient mqttAndroidClient;

    private static final int QOS = 1;
    /**
     * 长连接解析器
     */
    private ParseServerListener parseServerListener;

    /**
     * @param context  un
     * @param clientId appkey
     */
    public MqttHandler(Context context, String token, String clientId) {
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        parseServerListener = new ParseServerListener(context);
        pubAndSubTopic = "ob-smart." + token;
        Log.d(TAG, "MqttHandler: token =" + token + "clientId=" + clientId);
        setCallBack();
        connect();
    }

    /**
     * 连接mq
     */
    public void connect() {
        if (mqttAndroidClient != null ) {
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttConnectOptions.setCleanSession(false);
            try {
                Log.d(TAG, "connect: mqtt retry");
                mqttAndroidClient.connect(mqttConnectOptions, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        Log.d(TAG, "connect onSuccess: ");
                        publishMessage();
                        subscribeToTopic();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    Log.d(TAG, "connect onFailure:");
                                    connect();
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发布消息
     */
    private void publishMessage() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appkey", ObInit.DEVICE_ID);
            jsonObject.put("appId", "OB Smart" + ObInit.APPLICATION_NAME);
            jsonObject.put("system", "Android" + android.os.Build.VERSION.SDK_INT);
            jsonObject.put("type", 100);
            String pubMsg = jsonObject.toString();
            MqttMessage message = new MqttMessage();
            message.setPayload(pubMsg.getBytes());
            mqttAndroidClient.publish(pubAndSubTopic, message);
            Log.d(TAG, "Publishing: " + message.toString());
        } catch (MqttException e) {
            Log.e(TAG, "Error Publishing: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(pubAndSubTopic, QOS, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "subscribe onSuccess: ");
                    isConnect = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    subscribeToTopic();
                    isConnect = false;
                    Log.d(TAG, "subscribe onFailure: ");
                }
            });
            // THIS DOES NOT WORK!
            mqttAndroidClient.subscribe(pubAndSubTopic, QOS, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // message Arrived!
                    String pay = new String(message.getPayload());
                    Log.d(TAG, "messageArrived on sub: >>" + "Message: " + topic + " : " + pay);
                    if (pay.startsWith("STR")) {
                        parseServerListener.parseServerData(pay);
                    }
                }
            });
        } catch (MqttException ex) {
            Log.e(TAG, "Exception whilst subscribing ");
            ex.printStackTrace();
        }
    }


    private void setCallBack() {
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    subscribeToTopic();
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connectionLost: ");
                isConnect = false;
                connect();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d(TAG, "messageArrived on CallBack: >>" + "Message: " + topic + " : " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "deliveryComplete: ");
            }
        });
    }

    /**
     * 断开连接
     */
    public void shutDown() {
        try {
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}

