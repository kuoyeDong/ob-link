package com.onbright.oblink.cloud.helper;

import android.content.Context;

import com.onbright.oblink.ParseCondition;
import com.onbright.oblink.StringUtil;
import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.bean.Groups;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.Obox;
import com.onbright.oblink.local.net.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器情景管理类，提供创建仅基于服务器的情景、创建同时基于服务器与obox的情景。
 * 请注意，基于OBOX的情景，作为行为的节点必须属于obox、并且建议对基于同一个obox的情景操作间隔五秒，且基于同一个obox的情景数不要超过20个，否则可能发生不可预知的错误
 * 创建到obox的情景不可跨越OBOX进行情景设置{@link com.onbright.oblink.cloud.bean.CloudScene#LOCAL}
 * 没创建到obox的情景可跨越obox进行设置{@link com.onbright.oblink.cloud.bean.CloudScene#SERVER}
 * <p>
 * 如果要创建情景，请使用构造函数{@link #SSceneHelper(Context)}，再调用{@link #creatCloudScene(String, boolean, Obox)}设定情景名称以及是否基于obox。
 * 如果要设定条件，调用{@link #addCondition(int, int, int, int, int, int)}设定年、月、日、时、分条件，或调用{@link #addConditionWithWeek(boolean, boolean[], int, int, int)}设定星期几、时、分条件，
 * 调用{@link #addAction(byte[], DeviceConfig)}设定情景触发后的行为，设定好条件、行为参数后，调用{@link #sure()}触发请求，
 * 成功则回调{@link #onSetSceneSuc(CloudScene)},失败则回调{@link #onOperationFailed(String, String)}。
 * <p>
 * 如果要对已有情景做修改，调用{@link #SSceneHelper(Context, CloudScene)},再调用{@link #setCloudSceneId(String)}修改情景名称，
 * 调用{@link #addCondition(int, int, int, int, int, int)}和{@link #addConditionWithWeek(boolean, boolean[], int, int, int)}增加条件，
 * 注意一个情景最多支持三个条件，如果不确定是否可以继续添加条件，请调用{@link #canAddCondition()}检查，
 * 调用{@link #removeCondition(Condition)}删除条件，调用{@link #modifyCondition(Condition, int, int, int, int, int, int)}
 * 或{@link #modifyConditionWithWeek(Condition, boolean, boolean[], int, int, int)}修改条件，
 * 调用{@link #addAction(byte[], DeviceConfig)}增加行为，调用{@link #modifyAction(Action, byte[])}修改行为，调用{@link #removeAction(Action)}删除行为，
 * 以上参数设置操作完成后，调用{@link #sure()}触发请求，成功则回调{@link #onSetSceneSuc(CloudScene)},失败则回调{@link #onOperationFailed(String, String)}。
 * <p>
 * 如果要删除情景，调用{@link #SSceneHelper(Context, CloudScene)},再调用{@link #removeScene()}，
 * 成功则回调{@link #onExecutOrRemoveSuc(CloudScene)}。
 * <p>
 * 如果要执行已有情景，使情景预设行为立即改变到预设状态，请调用{@link #SSceneHelper(Context, CloudScene)},再调用{@link #excuteScene()}，
 * 执行成功则回调{@link #onExecutOrRemoveSuc(CloudScene)},失败则回调{@link #onOperationFailed(String, String)}。
 * <p>
 * 如果要在页面显示情景的语意化信息，比如某年某月某日某时某分，请调用{@link #getConditionShowString(Condition)},此方法本地解析，不涉及请求，直接返回显示信息。
 */

public abstract class SSceneHelper implements HttpRespond {
    private Context context;
    private CloudScene cloudScene;
    private boolean isCreat;

    /**
     * 创建情景的时候使用此构造函数
     *
     * @param context context
     */
    public SSceneHelper(Context context) {
        this.context = context;
        isCreat = true;
    }

    /**
     * 修改情景的时候使用此构造函数
     *
     * @param context    context
     * @param cloudScene 要修改的目标情景
     */
    public SSceneHelper(Context context, CloudScene cloudScene) {
        this.context = context;
        this.cloudScene = cloudScene;
        isCreat = false;
    }

    /**
     * 创建情景
     *
     * @param id      情景名称
     * @param isLocal 是否创建基于obox的情景
     * @param obox    如果要创建到obox则传入目标obox，否则为null
     */
    public void creatCloudScene(String id, boolean isLocal, Obox obox) {
        if (!isCreat) {
            return;
        }
        if (id == null) {
            return;
        }
        if (isLocal) {
            if (!StringUtil.isLegit(id, 0, 16)) {
                onWrongId();
                return;
            }
            cloudScene = new CloudScene();
            cloudScene.setScene_type(CloudScene.LOCAL);
            cloudScene.setObox_serial_id(obox.getObox_serial_id());
        } else {
            cloudScene = new CloudScene();
            cloudScene.setScene_type(CloudScene.SERVER);
        }
        cloudScene.setScene_name(id);
        cloudScene.setScene_status("1");
    }

    /**
     * 执行场景，使情景直接触发，创建模式不可用，此函数将直接触发交互
     */
    public void excuteScene() {
        if (isCreat) {
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.EXECUTE_SC,
                GetParameter.onExecuteLocationScene(cloudScene.getScene_number(), CloudConstant.CmdValue.ACTION_SCENE));
    }

    /**
     * 删除场景,创建模式不可用，此函数将直接触发交互
     */
    public void removeScene() {
        if (isCreat) {
            return;
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.EXECUTE_SC,
                GetParameter.onExecuteLocationScene(cloudScene.getScene_number(), CloudConstant.CmdValue.DELETE_SCENE));
    }

    /**
     * 设置情景名称,如果是创建情景，请确保已经执行过{@link #creatCloudScene(String, boolean, Obox)},否则此方法将直接return
     *
     * @param id 情景名称，如果是下发到obox的情景则长度不能超过16字节并且不能包含特殊字符
     *           如果不符合，则会回调{@link  #onWrongId()}
     */
    public void setCloudSceneId(String id) {
        if (cloudScene == null) {
            return;
        }
        if (cloudScene.getScene_type().equals(CloudScene.LOCAL) && (!StringUtil.isLegit(id, 0, 16))) {
            onWrongId();
            return;
        }
        cloudScene.setScene_name(id);
    }

    /**
     * 是否可以为情景添加条件,设定最大支持的条件数量为3
     *
     * @return 可以添加条件返回true
     */
    public boolean canAddCondition() {
        if (cloudScene.getConditions().size() < 3) {
            return true;
        } else {
            for (int i = 0; i < cloudScene.getConditions().size(); i++) {
                List<Condition> conditions = cloudScene.getConditions().get(i);
                if (conditions.size() == 0) {
                    return true;
                }
            }
        }
        return false;
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
     * 按照星期循环为情景添加条件
     *
     * @param everday    是否每天执行，若选择每天执行则weekcirlce可传null
     * @param weekcircle 星期几的boolen数组，长度为7，数组元素为true则在此天会执行，设置方式参照CircleHelper
     * @param hour       小时，24h
     * @param min        分
     * @param zone       时区，中国为东八区则传8
     * @return 是否设置成功
     */
    public boolean addConditionWithWeek(boolean everday, boolean[] weekcircle, int hour, int min, int zone) {
        if (cloudScene.getConditions().size() < 3) {
            Condition condition = makeweekTime(everday, weekcircle, (byte) hour, (byte) min, (byte) zone);
            List<Condition> conditions = new ArrayList<>();
            conditions.add(condition);
            cloudScene.getConditions().add(conditions);
            return true;
        } else {
            for (int i = 0; i < cloudScene.getConditions().size(); i++) {
                List<Condition> conditions = cloudScene.getConditions().get(i);
                if (conditions.size() == 0) {
                    Condition condition = makeweekTime(everday, weekcircle, (byte) hour, (byte) min, (byte) zone);
                    conditions.add(condition);
                    return true;
                }
            }
        }
        return false;
    }

    private Condition makeweekTime(boolean everday, boolean[] weekcircle, byte hour, byte min, byte zone) {
        byte[] conditonbyte = new byte[8];
        if (everday) {
            conditonbyte[0] = (byte) 0x80;
        } else {
            int sum = mathWeekByte(weekcircle);
            conditonbyte[0] = (byte) sum;
        }
        conditonbyte[1] = zone;
        conditonbyte[5] = hour;
        conditonbyte[6] = min;
        Condition cdt = new Condition();
        cdt.setCondition(Transformation.byteArryToHexString(conditonbyte));
        cdt.setCondition_type("00");
        return cdt;
    }

    /**
     * 根据星期循环方式修改情景的一个条件
     *
     * @param condition  要修改的条件
     * @param everday    是否每天，传true则weekcircle可为null
     * @param weekcircle 星期几的boolen数组，长度为7，数组元素为true则在此天会执行，设置方式参照CircleHelper
     * @param hour       小时，24h
     * @param min        分
     * @param zone       时区，中国为东八区则传8
     */
    public void modifyConditionWithWeek(Condition condition,
                                        boolean everday, boolean[] weekcircle, int hour, int min, int zone) {
        Condition condition1 = makeweekTime(everday, weekcircle, (byte) hour, (byte) min, (byte) zone);
        condition.setCondition(condition1.getCondition());
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
        if (cloudScene.getConditions().size() < 3) {
            Condition condition = makeTime(year, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) zone);
            List<Condition> conditions = new ArrayList<>();
            conditions.add(condition);
            cloudScene.getConditions().add(conditions);
            return true;
        } else {
            for (int i = 0; i < cloudScene.getConditions().size(); i++) {
                List<Condition> conditions = cloudScene.getConditions().get(i);
                if (conditions.size() == 0) {
                    Condition condition = makeTime(year, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) zone);
                    conditions.add(condition);
                    return true;
                }
            }
        }
        return false;
    }

    private Condition makeTime(int year, byte month, byte day, byte hour, byte minute, byte zone) {
        byte[] timebyte = new byte[8];
        timebyte[1] = zone;
        String yStr = String.valueOf(year);
        year = Integer.parseInt(yStr.substring(2));
        timebyte[2] = (byte) year;
        timebyte[3] = month;
        timebyte[4] = day;
        timebyte[5] = hour;
        timebyte[6] = minute;
        Condition condition = new Condition();
        condition.setCondition(Transformation.byteArryToHexString(timebyte));
        condition.setCondition_type("00");
        return condition;
    }

    /**
     * 按照年月日方式修改情景的一个条件
     *
     * @param condition 要修改的条件
     * @param year      年
     * @param month     月
     * @param day       日
     * @param hour      小时
     * @param minute    分
     * @param zone      时区，中国为东八区则为8
     */
    public void modifyCondition(Condition condition, int year, int month, int day, int hour, int minute, int zone) {
        Condition condition1 = makeTime(year, (byte) month, (byte) day, (byte) hour, (byte) minute, (byte) zone);
        condition.setCondition(condition1.getCondition());
    }

    /**
     * 删除情景的触发条件
     *
     * @param condition 要删除的条件
     * @return 删除成功返回true
     */
    public boolean removeCondition(Condition condition) {
        for (int i = 0; i < cloudScene.getConditions().size(); i++) {
            if (cloudScene.getConditions().get(i).contains(condition)) {
                cloudScene.getConditions().remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加action，即为场景添加行为动作
     *
     * @param action       当情景被触发时会被激发的状态，可与设置节点行为的status对应，即按照设置status的规则生成此action，7字节数组
     * @param deviceConfig 要添加到情景action,如果情景是下发到本地obox的，则设备必须和情景同obox，即{@link DeviceConfig#getObox_serial_id()}和
     *                     {@link CloudScene#getObox_serial_id()}相等
     * @return 添加成功的行为
     */
    public Action addAction(byte[] action, DeviceConfig deviceConfig) {
        Action actionNode = new Action();
        actionNode.setAction(Transformation.byteArryToHexString(action));
        if (deviceConfig instanceof Groups) {
            Groups groups = (Groups) deviceConfig;
            actionNode.setNode_type("01");
            actionNode.setDevice_type(groups.getGroup_type());
            actionNode.setDevice_child_type(groups.getGroup_child_type());
            actionNode.setActionName(groups.getGroup_name());
            actionNode.setAction(groups.getGroup_state());
            actionNode.setGroup_id(groups.getGroup_id());
            cloudScene.getActions().add(actionNode);
        } else {
            actionNode.setNode_type("00");
            actionNode.setSerialId(deviceConfig.getSerialId());
            actionNode.setAddr(deviceConfig.getAddr());
            actionNode.setObox_serial_id(deviceConfig.getObox_serial_id());
            actionNode.setDevice_type(deviceConfig.getDevice_type());
            actionNode.setDevice_child_type(deviceConfig.getDevice_child_type());
            actionNode.setActionName(deviceConfig.getName());
            actionNode.setAction(deviceConfig.getState());
            cloudScene.getActions().add(actionNode);
        }
        return actionNode;
    }

    /**
     * 修改情景触发时候的行为动作
     *
     * @param actionNode 要修改的行为节点
     * @param action     当情景被触发时会被激发的状态
     */
    public boolean modifyAction(Action actionNode, byte[] action) {
        if (!cloudScene.getActions().contains(actionNode)) {
            return false;
        }
        actionNode.setAction(Transformation.byteArryToHexString(action));
        return true;
    }

    /**
     * 删除情景的行为使其在情景触发的时候不会再被联动
     *
     * @param action 要删除的行为节点
     * @return 成功返回true
     */
    public boolean removeAction(Action action) {
        return cloudScene.getActions().remove(action);
    }

    /**
     * 获取情景条件显示信息
     *
     * @param condition 要解析的条件
     * @return 情景条件显示信息
     */
    public String getConditionShowString(Condition condition) {
        ParseCondition pc = new ParseCondition();
        return pc.getCdtShowMsg(context, condition);
    }

    /**
     * 情景相关参数设置完毕之后，进行的参数设置，除{@link #excuteScene()) {@link #removeScene()}}
     */
    public void sure() {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_SC_INFO,
                GetParameter.onSetScInfo(cloudScene));
    }

    /**
     * 传入情景名称不合规回调
     */
    public abstract void onWrongId();

    @Override
    public void onRequest(String action) {

    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.SETTING_SC_INFO:
                onSetSceneSuc(cloudScene);
                break;
            case CloudConstant.CmdValue.EXECUTE_SC:
                onExecutOrRemoveSuc(cloudScene);
                break;
        }
    }

    /**
     * 执行情景和删除情景成功的回掉
     *
     * @param cloudScene 执行或删除的情景
     */
    public abstract void onExecutOrRemoveSuc(CloudScene cloudScene);

    /**
     * 创建和修改情景成功的回调用
     *
     * @param cloudScene 创建产生的情景或者修改的目标情景
     */
    public abstract void onSetSceneSuc(CloudScene cloudScene);

    @Override
    public void onFaild(String action, Exception e) {

    }


    @Override
    public void onFaild(String action, int state) {

    }

    @Override
    public void onRespond(String action) {

    }

    @Override
    public void operationFailed(String action, String json) {
        onOperationFailed(action, json);
    }

    /**
     * 操作失败的回调
     *
     * @param action 失败操作时的动作
     * @param json   失败原因代码
     */
    public abstract void onOperationFailed(String action, String json);

}
