package com.onbright.oblink.local.helper;

import android.content.Context;
import android.os.Message;

import com.onbright.oblink.ParseCondition;
import com.onbright.oblink.StringUtil;
import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.local.bean.ActionReply;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.bean.SceneAction;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.bean.Timing;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.ParseUtil;
import com.onbright.oblink.local.net.Respond;
import com.onbright.oblink.local.net.TcpSend;
import com.onbright.oblink.local.net.Transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 情景管理类，根据OBOX内的设备创建、删除情景、设置情景条件、设置情景行为（不可跨越OBOX进行情景设置），且每个obox最多支持20个情景，超过该数量将会出现不可预知的错误。
 * <p>
 * 使用者应该实现{@link com.onbright.oblink.local.net.Respond}接口，调用{@link Watcher#registWatcher(Respond)}注册监听，并在{@link com.onbright.oblink.local.net.Respond#onReceive(Message)}
 * 中调用本类的{@link #onReceive(Message)}以执行本类提供的交互逻辑处理。要移除监听，请调用{@link Watcher#unRegistWatcher(Respond)}。
 * <p>
 * 除非仅仅要获取条件的语意化信息{@link #getConditionShowString(SceneCondition)}调用{@link #SceneHelper(Context)}构建对象之外，其他任何操作都应
 * 使用{@link #SceneHelper(Context, TcpSend, ObScene, List)}构造对象。
 * <p>
 * 要创建情景，请使用{@link #setSceneId(String)}设置情景名称，请注意名称的规范，如不符合规范将不会修改属性并回调{@link #onWrongId()}，
 * 使用{@link #setSceneGroup(int)}设置情景tag，此tag供上层组织情景的关系，可根据自己的思路使用。
 * 使用{@link #addCondition(int, int, int, int, int, int)}添加年月日时分条件，
 * 使用{@link #addConditionWithWeek(boolean, boolean[], int, int, int)}添加星期几、时分条件，
 * 使用{@link #modifyCondition(SceneCondition, int, int, int, int, int, int)}修改条件为年月日时分条件，
 * 使用{@link #modifyConditionWithWeek(SceneCondition, boolean, boolean[], int, int, int)}修改条件为星期几、时分条件，
 * 每个情景最多支持三个条件，超出将不会进行属性设置。
 * <p>
 * 使用{@link #addAction(byte[], SceneAction)}添加情景触发时的行为，使用{@link #modifyAction(SceneAction, byte[])}修改情景触发时的行为。
 * 请注意，以上操作都不会直接触发请求交互，请使用{@link #onSure()}触发交互请求，成功则回调{@link #finishOnsuc()}，失败回调{@link #onFialed()}。
 * <p>
 * 要删除情景，请使用{@link #remove()},删除成功则回调{@link #onRemoveSuc()},失败回调{@link #onFialed()}。
 * <p>
 * 要执行情景，请调用{@link #excute()}，执行成功则回调
 * <p>
 * 任何没有返回的超时都将回调{@link #onNotReply()}。
 */

public abstract class SceneHelper {

    private TcpSend mTcpSend;

    private ObScene obScene;

    private List<ObScene> obScenes;

    private List<List<SceneCondition>> sceneConditions;

    private List<SceneAction> sceneActions;

    private boolean isCreat;

    /**
     * @param context  Context
     * @param mTcpSend 当前连接
     * @param obScene  操作的场景，创建时候传入新建对象即可
     * @param obScenes 操作的场景所在的容器，或者创建的场景想要放置的容器
     */
    public SceneHelper(Context context, TcpSend mTcpSend, ObScene obScene, List<ObScene> obScenes) {
        this.context = context;
        this.mTcpSend = mTcpSend;
        this.obScene = obScene;
        isCreat = (obScene.getSerisNum() == 0);
        sceneConditions = obScene.getSceneCondition();
        sceneActions = obScene.getObNodes();
        if (isCreat) {
            obScene.setRfAddr(mTcpSend.getRfAddr());
        }
        copyActions(obScene);
        this.obScenes = obScenes;
    }

    /**
     * 复制原有action
     *
     * @param obScene 含有action数据源的情景
     */
    private void copyActions(ObScene obScene) {
        originalSceneAction = new ArrayList<>();
        List<SceneAction> sceneActions = obScene.getObNodes();
        for (int i = 0; i < sceneActions.size(); i++) {
            SceneAction sceneAction = sceneActions.get(i);
            if (sceneAction instanceof ObNode) {
                ObNode sourceObNode = (ObNode) sceneAction;
                ObNode obNode = new ObNode();
                obNode.setId(Arrays.copyOf(sourceObNode.getId(), sourceObNode.getId().length));
                obNode.setCplAddr(Arrays.copyOf(sourceObNode.getCplAddr(), sourceObNode.getCplAddr().length));
                obNode.putAction(obScene.getSerisNum(), Arrays.copyOf(sourceObNode.getActions(obScene.getSerisNum()),
                        sourceObNode.getActions(obScene.getSerisNum()).length));
                originalSceneAction.add(obNode);
            } else if (sceneAction instanceof ObGroup) {
                ObGroup sourceGroup = (ObGroup) sceneAction;
                ObGroup obGroup = new ObGroup();
                obGroup.setId(Arrays.copyOf(sourceGroup.getId(), sourceGroup.getId().length));
                obGroup.setRfAddrs(Arrays.copyOf(sourceGroup.getRfAddr(), sourceGroup.getRfAddr().length));
                obGroup.setAddr(sourceGroup.getAddr());
                obGroup.putAction(obScene.getSerisNum(), Arrays.copyOf(sourceGroup.getActions(obScene.getSerisNum()),
                        sourceGroup.getActions(obScene.getSerisNum()).length));
                originalSceneAction.add(obGroup);
            }
        }
    }


    /**
     * 是否可以为情景添加条件,设定最大支持的条件数量为3
     *
     * @return 可以添加条件返回true
     */
    public boolean canAddCondition() {
        return sceneConditions.size() < 3;
    }

    /**
     * 设置情景的名称
     *
     * @param id 名称，转换成字节数组后的长度不能超过16，不能包含特殊字符，此方法会对传入参数的合规性判断，如果不符合则会执行{@link #onWrongId()}
     */
    public void setSceneId(String id) {
        if (!StringUtil.isLegit(id, 0, 16)) {
            onWrongId();
            return;
        }
        obScene.setId(id.getBytes());
    }

    /**
     * 传入情景名称不合规回调
     */
    public abstract void onWrongId();

    /**
     * 按照星期循环为情景添加条件
     *
     * @param everday    是否每天执行，若选择每天执行则weekcirlce可传null
     * @param weekcircle 星期几的boolen数组，长度为7，数组元素为true则在此天会执行，设置方式参照{@link CircleHelper}
     * @param hour       小时，24h
     * @param min        分
     * @param zone       时区，中国为东八区则传8
     * @return 是否设置成功
     */
    public boolean addConditionWithWeek(boolean everday, boolean[] weekcircle, int hour, int min, int zone) {
        if (sceneConditions.size() < 3) {
            byte[] conditonbyte = new byte[8];
            if (everday) {
                conditonbyte[0] = (byte) 0x80;
            } else {
                int sum = mathWeekByte(weekcircle);
                conditonbyte[0] = (byte) sum;
            }
            conditonbyte[1] = (byte) zone;
            conditonbyte[5] = (byte) hour;
            conditonbyte[6] = (byte) min;
            SceneCondition sceneCondition = new Timing(conditonbyte);
            List<SceneCondition> sceneConditions = new ArrayList<>();
            sceneConditions.add(sceneCondition);
            this.sceneConditions.add(sceneConditions);
            return true;
        }
        return false;
    }

    /**
     * 根据星期循环方式修改情景的一个条件
     *
     * @param sceneCondition 要修改的条件
     * @param everday        是否每天，传true则weekcircle可为null
     * @param weekcircle     星期几的boolen数组，长度为7，数组元素为true则在此天会执行，设置方式参照CircleHelper
     * @param hour           小时，24h
     * @param min            分
     * @param zone           时区，中国为东八区则传8
     */
    public void modifyConditionWithWeek(SceneCondition sceneCondition,
                                        boolean everday, boolean[] weekcircle, int hour, int min, int zone) {
        byte[] conditonbyte = new byte[8];
        if (everday) {
            conditonbyte[0] = (byte) 0x80;
        } else {
            int sum = mathWeekByte(weekcircle);
            conditonbyte[0] = (byte) sum;
        }
        conditonbyte[1] = (byte) zone;
        conditonbyte[5] = (byte) hour;
        conditonbyte[6] = (byte) min;
        sceneCondition.setCondition(obScene.getSerisNum() + "", conditonbyte);
    }


    /**
     * 按照年月日为情景添加条件
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   小时
     * @param minute 分
     * @param zone   时区，中国为东八区则传8
     * @return 是否成功
     */
    public boolean addCondition(int year, int month, int day, int hour, int minute, int zone) {
        if (sceneConditions.size() < 3) {
            byte[] timebyte = new byte[8];
            timebyte[1] = (byte) zone;
            String yStr = String.valueOf(year);
            year = Integer.parseInt(yStr.substring(2));
            timebyte[2] = (byte) year;
            timebyte[3] = (byte) month;
            timebyte[4] = (byte) day;
            timebyte[5] = (byte) hour;
            timebyte[6] = (byte) minute;
            SceneCondition sceneCondition = new Timing(timebyte);
            List<SceneCondition> sceneConditions = new ArrayList<>();
            sceneConditions.add(sceneCondition);
            this.sceneConditions.add(sceneConditions);
            return true;
        }
        return false;
    }

    /**
     * 按照年月日方式修改情景的一个条件
     *
     * @param sceneCondition 要修改的条件
     * @param year           年
     * @param month          月
     * @param day            日
     * @param hour           小时
     * @param minute         分
     * @param zone           时区，中国为东八区则为8
     */
    public void modifyCondition(SceneCondition sceneCondition, int year, int month, int day, int hour, int minute, int zone) {
        byte[] timebyte = new byte[8];
        timebyte[1] = (byte) zone;
        String yStr = String.valueOf(year);
        year = Integer.parseInt(yStr.substring(2));
        timebyte[2] = (byte) year;
        timebyte[3] = (byte) month;
        timebyte[4] = (byte) day;
        timebyte[5] = (byte) hour;
        timebyte[6] = (byte) minute;
        sceneCondition.setCondition(obScene.getSerisNum() + "", timebyte);
    }

    /**
     * 删除情景的触发条件
     *
     * @param sceneCondition 要删除的条件
     * @return 删除成功返回true
     */
    public boolean removeCondition(SceneCondition sceneCondition) {
        for (int i = 0; i < sceneConditions.size(); i++) {
            if (sceneConditions.get(i).contains(sceneCondition)) {
                sceneConditions.remove(i);
                return true;
            }
        }
        return false;
    }

    private boolean isRemove;

    /**
     * 删除这场景，此方法会直接发送命令
     */
    public void remove() {
        isRemove = true;
        mTcpSend.editSceneId(ObScene.ENABLE, ObScene.DELETE, obScene.getSerisNum(), obScene.getSceneId(), 0);
    }

    /**
     * 设置场景id之后得到场景序列号设置对应的场景action
     */
    private void setAllAction(boolean isCreat) {
        for (int i = 0; i < obScene.getObNodes().size(); i++) {
            SceneAction sceneAction = obScene.getObNodes().get(i);
            byte[] action = sceneAction.getActions(isCreat ? 0 : obScene.getSerisNum());
            if (action == null) {
                action = new byte[8];
            }
            sceneAction.putAction(obScene.getSerisNum(), action);
        }
    }

    /**
     * 请在onReceive中调用此方法
     *
     * @param message 网络回复参数
     */
    public void onReceive(Message message) {
        switch (message.what) {
            case OBConstant.ReplyType.ON_SETSCENE_SUC:
                ParseUtil.onEditScene(false, obScene,
                        obScenes,
                        message, null, LocalDataPool.newInstance().getObnodesForOneObox(LocalDataPool.newInstance().getObox()));
                byte[] bytes = ParseUtil.getBytes(message);
                switch (bytes[8]) {
                    case ObScene.OBSCENE_ID:
                        if (isRemove) {
                            onRemoveSuc();
                            return;
                        }
                        if (isCreat) {
                            obScene.setRfAddr(mTcpSend.getRfAddr());
                        }
                        setAllAction(isCreat);
                        ParseUtil.batCondition(obScene, conditionIndex, mTcpSend);
                        break;
                    case ObScene.OBSCENE_CONDITION:
                        conditionIndex++;
                        /*传送条件*/
                        if (conditionIndex < 3) {
                            ParseUtil.batCondition(obScene, conditionIndex, mTcpSend);
                        } else {
                            /*传送action，创建模式*/
                            if (isCreat) {
                                checkFinish(obScene.getObNodes(), false);
                                if (isActionFinish && actionIndex == 0) {
                                    finishOnsuc();
                                }
                            } else {
                                /*传送action，修改模式*/
                                checkFinish(modifySceneActions, false);
                                if (isActionFinish && actionIndex == 0) {
                                    isDeleteing = true;
                                    checkFinish(deleteSceneActions, true);
                                    if (isActionFinish && actionIndex == 0) {
                                        finishOnsuc();
                                    }
                                }
                            }
                        }

                        break;
                    case ObScene.OBSCENE_ACTION:
                        if (isCreat) {
                            if (!isActionFinish) {
                                checkFinish(obScene.getObNodes(), false);
                            } else {
                                finishOnsuc();
                            }
                        } else {
                            if (!isDeleteing) {
                                if (!isActionFinish) {
                                    checkFinish(modifySceneActions, false);
                                } else {
                                    isDeleteing = true;
                                    actionIndex = 0;
                                    checkFinish(deleteSceneActions, true);
                                    if (isActionFinish && actionIndex == 0) {
                                        finishOnsuc();
                                    }
                                }
                            } else {
                                if (!isActionFinish) {
                                    checkFinish(deleteSceneActions, true);
                                } else {
                                    finishOnsuc();
                                }
                            }
                        }
                        break;
                }
                break;
            case OBConstant.ReplyType.ON_EXCUTE_SCENE_SUC:
                finishOnsuc();
                break;
            case OBConstant.ReplyType.NOT_REPLY:
                onNotReply();
                break;
            case OBConstant.ReplyType.WRONG_TIME_OUT:
            case OBConstant.ReplyType.ON_SETSCENE_FAL:
            case OBConstant.ReplyType.WRONG_WRONG_PWD:
            case OBConstant.ReplyType.ON_EXCUTE_SCENE_FAL:
                onFialed();
                break;
        }
    }

    /**
     * 执行情景
     */
    public void excute() {
        mTcpSend.editSceneId(2, 2, obScene.getSerisNum(), obScene.getSceneId(), 0);
    }

    /**
     * 删除情景成功的返回，此前已经将目标情景从容器中删除
     */
    protected abstract void onRemoveSuc();

    /**
     * 执行失败回调
     */
    public abstract void onFialed();

    /**
     * 没有回复的回掉
     */
    public abstract void onNotReply();

    /**
     * 行为设置记录
     */
    private int actionIndex;

    private boolean isActionFinish;
    /**
     * 条件设置记录
     */
    private int conditionIndex;
    /**
     * 用于修改模式下判断当前是否处于action的第二阶段删除操作
     */
    private boolean isDeleteing;

    /**
     * @param obNodes
     * @param b       是否删除
     */
    private void checkFinish(List<SceneAction> obNodes, boolean b) {
        ActionReply actionReply = ParseUtil.batAction(obNodes, actionIndex, b,
                mTcpSend, false);
        actionIndex = actionReply.getIndex();
        isActionFinish = actionReply.isFinish();
    }

    /**
     * 除remove方法外的其他参数设置并不会直接触发网络交互，
     * 调用此方法，才会开始网络交互，可以在各项参数设置后，再调用此方法，将自动下发之前设置的属性
     */
    public void onSure() {
        isRemove = false;
        conditionIndex = 0;
        actionIndex = 0;
        isActionFinish = false;
        isDeleteing = false;
        if (!isCreat) {
            compareActionData();
        }
        mTcpSend.editSceneId(ObScene.ENABLE, isCreat ? ObScene.CRETE : ObScene.MODIFY, obScene.getSerisNum(), obScene.getSceneId(), obScene.getSceneGroup());
    }

    /**
     * 网络交互成功结束的回调
     */
    public abstract void finishOnsuc();

    /**
     * 本地模式修改场景，用于保存修改的action列表
     */
    private List<SceneAction> modifySceneActions = new ArrayList<>();

    /**
     * 本地模式修改场景，用于保存删除的action
     */
    private List<SceneAction> deleteSceneActions = new ArrayList<>();

    /**
     * 本地模式的行为进入时候的备份
     */
    private List<SceneAction> originalSceneAction;

    /**
     * 本地模式对比原始数据 ，判断是否有修改状态，
     * 并且检查生成从原始数据中删除的action列表用于删除操作
     */
    private void compareActionData() {
        /*检查变更数据(变更状态数据，新增数据)并保存于modifySceneActions*/
        if (modifySceneActions.size() != 0) {
            modifySceneActions.clear();
        }
        List<SceneAction> sceneActions = obScene.getObNodes();
        for (int i = 0; i < sceneActions.size(); i++) {
            SceneAction scenAction = sceneActions.get(i);
            boolean isFind = false;
            for (int j = 0; j < originalSceneAction.size(); j++) {
                SceneAction cacheScenAction = originalSceneAction.get(j);
                if (Arrays.equals(scenAction.getAddrs(), cacheScenAction.getAddrs())) {
                    isFind = true;
                    if (!Arrays.equals(scenAction.getActions(obScene.getSerisNum()), cacheScenAction.getActions(obScene.getSerisNum()))) {
                        modifySceneActions.add(scenAction);
                    }
                    break;
                }
            }
            if (!isFind) {
                modifySceneActions.add(scenAction);
            }
        }
        /*检查被删除SceneAction 并保存于deleteSceneActions*/
        if (deleteSceneActions.size() != 0) {
            deleteSceneActions.clear();
        }
        for (int i = 0; i < originalSceneAction.size(); i++) {
            SceneAction scenAction = originalSceneAction.get(i);
            boolean isFind = false;
            for (int j = 0; j < sceneActions.size(); j++) {
                SceneAction cacheScenAction = sceneActions.get(j);
                if (Arrays.equals(scenAction.getAddrs(), cacheScenAction.getAddrs())) {
                    isFind = true;
                    break;
                }
            }
            if (!isFind) {
                deleteSceneActions.add(scenAction);
            }
        }
    }

    public void setSceneGroup(int sceneGroup) {
        obScene.setSceneGroup(sceneGroup);
    }

    /**
     * 此接口可帮助设置星期循环例如boolean[SUNDAY] = true表示在星期天会执行
     */
    public interface CircleHelper {
        int SATURDAY = 0;
        int FRIDAY = 1;
        int THURSDAY = 2;
        int WEDNESDAY = 3;
        int TUESDAY = 4;
        int MONDAY = 5;
        int SUNDAY = 6;
    }

    /*从星期循环计算byte*/
    private int mathWeekByte(boolean[] ints) {
        int sum = 0;
        for (int i = 0; i < ints.length; i++) {
            if (ints[i]) {
                sum += (1 << (ints.length - i - 1));
            }
        }
        return sum;
    }

    /**
     * 添加action，即为场景添加行为动作
     *
     * @param action      当情景被触发时会被激发的状态，可与设置节点状态对应，即按照设置status的规则生成此行为，7字节数组
     * @param sceneAction 要添加到情景行为，为null则不会被添加,且必须为当前情景所在的obox内的节点，否则不会进行参数设置并回调{@link #notTogther(SceneAction)}
     */
    public void addAction(byte[] action, SceneAction sceneAction) {
        if (sceneAction != null) {
            if (Arrays.equals(Arrays.copyOf(sceneAction.getAddrs(), 5), Transformation.hexString2Bytes(obScene.getRfAddr()))) {
                sceneAction.putAction(obScene.getSerisNum(), action);
                sceneActions.add(sceneAction);
            } else {
                notTogther(sceneAction);
            }
        }
    }

    /**
     * {@link #addAction(byte[], SceneAction)}试图添加不属于同一个obox的行为回调
     */
    protected abstract void notTogther(SceneAction sceneAction);

    /**
     * 修改情景触发时候的行为动作
     *
     * @param sa     要修改的行为节点
     * @param action 当情景被触发时会被激发的状态
     */
    public void modifyAction(SceneAction sa, byte[] action) {
        sa.putAction(obScene.getSerisNum(), action);
    }

    /**
     * 删除情景的行为使其在情景触发的时候不会再被联动
     *
     * @param sceneAction 要删除的行为节点
     * @return 成功返回true
     */
    public boolean removeAction(SceneAction sceneAction) {
        if (sceneActions.contains(sceneAction)) {
            sceneActions.remove(sceneAction);
            return true;
        }
        return false;
    }

    /**
     * 只获取情景显示信息的时候可使用此构造函数
     *
     * @param context Context
     */
    public SceneHelper(Context context) {
        this.context = context;
    }

    private Context context;

    /**
     * 获取情景条件显示信息
     *
     * @param sceneCondition 要解析的条件
     * @return 情景条件显示信息
     */
    public String getConditionShowString(SceneCondition sceneCondition) {
        ParseCondition pc = new ParseCondition();
        return pc.getLocalShowMsg(context, sceneCondition, "" + obScene.getSerisNum());
    }
}
