package com.onbright.oblink.local.bean;

import com.onbright.oblink.MathUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地情景
 * creat by adolf_dong
 */
public class ObScene implements Serializable {
    /*设置类型*/
    public static final int OBSCENE_ID = 1;
    public static final int OBSCENE_CONDITION = 2;
    public static final int OBSCENE_ACTION = 3;


    /*使能状态  0 不使能  1 使能 2立即生效*/
    public static final int DISABLE = 0;
    public static final int ENABLE = 1;
    public static final int EXUTE = 2;

    /*0 删除  1新增（序号传0） 2修改id*/
    public static final int DELETE = 0;
    public static final int CRETE = 1;
    public static final int MODIFY = 2;


    /*0 删除行为  2 修改行为 3不对行为进行操作*/
    public static final int ACTION_DELETE = 0;
    public static final int ACTION_MODIFY = 2;
    public static final int ACTION_HOLD = 3;

    /**
     * 编号， 255表示该情景是当前obox中最后一个情景
     */
    private int num;
    /**
     * 序列号
     */
    private int serisNum;

    public int getSceneGroup() {
        return sceneGroup;
    }

    public void setSceneGroup(int sceneGroup) {
        this.sceneGroup = sceneGroup;
    }

    /**
     * 情景tag，tag为键，与前节点addr对应
     */
    private int sceneGroup;

    public void setId(byte[] id) {
        this.id = id;
    }

    /**
     * id
     */
    private byte[] id;


    /**
     * 使能状态
     */
    private boolean isEnable;

    /**
     * 情景条件，除了可以为正常节点外还可以是定时和遥控器
     */
    private List<List<SceneCondition>> sceneCondition;

    /**
     *
     */
    private List<SceneAction> obNodes;


    /**
     * 就是obox的序列号，在序列号内容为空的时候使用此方法查找属于哪个obox
     */
    private String rfAddr;

    public ObScene(int serisNum, byte[] id, boolean isEnable, int num) {
        this.serisNum = serisNum;
        this.id = MathUtil.validArray(id);
        this.isEnable = isEnable;
        this.num = num;
    }

    public ObScene() {

    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getRfAddr() {
        return rfAddr;
    }

    public void setRfAddr(String rfAddr) {
        this.rfAddr = rfAddr;
    }

    public List<ObNode> getSingleAction() {
        List<ObNode> sceneActions = new ArrayList<>();
        if (obNodes == null) {
            return sceneActions;
        }
        for (SceneAction sceneAction : obNodes) {
            if (sceneAction instanceof ObNode) {
                sceneActions.add((ObNode) sceneAction);
            }
        }
        return sceneActions;
    }

    public List<ObGroup> getGroupAction() {
        List<ObGroup> sceneActions = new ArrayList<>();
        if (obNodes == null) {
            return sceneActions;
        }
        for (SceneAction sceneAction : obNodes) {
            if (sceneAction instanceof ObGroup) {
                sceneActions.add((ObGroup) sceneAction);
            }
        }
        return sceneActions;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getSerisNum() {
        return serisNum;
    }

    /**
     * 设置行为的action
     *
     * @param newSerNum 新场景序号
     */
    public void setSerisNum(int newSerNum) {
        if (obNodes != null) {
            for (SceneAction sceneAction :
                    obNodes) {
                byte[] action = sceneAction.getActions(serisNum);
                sceneAction.putAction(newSerNum, action);
            }
        }
        if (sceneCondition != null) {
            for (List<SceneCondition> sceneConditions : sceneCondition) {
                for (SceneCondition sc :
                        sceneConditions) {
                    byte[] cdt = sc.getCondition("" + serisNum);
                    sc.setCondition(""+newSerNum,cdt);
                }
            }
        }
        this.serisNum = newSerNum;
    }

    public List<List<SceneCondition>> getSceneCondition() {
        if (sceneCondition == null) {
            sceneCondition = new ArrayList<>();
        }
        return sceneCondition;
    }

    public void setSceneCondition(List<List<SceneCondition>> sceneCondition) {
        this.sceneCondition = sceneCondition;
    }

    public List<SceneAction> getObNodes() {
        if (obNodes == null) {
            obNodes = new ArrayList<>();
        }
        return obNodes;
    }

    public void setObNodes(List<SceneAction> obNodes) {
        this.obNodes = obNodes;
    }

    public byte[] getSceneId() {
        return id;
    }

    /**
     * 设置整个类型设备的action
     *
     * @param modifyActionPtype 类型
     * @param modifyActionType  子类型
     * @param action            新action
     */
    public void setActionForType(int modifyActionPtype, int modifyActionType, byte[] action) {
        for (int i = 0; i < obNodes.size(); i++) {
            SceneAction sceneAction = obNodes.get(i);
            if (sceneAction instanceof ObNode) {
                ObNode obNode = (ObNode) sceneAction;
                if (obNode.getParentType() == modifyActionPtype && obNode.getType() == modifyActionType) {
                    obNode.putAction(serisNum, action);
                }
            } else if (sceneAction instanceof ObGroup) {
                ObGroup obGroup = (ObGroup) sceneAction;
                if (obGroup.getGroupPType() == modifyActionPtype && obGroup.getGroupType() == modifyActionType) {
                    obGroup.putAction(serisNum, action);
                    for (int j = 0; j < obGroup.getObNodes().size(); j++) {
                        ObNode obNode = obGroup.getObNodes().get(j);
                        obNode.putAction(serisNum, action);
                    }
                }
            }
        }
    }
}
