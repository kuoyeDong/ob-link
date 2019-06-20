package com.onbright.oblink.local.helper;

import android.os.Message;

import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.ParseUtil;
import com.onbright.oblink.local.net.Respond;
import com.onbright.oblink.local.net.TcpSend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 提供扫描节点、停止扫面节点、释放单个节点、以及释放obox内所有节点功能
 * <p>
 * 使用者应该实现{@link com.onbright.oblink.local.net.Respond}接口，调用{@link Watcher#registWatcher(Respond)}注册监听，并在{@link com.onbright.oblink.local.net.Respond#onReceive(Message)}
 * 中调用本类的{@link #onReceive(Message)}以执行本类提供的交互逻辑处理。要移除监听，请调用{@link Watcher#unRegistWatcher(Respond)}。
 * <p>
 * 如果要扫描节点，请使用{@link #startScan(boolean, int, int)},开启成功则回调{@link #onScanSuc()},之后有节点入网时即回调{@link #onGetNewNode(ObNode)},
 * 请注意在扫描持续时间内，obox对除{@link #stopScan()}的指令将不做处理，直到设定扫描的持续时间到达后，或者调用{@link #stopScan()}并回调{@link #onStopScanSuc(List)}为止。
 * <p>
 * 如果要停止扫描节点，请使用{@link #stopScan()},成功则回调{@link #onStopScanSuc(List)},回调将返回本次扫描获新入网的节点。
 * <p>
 * 如果要删除某个节点，请使用{@link #removeNode(ObNode, Map)},成功则回调{@link #onRemoveNodeSuc(ObNode)}。
 * <p>
 * 如果要释放所有节点，请使用{@link #freedAllNode()},成功则回调{@link #onRealeaseSuc()}。
 * <p>
 * 所有交互失败都会回调{@link #onFialed()},操作没收到回复则会回调{@link #onNotReply()}。
 */

public abstract class ScanAndReleaseHelper {
    private TcpSend mTcpSend;
    private List<ObNode> obNodes = new ArrayList<>();
    private ObNode mObNode;
    private Map<String, List<ObNode>> mObNodeMap;

    public ScanAndReleaseHelper(TcpSend mTcpSend) {
        this.mTcpSend = mTcpSend;
    }

    /**
     * 开始扫描节点,两次扫描间隔时间请不要超过10s
     *
     * @param forceScan 扫描模式是否强制扫描，强制扫描即不必给节点重新上电也可被扫入
     * @param idex      此参数将决定此次扫描入网节点id的起始位置，即以此为起点往后分配
     * @param time      扫描持续时间
     * @return 扫描指令发送是否成功，仅是发送成功
     */
    public boolean startScan(boolean forceScan, int idex, int time) {
        obNodes.clear();
        if (idex > Math.pow(2, 24) - 1) {
            return false;
        }
        byte[] startId = new byte[3];
        for (int i = 0; i < 3; i++) {
            startId[i] = (byte) (idex >> 8 * i);
        }
        mTcpSend.setMustRec(true);
        mTcpSend.rfCmd(forceScan ? 2 : 1, startId, time);
        return true;
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        mTcpSend.setMustRec(false);
        mTcpSend.rfCmd(0, new byte[3], 0);
    }

    /**
     * 释放obox的所有节点
     */
    public void freedAllNode() {
        mTcpSend.release();
    }


    /**
     * 从obox内移除单个节点
     *
     * @param obNode    要移除网络的节点
     * @param obNodeMap 移除网络的节点所在容器，成功后会在此容器删除,可为null
     */
    public void removeNode(ObNode obNode, Map<String, List<ObNode>> obNodeMap) {
        mObNode = obNode;
        mObNodeMap = obNodeMap;
        mTcpSend.editNodeorGroup(0, obNode, null, null, false);
    }

    /**
     * 请在onResPond中的onRecieve中调用
     *
     * @param message 回调携带参数message
     */
    public void onRecieve(Message message) {
        switch (message.what) {
            case OBConstant.ReplyType.FORCE_SEARCH_SUC:
                onScanSuc();
                break;

            case OBConstant.ReplyType.ON_GET_NEW_NODE:
                ParseUtil.parseNewNode(message, obNodes);
                int i = obNodes.size() - 1;
                ObNode obNode = obNodes.get(i);
                onGetNewNode(obNode);
                break;

            case OBConstant.ReplyType.ON_REALEASE_SUC:
                onRealeaseSuc();
                break;
            case OBConstant.ReplyType.STOP_SEARCH_SUC:
                onStopScanSuc(obNodes);
                break;
            case OBConstant.ReplyType.EDIT_NODE_OR_GROUP_SUC:
                ParseUtil.onEditNodeOrGroupSuc(message, true, mObNode, null, mObNodeMap, null);
                onRemoveNodeSuc(mObNode);
                break;
            case OBConstant.ReplyType.EDIT_NODE_OR_GROUP_FAL:
            case OBConstant.ReplyType.ON_REALEASE_FAL:
            case OBConstant.ReplyType.FORCE_SEARCH_FAL:
            case OBConstant.ReplyType.SEARCH_NODE_FAL:
            case OBConstant.ReplyType.STOP_SEARCH_FAL:
                onFialed();
            case OBConstant.ReplyType.NOT_REPLY:
                onNotReply();
                break;
        }
    }

    public abstract void onNotReply();

    /**
     * 删除单节点成功回调
     *
     * @param mObNode 被删除的节点
     */
    public abstract void onRemoveNodeSuc(ObNode mObNode);


    /**
     * obox返回开始扫描成功
     */
    public abstract void onScanSuc();

    /**
     * obox返回停止扫描成功
     *
     * @param obNodes 包含本次扫描所有新入网节点的list
     */
    public abstract void onStopScanSuc(List<ObNode> obNodes);

    /**
     * 扫描到新节回调
     *
     * @param obNode 新扫描到的节点
     */
    public abstract void onGetNewNode(ObNode obNode);

    /**
     * 释放obox内的所有节点成功
     */
    public abstract void onRealeaseSuc();

    /**
     * 开始扫描，停止扫描，释放节点失败回调
     */
    public abstract void onFialed();

}
