package com.onbright.oblink.local.net;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 检查当前网络判断工作模式，并查找连接的路由器中的obox
 * Created by adolf_dong on 2016/5/19.
 */
public abstract class NetUtil {
    private static final String TAG = "NetUtil";

    private static int workMode;
    private static int oriWorkMode;
    private DhcpInfo dhcpinfo;
    private DatagramPacket mDatagramPacket;
    private MulticastSocket ms;
    private int search_count;
    private boolean isSearchTimeout;
    private DatagramPacket pack;
    private List<String> oboxStringList;
    private WifiManager mWifiManager;
    private WifiManager.MulticastLock lock;

    /**
     * 检测当前网络环境
     *
     */
    public void ckNet() {
        if (mWifiManager.isWifiEnabled()) {
            dhcpinfo = mWifiManager.getDhcpInfo();
            int ip = dhcpinfo.serverAddress;
            if ((ip & 0xff) == 192 && ((ip >> 8) & 0xff)
                    == 168 && ((ip >> 16) & 0xff) == 2 &&
                    ((ip >> 24) & 0xff) == 3) {
                final TcpSend tcpSend = new TcpSend();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (tcpSend.tryCon()) {
                            setWorkMode(OBConstant.NetState.ON_AP);
                            tcpSend.disConnect();
                        } else {
                            setWorkMode(OBConstant.NetState.ON_STATION);
                        }
                    }
                }).start();
            } else {
                setWorkMode(OBConstant.NetState.ON_STATION);
            }
        } else {
            onWifiDisAble();
        }
    }

    public abstract void onWifiDisAble();
    public NetUtil(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            dhcpinfo = mWifiManager.getDhcpInfo();
        } else {
            onWifiDisAble();
        }
    }

    /**
     * 发送udp广播包
     */
    public void udpBc(Context context,final Handler handler, final boolean findServer,final int timeOut) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("broad");
        lock.acquire();
        try {
            if (ms == null) {
                ms = new MulticastSocket();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String broadCastIp = null;
                int ip = dhcpinfo.serverAddress;
                int mask = dhcpinfo.netmask;
                int broadIp = (ip & mask)|~mask;
                if (broadIp != 0) {
                    broadCastIp = ((broadIp & 0xff) + "." + (broadIp >> 8 & 0xff) + "." + (broadIp >> 16 & 0xff) + "." + (broadIp >> 24 & 0xff));
                }
                byte[] buffer = new byte[10];
                mDatagramPacket = new DatagramPacket(buffer, 10);
                String str = findServer ? "on-bright" : "HLK";
                byte out[] = str.getBytes();
                mDatagramPacket.setData(out);
                mDatagramPacket.setLength(out.length);
                mDatagramPacket.setPort(findServer ? 9090 : 988);
                search_count = 0;
                while (search_count < 10) {
                    try {
                        InetAddress address = InetAddress.getByName(broadCastIp);
                        mDatagramPacket.setAddress(address);
                        ms.send(mDatagramPacket);
                        Log.d(TAG, "run: mDatagramPacket.send = " + str);
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    search_count++;
                }
                Timer mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isSearchTimeout = true;
                        Message mes = new Message();
                        lock.release();
                        mes.what = findServer ? OBConstant.NetState.ON_DSFINISH_SERVER : OBConstant.NetState.ON_DSFINISH_OBOX;
                        handler.sendMessage(mes);
                    }
                }, timeOut);
            }
        }).start();
    }


    /**
     * 接收udp广播包
     *
     * @param obIplist 存放oboxip地址的列表
     */
    public void dscvObox(List<String> obIplist) {
        oboxStringList = obIplist;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isSearchTimeout) {
                        byte[] DataReceive = new byte[64];
                        pack = new DatagramPacket(DataReceive, DataReceive.length);
                        ms.receive(pack);
                        String addr = ("" + pack.getAddress()).substring(1);
                        boolean isFound = false;
                        for (String tmp : oboxStringList) {
                            if (tmp.equalsIgnoreCase(addr)) {
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) {
                            oboxStringList.add(addr);
                            Log.d(TAG, "add(addr) = " + addr);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    /**
     * 设置当前工作模式
     *
     * @param mode OBConstant.ON_AP ap模式 OBConstant.ON_STATION station模式 OBConstant.ON_CLOUD服务器模式
     */
    public static void setWorkMode(int mode) {
        workMode = mode;
    }

    /**
     * @return 获取原始工作状态
     */
    public static int getOriWorkMode() {
        return oriWorkMode;
    }

    /**设置原始工作状态
     */
    public static void setOriWorkMode(int workMode) {
        oriWorkMode = workMode;
    }

    public static int getWorkMode() {
        return workMode;
    }


    /**释放本地网络资源，即断开本地的连接和监听
     * @param tcpSend 网络连接
     * @param tcpMaps 网络连接映射
     */
    public static void releatLocalNet(TcpSend tcpSend, Map<String, TcpSend> tcpMaps) {
        if (tcpSend != null) {
            tcpSend.disConnect();
        }
        if (tcpMaps != null) {
            for (TcpSend ts :
                    tcpMaps.values()) {
                ts.disConnect();
            }
        }
    }

}
