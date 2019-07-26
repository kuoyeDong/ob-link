package com.onbright.oblink.smartconfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.TcpSend;
import com.onbright.oblink.local.net.Transformation;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

/**
 * smartconfig激活方式流程主控制类
 * 1.执行smartconfig让设备连接到目标路由器，获取到设备ip地址后连接设备的5000端口；
 * 2.发送obgz，收到obconfig数据判断设备类型，按如下分支进行：
 * 2.1如果是obox则按传统OB_Link模式读取obox内配置直至获取完成,之后注册服务器，返回后发送服务器配置到设备，调用addobox接口，等待mqtt返回；
 * 2.2如果是单品设备则注册服务器，返回后发送服务器配置到设备，调用upload接口，等待mqtt返回；
 * Created by adolf_dong on 2019/2/20.
 */

@SuppressWarnings("deprecation")
public abstract class SmartConnectDeviceHandler extends ConnectDeviceHandler {

    /**
     * wifi服务
     */
    private EspWifiAdminSimple wifiAdmin;


    private static final String TAG = "ConnectDeviceHandler";

    /**
     * @param context      上下文
     * @param routePwd     目标路由器密码
     * @param deviceSecret 设备密钥
     * @param deviceName   设备名称
     * @param kitCenter    连接域名
     * @param productKey   产品key
     */
    protected SmartConnectDeviceHandler(Context context, String routePwd, String deviceSecret, String deviceName, String kitCenter, String productKey) {
        super(context, routePwd, deviceSecret, deviceName, kitCenter, productKey);
        wifiAdmin = new EspWifiAdminSimple(context);
    }

    /**
     * 开始网络配置
     */
    @Override
    public void start() {
        Log.d(TAG, "start: wifiAdmin.getWifiConnectedSsidAscii(wifiAdmin.getWifiConnectedSsid())"+wifiAdmin.getWifiConnectedSsidAscii(wifiAdmin.getWifiConnectedSsid()));
        Log.d(TAG, "start: wifiAdmin.getWifiConnectedBssid()"+wifiAdmin.getWifiConnectedBssid());
        Log.d(TAG, "start: routePwd"+routePwd);
        new SmartConnectDeviceHandler.EsptouchAsyncTask().execute(wifiAdmin.getWifiConnectedSsidAscii(wifiAdmin.getWifiConnectedSsid()), wifiAdmin.getWifiConnectedBssid(), routePwd);
        if (tcpServer == null) {
            tcpServer = new SmartConnectDeviceHandler.TcpServer();
        }
        onSendDeviceToRoute();
        handler.sendEmptyMessageDelayed(CON_ROUTE_TIMEOUT, CON_ROUTE_MAX_TIME);
    }

    @Override
    void getParmFinish() {
        tcpServer.startSendParamter();
    }

    /**
     * 具体执行者
     */
    @SuppressLint("StaticFieldLeak")
    private class EsptouchAsyncTask extends AsyncTask<String, Void, List<IEsptouchResult>> {
        private IEsptouchTask mEsptouchTask;
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {
            synchronized (mLock) {
                mEsptouchTask = new EsptouchTask(params[0], params[1], params[2], context);
                mEsptouchTask.setEsptouchListener(myListener);
            }
            return mEsptouchTask.executeForResults(1);
        }

        private IEsptouchListener myListener = new IEsptouchListener() {

            @Override
            public void onEsptouchResultAdded(final IEsptouchResult result) {
                Log.d(TAG, "onEsptouchResultAdded: >>>>Add");
                tcpServer.lstTcp(result.getInetAddress().getHostAddress());
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
            }
        };


        @Override
        protected void onPostExecute(List<IEsptouchResult> iEsptouchResultList) {
            super.onPostExecute(iEsptouchResultList);
        }
    }

    private SmartConnectDeviceHandler.TcpServer tcpServer;

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

        private SmartConnectDeviceHandler.TcpServer.DeviceChanel deviceChanel;
        private static final String TAG = "TcpServer";

        /**
         * 释放网络资源
         */
        private void releaseSource() {
            if (deviceChanel != null) {
                deviceChanel.releaseSource();
            }
        }

        private void lstTcp(final String ip) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket sc = new Socket();
                    SocketAddress mSocketAddress = new InetSocketAddress(ip, 5000);
                    try {
                        sc.connect(mSocketAddress, 2000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tcpSend = new TcpSend(sc, LocalDataPool.newInstance().getHandler(), SmartConnectDeviceHandler.this);
                    LocalDataPool.newInstance().regist(SmartConnectDeviceHandler.this);
                    deviceChanel = new DeviceChanel(sc);
                    try {
                        sc.getOutputStream().write("obgz".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.removeMessages(CON_ROUTE_TIMEOUT);
                    handler.sendEmptyMessageDelayed(CON_CLOUD_TIMEOUT, CON_CLOUD_MAX_TIME);
                    onScendDeviceToRouteSuc();
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
                    if (ifConfig.substring(0, 4).equals("++++")) {
                        String cmd = goalStr.substring(8, 10);
                        if (cmd.equals("81")) {/*发送指令让设备连接到服务器*/
                            onSendDeviceToCloud();
                            if (type == OBConstant.OnAddWifiDeviceType.OBOX) {
                                addOboxToSerVer();
                            } else {
                                getDeviceState(type);
                            }
                            return null;
                        }
                    } else if (ifConfig.length() >= 8 && ifConfig.substring(0, 8).equals("obconfig")) {/*设备连接到路由器并连接到tcpserver，发送云端配置给设备*/
                        serNum = goalStr.substring(16, 16 + 10);
                        type = Integer.valueOf(goalStr.substring(16 + 10, 16 + 12), 16);
                        /*判断设备类型，如果是obox则获取数据，单品则请求阿里云配置*/
                        /*接收业务转交给传统recieve*/
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
             * 发送配置信息，每包最大长度500，预留更新ca功能，所以分开发送
             *
             * @param deviceSecret 设备连接密钥
             * @param deviceName   设备名称
             * @param kitCenter    连接域名
             * @param productKey   产品key
             * @return 包含配置信息的待加密数组
             */
            private byte[] sendparamData(String deviceSecret, String deviceName, String kitCenter, String productKey) {
                byte[] deviceSecretBytes = deviceSecret.getBytes();
                byte[] deviceNameBytes = deviceName.getBytes();
                byte[] kitCenterBytes = kitCenter.getBytes();
                byte[] productkeyBytes = productKey.getBytes();
                byte[] bytes = new byte[deviceSecretBytes.length + deviceNameBytes.length + kitCenterBytes.length + productkeyBytes.length + 2 * 4 + 1 + 3];
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
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length] = 0x01;/*编码*/
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1] = 0x01;/*长度*/
                bytes[3 + deviceSecretBytes.length + 1 + 1 + deviceNameBytes.length + 1 + 1 + kitCenterBytes.length + 1 + 1 + productkeyBytes.length + 1 + 1] = 0x08 | 0x02;/*内容*/
                return bytes;
            }

            /**
             * 配置获取完成后开始传输服务器参数给obox
             */
            private void startSendParamter() {
                final byte[] parameter = sboxOfSmartConfig.pack(sendparamData(deviceSecret, deviceName, kitCenter, productKey));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket.getOutputStream().write(parameter);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }


}
