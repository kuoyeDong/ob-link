package com.onbright.oblink.local.helper;

import android.os.Message;

import com.onbright.oblink.StringUtil;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.ParseUtil;
import com.onbright.oblink.local.net.Respond;
import com.onbright.oblink.local.net.TcpSend;

/**
 * 节点分组管理包括创建组，删除组，把节点添加到组、从组内移除节点、节点重命名、组重命名的类。分组即为单节点设定组结构，以便于在状态控制的时候更便捷。
 * <p>
 * 使用者应该实现{@link com.onbright.oblink.local.net.Respond}接口，调用{@link Watcher#registWatcher(Respond)}注册监听，并在{@link com.onbright.oblink.local.net.Respond#onReceive(Message)}
 * 中调用本类的{@link #onReceive(Message)}以执行本类提供的交互逻辑处理。要移除监听，请调用{@link Watcher#unRegistWatcher(Respond)}。
 * <p>
 * 如果要创建组，请使用{@link #creatGroup(String)},请注意组id的规范。成功则回调{@link #onEditNodeOrGroupSuc(ObNode, ObGroup)。
 * <p>
 * 如果要删除组，请使用{@link #removeGroup(ObGroup)},成功则回调{@link #onEditNodeOrGroupSuc(ObNode, ObGroup)。
 * <p>
 * 如果要修改节点名称，请使用{@link #reNameNode(ObNode, String)},成功则回调{@link #onEditNodeOrGroupSuc(ObNode, ObGroup)}。
 * <p>
 * 如果要修改节点名称，请使用{@link #reNameGroup(ObGroup, String)} ,成功则回调{@link #onEditNodeOrGroupSuc(ObNode, ObGroup)}。
 * <p>
 * 如过要添加节点到组，请使用{@link #addNodeFromGroup(ObNode, ObGroup)},成功则回调{@link #onAddOrRemoveNodeFromGroupSuc(ObNode, ObGroup)}。
 * <p>
 * 如果要把节点从某个组删除，请使用{@link #removeNodeFromGroup(ObNode, ObGroup)},成功则回调{@link #onAddOrRemoveNodeFromGroupSuc(ObNode, ObGroup)}。
 * <p>
 * 本类任何失败操作都会回调{@link #onFailed(ObNode, ObGroup)}。
 * <p>
 */

public abstract class OrganizationHelper {

    private TcpSend mTcpSend;
    private ObNode mObNode;
    private ObGroup mObGroup;
    private boolean isSingle;

    public OrganizationHelper(TcpSend mTcpSend) {
        this.mTcpSend = mTcpSend;
    }

    /**
     * 创建组
     *
     * @param groupId 不包含特殊符号，总字节数不超过16，如果不符合规范将不会触发交互而回调{@link #onWrongId()}
     */
    public void creatGroup(String groupId) {
        isSingle = false;
        if (!StringUtil.isLegit(groupId, 0, 16)) {
            onWrongId();
            return;
        }
        ObGroup obGroup = new ObGroup();
        obGroup.setId(groupId.getBytes());
        mObGroup = obGroup;
        mTcpSend.editNodeorGroup(1, null, obGroup, groupId.getBytes(), true);
    }

    /**
     * 删除组
     *
     * @param obGroup 要删除的组
     */
    public void removeGroup(ObGroup obGroup) {
        isSingle = false;
        mObNode = null;
        mObGroup = obGroup;
        mTcpSend.editNodeorGroup(0, null, obGroup, new byte[8], true);
    }

    /**
     * 添加某节点到某组
     *
     * @param obNode  要添加的节点
     * @param obGroup 要添加到的组
     */
    public void addNodeFromGroup(ObNode obNode, ObGroup obGroup) {
        mObNode = obNode;
        mObGroup = obGroup;
        mTcpSend.organizGoup(obNode, obGroup, true);
    }

    /**
     * 从某组删除某节点
     *
     * @param obNode  要删除的节点
     * @param obGroup 被删除节点的组
     */
    public void removeNodeFromGroup(ObNode obNode, ObGroup obGroup) {
        mObNode = obNode;
        mObGroup = obGroup;
        mTcpSend.organizGoup(obNode, obGroup, false);
    }

    /**
     * 重命名单节点，如名称不符合规范则不会发送指令
     *
     * @param obNode    要重命名的单节点
     * @param newNodeId 新名称，不得包含特殊字符以及超过16个字节
     */
    public void reNameNode(ObNode obNode, String newNodeId) {
        if (!StringUtil.isLegit(newNodeId, 0, 16)) {
            onWrongId();
            return;
        }
        isSingle = true;
        mObNode = obNode;
        mObGroup = null;
        mTcpSend.editNodeorGroup(2, obNode, null, newNodeId.getBytes(), !isSingle);
    }

    /**
     * 重命名组，如名称不符合规范则不会发送指令
     *
     * @param obGroup    要重命名的组
     * @param newGroupId 新名称，不得包含特殊字符以及超过16个字节
     */
    public void reNameGroup(ObGroup obGroup, String newGroupId) {
        if (!StringUtil.isLegit(newGroupId, 0, 16)) {
            onWrongId();
            return;
        }
        isSingle = false;
        mObNode = null;
        mObGroup = obGroup;
        mTcpSend.editNodeorGroup(2, null, obGroup, newGroupId.getBytes(), !isSingle);
    }

    /**
     * 请在onReceive调用
     *
     * @param message 网络回调参数
     */
    public void onReceive(Message message) {
        switch (message.what) {
            case OBConstant.ReplyType.ON_ORGNZ_GROUP_SUC:
                ParseUtil.onOrganizGoup(mObNode, mObGroup, message);
                onAddOrRemoveNodeFromGroupSuc(mObNode, mObGroup);
                break;
            /*组重命名， 单节点重命名，删除组,新增组*/
            case OBConstant.ReplyType.EDIT_NODE_OR_GROUP_SUC:
                ParseUtil.onEditNodeOrGroupSuc(message, isSingle, mObNode, mObGroup, null, null);
                onEditNodeOrGroupSuc(mObNode, mObGroup);
                break;
            case OBConstant.ReplyType.EDIT_NODE_OR_GROUP_FAL:
            case OBConstant.ReplyType.ON_ORGNZ_GROUP_FAL:
                onFailed(mObNode, mObGroup);
                break;
            case OBConstant.ReplyType.NOT_REPLY:
                onNotReply();
        }
    }

    /**
     * 没回复则回调
     */
    public abstract void onNotReply();

    /**
     * 任何操作失败回调此方法
     *
     * @param mObNode  操作的单节点
     * @param mObGroup 操作的组
     */
    public abstract void onFailed(ObNode mObNode, ObGroup mObGroup);

    /**
     * 组重命名， 单节点重命名，删除组,新增组操作成功后回调此方法
     *
     * @param mObNode  操作的单节点
     * @param mObGroup 操作的组
     */
    public abstract void onEditNodeOrGroupSuc(ObNode mObNode, ObGroup mObGroup);

    /**
     * 添加节点到组或者从组内删除节点成功后回调此方法，注意在此方法执行之前已经根据操作在mObGroup添加或者删除mObNode
     *
     * @param mObNode  操作的单节点
     * @param mObGroup 操作的组
     */
    public abstract void onAddOrRemoveNodeFromGroupSuc(ObNode mObNode, ObGroup mObGroup);

    /**
     * 任何时候传入id错误回调此方法并且不会发送指令
     */
    public abstract void onWrongId();

}
