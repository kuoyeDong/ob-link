package com.onbright.oblink.local.helper;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.Share;
import com.onbright.oblink.Obox;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.net.NetUtil;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.ParseUtil;
import com.onbright.oblink.local.net.Respond;
import com.onbright.oblink.local.net.TcpSend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地模式获取obox数据，使用本地帮助包中的类都应确保先使用过此类获取本地的配置。
 * <p>
 * 使用时请连接obox的wifi热点、或obox所属网段路由器热点。
 * 成功初始化后包含网段内OBOX数据包括：obox对象与对应连接对象map、节点配置、组关系配置、场景配置。
 * <p>
 * 使用者应该实现{@link com.onbright.oblink.local.net.Respond}接口，调用{@link Watcher#registWatcher(Respond)}注册监听，并在{@link com.onbright.oblink.local.net.Respond#onReceive(Message)}
 * 中调用本类的{@link #onReceive(Message)}以执行本类提供的交互逻辑处理。要移除监听，请调用{@link Watcher#unRegistWatcher(Respond)}。
 * <p>
 * 要获取obox内配置，请使用{@link #InitConfigHelper(Context)}构造对象，调用{@link #startGetConfig()}开始获取配置，
 * 注意此方法涉及大量的连续的网络交互，在收到回调之前请不要使用涉及网络交互的方法，成功获得配置后回调{@link #finishInit(Map, List, List, List, Map, Map, Map)},
 * 请注意在连接路由器中没找到obox的情况下，回调实参容器内没有数据。
 * <p>
 * 此方法没有超时回调，因为在成功之前，将一直尝试获取，直到成功为止，要停止，请调用{@link #stop()},停止后将回调{@link #finishInit(Map, List, List, List, Map, Map, Map)}，
 * 但不对配置数据的完整性做保证。
 */

public abstract class InitConfigHelper {
    private TcpSend mTcpSend;
    private Map<String, TcpSend> mTcpSendMap = new HashMap<>();
    private Context mContext;
    private NetUtil netUtil;
    private List<String> oboxIps = new ArrayList<>();
    private int oboxIpIdx;
    private boolean isReqOboxMsg;

    private List<String> oboxSSIDs = new ArrayList<>();

    private Obox obox;
    private List<Obox> oboxs = new ArrayList<>();

    private List<ObNode> obNodes;
    private List<ObGroup> obGroups;
    private List<ObScene> obScenes;
    private Map<String, List<ObNode>> obNodeMap = new HashMap<>();
    private Map<String, List<ObGroup>> obGroupMap = new HashMap<>();
    private Map<String, List<ObScene>> obSceneMap = new HashMap<>();

    private boolean isGetStatus;


    private boolean wantStop;


    public InitConfigHelper(Context mContext) {
        this.mContext = mContext;
        netUtil = new NetUtil(mContext) {
            @Override
            public void onWifiDisAble() {

            }
        };
        netUtil.ckNet();
    }


    /**
     * 停止获取配置
     */
    public void stop() {
        wantStop = true;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case OBConstant.NetState.ON_DSFINISH:
                case OBConstant.NetState.ON_DSFINISH_OBOX:
                    if (oboxIps.size() > 0) {
                        reqOboxMsg(oboxIpIdx);
                    } else {
                        finishInit(mTcpSendMap, oboxIps, oboxSSIDs, oboxs, obNodeMap, obGroupMap, obSceneMap);
                    }
                    break;
            }
        }
    };

    /**
     * 获取参数结束时回调
     *
     * @param mTcpSendMap 当前连接对象map
     * @param oboxIps     包含当前obox的ip地址list
     * @param oboxSSIDs   包含当前obox的list
     * @param oboxs       包含obox的列表
     * @param obNodeMap   包含初始化的节点容器，key为以上obox的序列号getObox_serial_id
     * @param obGroupMap  包含初始化的组容器,key为以上obox的序列号getObox_serial_id
     * @param obSceneMap  包含初始化的情景容器,key为以上obox的序列号getObox_serial_id
     */
    public abstract void finishInit(Map<String, TcpSend> mTcpSendMap,
                                    List<String> oboxIps,
                                    List<String> oboxSSIDs,
                                    List<Obox> oboxs,
                                    Map<String, List<ObNode>> obNodeMap,
                                    Map<String, List<ObGroup>> obGroupMap,
                                    Map<String, List<ObScene>> obSceneMap);


    /**
     * 开始获取配置
     */
    public void startGetConfig() {
        oboxs.clear();
        oboxSSIDs.clear();
        oboxIps.clear();
        obNodeMap.clear();
        obGroupMap.clear();
        obSceneMap.clear();
        NetUtil.releatLocalNet(mTcpSend, mTcpSendMap);
        oboxIpIdx = 0;
        switch (NetUtil.getWorkMode()) {
            case OBConstant.NetState.ON_AP:
                oboxIps.add("192.168.2.3");
                reqOboxMsg(0);
                break;

            case OBConstant.NetState.ON_STATION:
                netUtil.udpBc(mContext, handler, 3000);
                netUtil.dscvObox(oboxIps);
                break;

            case OBConstant.NetState.ON_CLOUD:
                break;
            default:
                break;
        }

    }

    private void reqOboxMsg(int index) {
        mTcpSend = new TcpSend(oboxIps.get(index), Watcher.getInstance().getHandler());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mTcpSend.connect()) {
                    isReqOboxMsg = true;
                    mTcpSend.reqOboxMsg();
                }
            }
        }).start();
    }


    /**
     * 请在onReceive的时候调用
     *
     * @param message 回调参数message
     */
    public void onReceive(Message message) {
        if (wantStop) {
            finishInit(mTcpSendMap, oboxIps, oboxSSIDs, oboxs, obNodeMap, obGroupMap, obSceneMap);
            return;
        }
        switch (message.what) {
            case OBConstant.ReplyType.GET_OBOX_NAME_BACK:
                ParseUtil.getOboxId(message, obox,Share.getSp(mContext), mTcpSend, oboxSSIDs,mContext,false);
                //noinspection deprecation
                mTcpSend.setOboxTime(new Time());
                break;
            case OBConstant.ReplyType.ON_SETOBOXTIME_SUC:
            case OBConstant.ReplyType.ON_SETOBOXTIME_FAL:
                mTcpSend.getDevice(1, false);
                break;
            case OBConstant.ReplyType.GET_OBOX_MSG_BACK:
                isReqOboxMsg = false;
                obox = new Obox();
                ParseUtil.parseObox(message, obox, mTcpSend, Share.getSp(mContext));
                mTcpSendMap.put(obox.getObox_serial_id(), mTcpSend);
                oboxs.add(obox);
                obNodes = new ArrayList<>();
                obNodeMap.put(obox.getObox_serial_id(), obNodes);
                obGroups = new ArrayList<>();
                obGroupMap.put(obox.getObox_serial_id(), obGroups);
                obScenes = new ArrayList<>();
                obSceneMap.put(obox.getObox_serial_id(), obScenes);
                mTcpSend.reqOboxSSID();
                break;
            case OBConstant.ReplyType.GET_SINGLENODE_BACK:
                if (ParseUtil.parseDevice(message, obNodes, mTcpSend)) {
                    if (obNodes.size() == 0) {
                        onOboxConfigFinish();
                    } else {
                        mTcpSend.getDevice(1, true);
                    }
                }
                break;

            case OBConstant.ReplyType.GET_GROUP_BACK:
                if (ParseUtil.parseGroup(message, obGroups, mTcpSend)) {
                    if (obNodes.size() == 0) {
                        onOboxConfigFinish();
                    } else {
                        isGetStatus = true;
                        mTcpSend.getDeviceState(obNodes.get(0).getCplAddr(), new byte[2]);
                    }
                }
                break;
            case OBConstant.ReplyType.GET_STATE:
                if (ParseUtil.parseDeviceState(message, obNodes, mTcpSend)) {
                    MathUtil.nodeFindGroup(obGroups, obNodes, false);
                    mTcpSend.reqScene(ObScene.OBSCENE_ID, 0, 1);
                }
                break;
            case OBConstant.ReplyType.GET_SCENE_BACK:
                if (ParseUtil.parseScene(message, obScenes, obNodes, obGroups, mTcpSend)) {
                    onOboxConfigFinish();
                }
                break;
            case OBConstant.ReplyType.WRONG_TIME_OUT:
                if (isGetStatus) {
                    int index = mTcpSend.getGetStatusIndex();
                    if (index < obNodes.size()) {
                        mTcpSend.getDeviceState(obNodes.get(index).getCplAddr(), new byte[2]);
                    } else {
                        isGetStatus = false;
                        MathUtil.nodeFindGroup(obGroups, obNodes, false);
                        mTcpSend.reqScene(ObScene.OBSCENE_ID, 0, 1);
                    }
                }
                break;
            case OBConstant.ReplyType.WRONG_CRC:
                if (isReqOboxMsg) {
                    supplementReqOboxMsg();
                }
                break;
            case OBConstant.ReplyType.NOT_REPLY:
                mTcpSend.sendBefore();
                break;
        }
    }

    /**
     * 前导码出错会回复crc校验错误，此时用正确方式补充拿取obox信息
     */
    private void supplementReqOboxMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isReqOboxMsg = true;
                mTcpSend.reqOboxMsg();
            }
        }).start();
    }

    private void onOboxConfigFinish() {
        oboxIpIdx++;
        if (oboxIpIdx < oboxIps.size()) {
            reqOboxMsg(oboxIpIdx);
        } else {
            finishInit(mTcpSendMap, oboxIps, oboxSSIDs, oboxs, obNodeMap, obGroupMap, obSceneMap);
        }
    }
}
