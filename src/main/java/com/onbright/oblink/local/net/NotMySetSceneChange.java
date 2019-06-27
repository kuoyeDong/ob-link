package com.onbright.oblink.local.net;

import android.content.Context;
import android.content.Intent;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.bean.SceneAction;

import java.util.Arrays;
import java.util.List;

/**
 * 非本机操作的情景增删改数据处理
 * Created by adolf_dong on 2017/9/1.
 */
@SuppressWarnings("WeakerAccess")
public class NotMySetSceneChange {
    private Context context;
    private String oboxSer;

    public NotMySetSceneChange(Context context) {
        this.context = context;
    }

    private static int[] index = new int[65];

    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }

    /**
     * 设置对应的连接
     *
     * @param oboxSer obox序列号
     */
    public void setPaseUpLocad(String oboxSer) {
        this.oboxSer = oboxSer;
    }

    /**
     * 情景列表
     */
    private List<ObScene> obScenes;

    /**
     * 操作节点列表
     */
    private List<ObNode> obNodes;
    /**
     * 当前连接的组列表
     */
    private List<ObGroup> obGroups ;

    /**
     * @param bytes obox读取数据
     */
    public void onSceneChange(byte[] bytes) {
        if (obScenes == null) {
            obScenes = LocalDataPool.newInstance().getObSceneMap().get(oboxSer);
        }
        if (obNodes == null) {
            obNodes = LocalDataPool.newInstance().getObnodesForOneObox(oboxSer);
        }
        if (obGroups == null) {
            obGroups = LocalDataPool.newInstance().getObGroupMap().get(oboxSer);
        }
        if (obScenes == null) {
            return;
        }
        int serNum = bytes[index[11]];
        switch (bytes[index[8]]) {
            case OBConstant.ReplyType.SUC:
                if (bytes[index[9]] == ObScene.OBSCENE_ID && MathUtil.byteIndexValid(bytes[index[10]], 4, 2) == ObScene.EXUTE) {
//                    msg.what = OBConstant.ReplyType.ON_EXCUTE_SCENE_SUC;
                    /*执行场景成功之后的数据刷新*/
                    ObScene obScene = findObScene(serNum);
                    updateStateForAction(obScene);
                } else {
//                    msg.what = OBConstant.ReplyType.ON_SETSCENE_SUC;
                    ObScene obScene = null;
                    int operaType = bytes[index[9]];
                    switch (operaType) {
                        /*情景id相关*/
                        case ObScene.OBSCENE_ID:
                              /*操作详情*/
                            int operaOption = MathUtil.byteIndexValid(bytes[index[10]], 0, 4);
                            switch (operaOption) {
                                /*创建情景new对象*/
                                case ObScene.CRETE:
                                    obScene = new ObScene();
                                    break;
                                /*删除和修改查找*/
                                case ObScene.DELETE:
                                case ObScene.MODIFY:
                                    obScene = findObScene(serNum);
                                    break;
                            }
                            if (obNodes == null) {
                                return;
                            }
                            if (obScene == null) {
                                return;
                            }
                            ParseUtil.onEditScene(false, obScene, obScenes, null, bytes, obNodes);
                            break;
                        case ObScene.OBSCENE_CONDITION:
                            obScene = findObScene(serNum);
                            ParseUtil.onEditScene(false, obScene, obScenes, null, bytes, obNodes);
                            break;

                        case ObScene.OBSCENE_ACTION:
                            obScene = findObScene(serNum);
                            if (obScene == null) {
                                return;
                            }
                            boolean needHandle = true;
                            //noinspection ConstantConditions
                            if (needHandle) {
                                for (int i = 0; i < 3; i++) {
                                    byte[] addr = new byte[7];
                                    byte[] action = new byte[8];
                                    System.arraycopy(bytes, 0, addr, index[11] + i * 15, 7);
                                    System.arraycopy(bytes, 0, action, index[11] + i * 15 + 7, 8);
                                    /*全0则不操作*/
                                    if (MathUtil.byteArrayIsZero(addr)) {
                                        continue;
                                    }
                                    switch (MathUtil.byteIndexValid(bytes[index[10]], 2 * i, 2)) {
                                        /*删除*/
                                        case ObScene.ACTION_DELETE:
                                            List<SceneAction> sceneActions = obScene.getObNodes();
                                            for (int j = 0; j < sceneActions.size(); j++) {
                                                SceneAction sceneAction = sceneActions.get(j);
                                                if (Arrays.equals(sceneAction.getAddrs(), addr)) {
                                                    sceneActions.remove(j);
                                                    break;
                                                }
                                            }
                                            break;
                                        /*修改和新增*/
                                        case ObScene.ACTION_MODIFY:
                                            sceneActions = obScene.getObNodes();
                                            boolean isFind = false;
                                            for (int j = 0; j < sceneActions.size(); j++) {
                                                SceneAction sceneAction = sceneActions.get(j);
                                                /*修改*/
                                                if (Arrays.equals(sceneAction.getAddrs(), addr)) {
                                                    sceneAction.putAction(serNum, action);
                                                    isFind = true;
                                                    break;
                                                }
                                            }
                                            /*新增*/
                                            if (!isFind) {
                                                /*组地址为0则为单节点*/
                                                if (addr[index[5]] == 0) {
                                                    for (int j = 0; j < obNodes.size(); j++) {
                                                        ObNode obNode = obNodes.get(j);
                                                        if (obNode.getAddr() == addr[index[6]]) {
                                                            obNode.putAction(serNum, action);
                                                            sceneActions.add(obNode);
                                                        }
                                                    }
                                                } else {
                                                    for (int j = 0; j < obGroups.size(); j++) {
                                                        ObGroup obGroup = obGroups.get(j);
                                                        if (obGroup.getAddr() == addr[index[5]]) {
                                                            obGroup.putAction(serNum, action);
                                                            sceneActions.add(obGroup);
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        /*保持不变*/
                                        case ObScene.ACTION_HOLD:
                                            break;
                                    }
                                }
                            }
                            break;
                    }
                }
                break;
        }
    }

    /**
     * 查找目标情景
     *
     * @param serNum 情景序号
     * @return 目标情景
     */
    private ObScene findObScene(int serNum) {
        for (int i = 0; i < obScenes.size(); i++) {
            ObScene obSceneFind = obScenes.get(i);
            if (obSceneFind.getSerisNum() == serNum) {
                return obSceneFind;
            }
        }
        return null;
    }

    /**
     * 情景触发更新action对应的状态
     *
     * @param obScene 触发的情景
     */
    private void updateStateForAction(ObScene obScene) {
        List<SceneAction> actions = obScene.getObNodes();
        for (SceneAction sa : actions) {
            if (sa instanceof ObNode) {
                ObNode obNode = (ObNode) sa;
                byte[] action = obNode.getActions(obScene.getSerisNum());
                obNode.setState(action);
                sendBroadUpdateNodeState(obNode);
            }
            if (sa instanceof ObGroup) {
                ObGroup og = (ObGroup) sa;
                byte[] action = og.getActions(obScene.getSerisNum());
                og.setStatus(action);
            }
        }
    }


    /**
     * 发送更新状态通知
     *
     * @param obNode 被更新的节点
     */
    private void sendBroadUpdateNodeState(ObNode obNode) {
        Intent broadIntent = new Intent();
        broadIntent.setAction(OBConstant.StringKey.UPDATE_OBOX_STATUS);
        broadIntent.putExtra("state", Transformation.byteArryToHexString(obNode.getState()));
        broadIntent.putExtra("serialId", Transformation.byteArryToHexString(obNode.getSerNum()));
        context.sendBroadcast(broadIntent);
    }
}
