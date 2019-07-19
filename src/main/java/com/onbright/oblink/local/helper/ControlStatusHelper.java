package com.onbright.oblink.local.helper;

import android.os.Message;

import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.ParseUtil;
import com.onbright.oblink.local.net.Respond;
import com.onbright.oblink.local.net.TcpSend;

/**
 * 设置节点、组状态帮助类，请注意，虽然本类没做限制，连续调用设置方法间隔请超过200ms，最好超过500ms，
 * <p>
 * 使用者应该实现{@link com.onbright.oblink.local.net.Respond}接口，调用{@link Watcher#registWatcher(Respond)}注册监听，并在{@link com.onbright.oblink.local.net.Respond#onReceive(Message)}
 * 中调用本类的{@link #onReceive(Message)}以执行本类提供的交互逻辑处理。要移除监听，请调用{@link Watcher#unRegistWatcher(Respond)}。
 * <p>
 * 如果要设置节点或者状态，请使用{@link #ControlStatusHelper(TcpSend, ObNode, ObGroup, boolean)}构造对象，
 * 再调用{@link #setNodeStatus(byte[])}触发tcp交互，请注意，如果是设置组，且组内节点为0，则不会触发交互并回调{@link #onEmptyGroup()},
 * 设置成功则回调{@link #onControlSuc(ObNode, ObGroup)},设置失败则回调{@link #onControlFailed(ObNode, ObGroup)}，
 * 设置后没回复则回调{@link #onNotReply()}。
 */
public abstract class ControlStatusHelper {

    private TcpSend mTcpSend;
    private ObNode mObNode;
    private ObGroup mObGroup;
    private boolean mIsGroup;

    /**
     * @param mTcpSend tcp连接对象
     * @param mObNode  单节点控制时传入单节点对象，组节点控制可传null
     * @param mObGroup 组节点控制传入组对象，单节点控制可传null
     * @param mIsGroup 是否控制组
     */
    public ControlStatusHelper(TcpSend mTcpSend, ObNode mObNode, ObGroup mObGroup, boolean mIsGroup) {
        this.mTcpSend = mTcpSend;
        this.mObNode = mObNode;
        this.mObGroup = mObGroup;
        this.mIsGroup = mIsGroup;
    }


    /**
     * 对应灯属性的参数设置位置，仅供参考，客户可自定义
     */
    public interface StatusGuide {
        /**
         * 亮度、彩色灯0-255，非彩色灯0或125-255
         */
        int LIGHT = 0;
        /**
         * 双色灯的冷色 0-255
         */
        int COOL = 1;
        /**
         * 双色灯的暖 0-255
         */
        int WARM = 2;
        /**
         * 彩灯红色数值 0-255
         */
        int RED = 3;
        /**
         * 彩灯绿色数值 0-255
         */
        int GREEN = 4;
        /**
         * 彩灯蓝色数值 0-255
         */
        int BLUE = 5;
        /**
         * 渐变时长，即状态改变的速度，此建议范围1-5，数值越大时间越长灯的变化看上去越柔和
         */
        int TIME = 6;
    }

    /**
     * 设置状态
     *
     * @param status 状态传入 七字节
     * @see ControlStatusHelper.StatusGuide
     */
    public void setNodeStatus(byte[] status) {
        if (mIsGroup) {
            if (mObGroup.getObNodes().size() == 0) {
                onEmptyGroup();
            } else {
                ObNode node = mObGroup.getObNodes().get(0);
                mTcpSend.setNodeState(node, status, true);
            }
        } else {
            mTcpSend.setNodeState(mObNode, status, false);
        }
    }


    /**
     * 在网络回复回调中调用
     *
     * @param message 传入message
     */
    public void onReceive(Message message) {
        switch (message.what) {
            case OBConstant.ReplyType.SET_STATUS_SUC:
                ParseUtil.onSetStatusRec(message, !mIsGroup, mObNode, mObGroup);
                onControlSuc(mObNode, mObGroup);
                break;
            case OBConstant.ReplyType.SET_STATUS_FAL:
                onControlFailed(mObNode, mObGroup);
                break;
            case OBConstant.ReplyType.NOT_REPLY:
                onNotReply();
        }
    }

    public abstract void onNotReply();

    /**
     * 设置状态成功的时候得到回调
     *
     * @param mObNode  设置的节点
     * @param mObGroup 设置的组
     */
    public abstract void onControlSuc(ObNode mObNode, ObGroup mObGroup);

    /**
     * 当传入的组节点为空的时候，不会发出设置节点状态的指令而回调此方法
     */
    public abstract void onEmptyGroup();

    /**
     * 设置状态失败的时候得到回调
     *
     * @param mObNode  设置的节点
     * @param mObGroup 设置的组
     */
    public abstract void onControlFailed(ObNode mObNode, ObGroup mObGroup);
}
