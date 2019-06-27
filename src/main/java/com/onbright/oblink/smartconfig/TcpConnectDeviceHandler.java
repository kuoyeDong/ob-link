package com.onbright.oblink.smartconfig;

import android.content.Context;
import android.util.Log;

import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.MqttHandler;
import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.TcpSend;
import com.onbright.oblink.local.net.Transformation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * com.ob.obsmarthouse.common.net.smartconfig   TcpConnectDeviceHandler
 * author:Adolf_Dong  time: 2019/2/20 18:27
 * use:tcp方式激活设备实现类
 * <p>
 * smartconfig激活方式流程主控制类
 * 注意在切换连接设备wifi之前就已经在服务器注册好设备
 * * 1.连接设备的5000端口；
 * * 2.发送obgz，收到obconfig数据判断设备类型，按如下分支进行：
 * * 2.1如果是obox则按传统OB_Link模式读取obox内配置直至获取完成，之后发送服务器配置到设备，切换回可达外网网络后，调用addobox接口，重新连接mqtt，等待mqtt返回；
 * * 2.2如果是单品设备则，发送服务器配置到设备，切换回可达外网网络后，调用upload接口，重新连接mqtt，等待mqtt返回；；
 */

public abstract class TcpConnectDeviceHandler extends ConnectDeviceHandler {
    /**
     * 目标路由器的ssid
     */
    private String routeSsid;

    /**
     * 设备的ip地址
     */
    private static final String IP = "192.168.2.3";

    /**
     * @param context      上下文
     * @param routeSsid    目标路由器ssid
     * @param routePwd     目标路由器密码
     * @param deviceSecret 设备密钥
     * @param deviceName   设备名称
     * @param kitCenter    连接域名
     * @param productKey   产品key
     */
    protected TcpConnectDeviceHandler(Context context, String routeSsid, String routePwd, String deviceSecret, String deviceName, String kitCenter, String productKey) {
        super(context, routePwd, deviceSecret, deviceName, kitCenter, productKey);
        this.routeSsid = routeSsid;
    }


    @Override
    public void start() {
        if (tcpServer == null) {
            tcpServer = new TcpConnectDeviceHandler.TcpServer();
        }
        tcpServer.connectAndStart();
        handler.sendEmptyMessageDelayed(CON_CLOUD_TIMEOUT, CON_CLOUD_MAX_TIME);
    }

    @Override
    public void getParmFinish() {
        tcpServer.startSendParamter();
    }


    private TcpConnectDeviceHandler.TcpServer tcpServer;

    /**
     * 释放网络资源
     */
    @Override
    public void releaseSource() {
        super.releaseSource();
        if (tcpServer != null) {
            tcpServer.releaseSource();
        }
    }

    @Override
    public void onReceive(byte[] bytes) {
        tcpServer.onReceive(bytes);
    }

    private class TcpServer {

        private TcpConnectDeviceHandler.TcpServer.DeviceChanel deviceChanel;
        private static final String TAG = "TcpServer";

        /**
         * 释放网络资源
         */
        private void releaseSource() {
            if (deviceChanel != null) {
                deviceChanel.releaseSource();
            }
        }

        /**
         * 连接并发送obgz请求数据开始交互
         */
        private void connectAndStart() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket sc = new Socket();
                    SocketAddress mSocketAddress = new InetSocketAddress(IP, 5000);
                    try {
                        sc.connect(mSocketAddress, 2000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tcpSend = new TcpSend(sc, LocalDataPool.newInstance().getHandler(), TcpConnectDeviceHandler.this);
                    LocalDataPool.newInstance().regist(TcpConnectDeviceHandler.this);
                    deviceChanel = new DeviceChanel(sc);
                    try {
                        sc.getOutputStream().write("obgz".getBytes());
                        Log.d(TAG, "run: 写出 obgz");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        private void startSendParamter() {
            deviceChanel.startSendParamter();
        }

        public void onReceive(byte[] bytes) {
            deviceChanel.onReceive(bytes);
        }

        /**
         * 收到设备连接后，维护设备通道类
         */
        private class DeviceChanel {
            private Socket socket;
            private SboxOfSmartConfig sboxOfSmartConfig;

            DeviceChanel(Socket socket) {
                this.socket = socket;
                this.sboxOfSmartConfig = new SboxOfSmartConfig();
            }

            /**
             * 释放网络资源
             */
            private void releaseSource() {
                if (tcpSend != null) {
                    tcpSend.disConnect();
                }
            }

            public void onReceive(byte[] bytes) {
                byte[] goalRec = sboxOfSmartConfig.unPack(bytes);
                Log.d(TAG, "rec:hexStr== " + Transformation.byteArryToHexString(goalRec));
                Log.d(TAG, "rec:== " + new String(goalRec));
                byte[] goalSend = makeSendData(goalRec);
                if (goalSend == null) {
                    return;
                }
                OutputStream ops;
                try {
                    Log.d(TAG, "send:== " + new String(goalSend));
                    Log.d(TAG, "send:Hex== " + Transformation.byteArryToHexString(goalSend));
                    ops = socket.getOutputStream();
                    ops.write(goalSend);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 根据收到的数据产生回复数据
             *
             * @param goalRec 接收到的解密后数据
             * @return 加密的回复数据
             */
            private byte[] makeSendData(byte[] goalRec) {
                String ifConfig = new String(goalRec);
                String goalStr = Transformation.byteArryToHexString(goalRec);
                if (goalStr != null) {
                    /*发送指令让设备连接到路由器和服务器的回复*/
                    if (ifConfig.substring(0, 4).equals("++++")) {
                        String cmd = goalStr.substring(8, 10);
                        if (cmd.equals("81")) {
                            onSendDeviceToCloud();
                            reConnectMqtt();
                            return null;
                        }
                        /*判断设备类型，如果是obox则获取数据，接收业务转交给传统recieve，单品则发送路由器和服务器配置*/
                    } else if (ifConfig.length() >= 8 && ifConfig.substring(0, 8).equals("obconfig")) {
                        serNum = goalStr.substring(16, 16 + 10);
                        type = Integer.valueOf(goalStr.substring(16 + 10, 16 + 12), 16);
                        if (type == OBConstant.OnAddWifiDeviceType.OBOX) {
                            isReqOboxMsg = true;
                            tcpSend.reqOboxMsg();
                        } else {
                            getParmFinish();
                        }
                        return null;
                    }
                }
                return new byte[68];
            }

            /**
             * 发送服务器配置数据到设备连接
             *
             * @param deviceSecret 设备连接密钥
             * @param deviceName   设备名称
             * @param kitCenter    连接域名
             * @param productKey   产品key
             * @param routeSsid    路由器ssid
             * @param routePwd     路由器密码
             * @return 包含配置信息的待加密数组
             */
            private byte[] sendparamData(String deviceSecret, String deviceName, String kitCenter, String productKey, String routeSsid, String routePwd) {
                byte[] deviceSecretBytes = deviceSecret.getBytes();
                byte[] deviceNameBytes = deviceName.getBytes();
                byte[] kitCenterBytes = kitCenter.getBytes();
                byte[] productkeyBytes = productKey.getBytes();
                byte[] routeSsidBytes = routeSsid.getBytes();
                byte[] routePwdBytes = routePwd.getBytes();
                byte[] bytes = new byte[deviceSecretBytes.length + deviceNameBytes.length + kitCenterBytes.length + productkeyBytes.length
                        + routeSsidBytes.length + routePwdBytes.length
                        + 2 * 6 + 1 + 3];
                /*密钥*/
                bytes[0] = 0x01;
                bytes[1] = 0x07;
                bytes[2] = (byte) deviceSecretBytes.length;
                System.arraycopy(deviceSecretBytes, 0, bytes, 3, deviceSecretBytes.length);
                bytes[3 + deviceSecretBytes.length] = 0x08;
                bytes[3 + deviceSecretBytes.length + 1] = (byte) deviceNameBytes.length;
                System.arraycopy(deviceNameBytes, 0, bytes, 3 + deviceSecretBytes.length + 1 + 1, deviceNameBytes.length);
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length] = 0x09;
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1] = (byte) kitCenterBytes.length;
                System.arraycopy(kitCenterBytes, 0, bytes, 3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1, kitCenterBytes.length);
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length] = 0x0a;
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1] = (byte) productkeyBytes.length;
                System.arraycopy(productkeyBytes, 0, bytes, 3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1, productkeyBytes.length);
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length] = 0x05;
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1] = (byte) routeSsidBytes.length;
                System.arraycopy(routeSsidBytes, 0, bytes, 3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1, routeSsidBytes.length);
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1 + routeSsidBytes.length] = 0x06;
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1 + routeSsidBytes.length + 1] = (byte) routePwdBytes.length;
                System.arraycopy(routePwdBytes, 0, bytes, 3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1 +
                        routeSsidBytes.length + 1 + 1, routePwdBytes.length);
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1 +
                        routeSsidBytes.length + 1 + 1 + routePwdBytes.length] = 0x01;/*编码*/
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1 +
                        routeSsidBytes.length + 1 + 1 + routePwdBytes.length + 1] = 0x01;/*长度*/
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1 +
                        routeSsidBytes.length + 1 + 1 + routePwdBytes.length + 1 + 1] = 0x08 | 0x02;/*内容*/
                return bytes;
            }

            /**
             * 配置获取完成后开始传输服务器参数给obox
             */
            private void startSendParamter() {
                final byte[] parameter = sboxOfSmartConfig.pack(sendparamData(deviceSecret, deviceName, kitCenter, productKey, routeSsid, routePwd));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.getOutputStream().write(parameter);
                            Log.d(TAG, "run: tcp模式开始发送服务器配置到设备");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }

    /**
     * 检测网络恢复可达状态后,上传配置
     */
    private void reConnectMqtt() {
        new Thread(new Runnable() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    InetAddress.getByName(CloudConstant.Source.SERVER).getAddress();
                    if (MqttHandler.isConnect) {
                        if (type == OBConstant.OnAddWifiDeviceType.OBOX) {
                            addOboxToSerVer();
                        } else {
                            getDeviceState(type);
                        }
                    } else {
                        reConnectMqtt();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    reConnectMqtt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
