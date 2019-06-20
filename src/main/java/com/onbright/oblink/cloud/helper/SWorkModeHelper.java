package com.onbright.oblink.cloud.helper;

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
import com.onbright.oblink.local.bean.Handset;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.bean.ObSensor;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.bean.Timing;
import com.onbright.oblink.local.helper.InitConfigHelper;
import com.onbright.oblink.local.net.TcpSend;
import com.onbright.oblink.local.net.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务器设置obox工作模式的类，提供把obox数据添加到服务器、把服务器内的目标obox数据删除并设为独立工作模式,
 * 请注意，obox独立工作模式的时候指示灯闪烁，连接到服务器模式指示灯长亮
 * <p>
 * <p>
 * 如果要把处于独立工作模式（指示灯闪烁）的obox数据添加到服务器，请先连接目标obox的wifi热点，
 * 再使用{@link InitConfigHelper#startGetConfig()}尝试与obox建立通讯并获取配置，
 * 成功则回调{@link InitConfigHelper#finishInit(Map, List, List, List, Map, Map, Map)}（此回调中的所有数据请保存，后面的步骤会用到），
 * 在回调中获取到{@link com.onbright.oblink.local.net.TcpSend}对象后，再构造{@link com.onbright.oblink.local.helper.WorkModeHelper#WorkModeHelper(TcpSend)},
 * 然后调用{@link com.onbright.oblink.local.helper.WorkModeHelper#toStationMode(boolean, String, String, byte[], String)}obox即尝试连接目标路由器并在第一参数为true时尝试连接服务器，
 * 此方法不会有回调，请在十五秒后自行观察obox的指示灯，如果指示灯依然闪烁请重试上面的操作，如指示灯长亮并且你只想把obox加入到路由网段而不想添加到服务器，那到这里就完成了。
 * <p>
 * 如果想继续把obox添加到服务器，组成以服务器为中心的工作单元，请继续下面的操作：
 * android移动设备使用可以连接到目标服务器的网络（obox的wifi热点是肯定不行的），
 * 构造{@link SInitConfigHelper#SInitConfigHelper(String, String)}对象，
 * 调用{@link SInitConfigHelper#startInit()}以确定要把之前的obox添加到哪个账户，成功则回调{@link SInitConfigHelper#onFinish(List, List, List, List)},
 * 接着调用{@link SWorkModeHelper#addObox(Obox, Map, Map, Map)}尝试将obox数据添加到服务器指定账户，请注意之前保存的map中，key都是obox的序列号{@link Obox#getObox_serial_id()},
 * 成功则回调{@link #onAddOboxSuc()},否则回调{@link #onAddOboxFailed(String, String)}
 * <p>
 * 如果想把obox从服务器删除，注意，删除操作同时会把obox设置为独立工作模式，即不在路由器网段内，请调用
 * {@link #removeObox(Obox)},成功则返回{@link #onRemoveOboxSuc()},否则回调{@link #onRemoveOboxFailed(String, String)},
 * 请注意如果在此之前整个应用中没有成功运行过{@link SInitConfigHelper#startInit()}，请运行{@link SInitConfigHelper#startInit()}，否则请忽略此提示，
 */

public abstract class SWorkModeHelper implements HttpRespond {

    /**
     * 从当前用户下删除obox，并且obox工作模式会是独立ap模式
     *
     * @param obox 要删除的obox
     */
    public void removeObox(Obox obox) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_OBOX,
                GetParameter.onDeleteObox(true, obox.getObox_serial_id()));
    }

    /**
     * 把obox绑定到当前账户,请注意调用此函数前需确认目标obox已经连接到目标服务器，
     * 此函数操作成功后，目标obox的服务器节点配置将被初始化，
     * 可通过{@link Obox#getDevice_config(),Obox#getGroup_config(),Obox#getScene_config()}获取使用
     *
     * @param obox       要绑定的obox
     * @param obNodeMap  同网段内的节点数据容器
     * @param obGroupMap 同网段内的组数据容器
     * @param obSceneMap 同网段内的情景数据容器
     * @see com.onbright.oblink.local.helper.WorkModeHelper#toStationMode(boolean, String, String, byte[], String)
     * 并且在所在网段内获取过obox节点、组和情景配置信息
     * @see InitConfigHelper#startGetConfig(),
     * 并得到回调，回调参数正是您需要传入此函数的的参数
     * @see InitConfigHelper#finishInit(Map, List, List, List, Map, Map, Map) ()
     */
    public void addObox(Obox obox, Map<String, List<ObNode>> obNodeMap,
                        Map<String, List<ObGroup>> obGroupMap,
                        Map<String, List<ObScene>> obSceneMap) {
        initDeviceConfig(obNodeMap, obox);
        initGroup(obGroupMap, obox);
        initScene(obSceneMap, obox);
        this.obox = obox;
        retryIndex = 0;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_OBOX,
                GetParameter.onAddObox(obox));
    }


    private void initScene(Map<String, List<ObScene>> obSceneMap, Obox obox) {
        if (obSceneMap == null) {
            return;
        }
        List<ObScene> obScenes = obSceneMap.get(obox.getObox_serial_id());
        if (obScenes == null) {
            return;
        }
        List<CloudScene> clss = new ArrayList<>();
        for (ObScene obScen : obScenes) {
            CloudScene cls = new CloudScene();
            initConditions(obScen, cls);
            initActions(obScen, cls);
            cls.setScene_name(StringUtil.getUtf8(obScen.getSceneId()));
            cls.setObox_scene_number(obScen.getSerisNum() + "");
            cls.setScene_status(obScen.isEnable() ? "01" : "00");
            cls.setObox_serial_id(obScen.getRfAddr());
            cls.setScene_group(Transformation.byte2HexString((byte) obScen.getSceneGroup()));
            cls.setScene_type("01");
            clss.add(cls);
        }
        obox.setScene_config(clss);
    }


    private void initConditions(ObScene obScen, CloudScene cls) {
        List<List<Condition>> cldCdtss = new ArrayList<>();
        List<List<SceneCondition>> sCss = obScen.getSceneCondition();
        for (List<SceneCondition> sCs : sCss) {
            List<Condition> cldCdts = new ArrayList<>();
            for (SceneCondition sc : sCs) {
                Condition cdt = new Condition();
                if (sc instanceof Timing) {
                    cdt.setCondition_type("00");
                    cdt.setCondition(Transformation.byteArryToHexString(sc.getCondition(null)));
                } else if (sc instanceof ObSensor) {
                    ObSensor obSensor = (ObSensor) sc;
                    cdt.setCondition_type("01");
                    cdt.setSerialId(obSensor.getSerNumString());
                    cdt.setAddr(Transformation.byteArryToHexString(obSensor.getCplAddr()));
                    cdt.setObox_serial_id(Transformation.byteArryToHexString(obSensor.getRfAddr()));
                    cdt.setConditionID(obSensor.getNodeId());
                    cdt.setDevice_type(Transformation.byte2HexString((byte) obSensor.getParentType()));
                    cdt.setDevice_child_type(Transformation.byte2HexString((byte) obSensor.getType()));
                    cdt.setCondition(Transformation.byteArryToHexString(obSensor.getCondition("" + obScen.getSerisNum())));
                } else if (sc instanceof Handset) {
                    Handset hs = (Handset) sc;
                    cdt.setCondition_type("02");
                    cdt.setOboxs(hs.getBindObox());
                }
                cldCdts.add(cdt);
            }
            cldCdtss.add(cldCdts);
        }
        cls.setConditions(cldCdtss);
    }


    private void initActions(ObScene obScen, CloudScene cls) {
        List<Action> scActions = new ArrayList<>();
        List<ObNode> scNodes = obScen.getSingleAction();
        for (ObNode obNode : scNodes) {
            Action act = new Action();
            act.setNode_type("00");
            act.setSerialId(obNode.getSerNumString());
            act.setActionName(obNode.getNodeId());
            act.setAddr(Transformation.byte2HexString(obNode.getAddr()));
            act.setObox_serial_id(Transformation.byteArryToHexString(obNode.getRfAddr()));
            act.setDevice_type(Transformation.byte2HexString((byte) obNode.getParentType()));
            act.setDevice_child_type(Transformation.byte2HexString((byte) obNode.getType()));
            act.setAction(Transformation.byteArryToHexString(obNode.getActions(obScen.getSerisNum())));
            scActions.add(act);
        }
        List<ObGroup> scgrops = obScen.getGroupAction();
        for (ObGroup obGroup : scgrops) {
            Action act = new Action();
            act.setNode_type("01");
            act.setDevice_type(Transformation.byte2HexString((byte) obGroup.getGroupPType()));
            act.setDevice_child_type(Transformation.byte2HexString((byte) obGroup.getGroupType()));
            act.setAction(Transformation.byteArryToHexString(obGroup.getActions(obScen.getSerisNum())));
            act.setActionName(obGroup.getNodeId());
            act.setGroup_id(obGroup.getNodeId());
            act.setGroupAddr(Transformation.byte2HexString(obGroup.getAddr()));
            scActions.add(act);
        }
        cls.setActions(scActions);
    }

    private void initGroup(Map<String, List<ObGroup>> obGroupMap, Obox obox) {
        if (obGroupMap == null) {
            return;
        }
        List<ObGroup> obGroups = obGroupMap.get(obox.getObox_serial_id());
        if (obGroups == null) {
            return;
        }
        List<Groups> groupses = new ArrayList<>();
        for (ObGroup obGroup : obGroups) {
            Groups groups = new Groups();
            List<DeviceConfig> dvs = new ArrayList<>();
            List<ObNode> obNodes = obGroup.getObNodes();
            for (ObNode obNode : obNodes) {
                DeviceConfig deviceConfig = new DeviceConfig();
                deviceConfig.setName(obNode.getNodeId());
                deviceConfig.setSerialId(obNode.getSerNumString());
                deviceConfig.setObox_serial_id(Transformation.byteArryToHexString(obNode.getRfAddr()));
                deviceConfig.setAddr(Transformation.byte2HexString(obNode.getAddr()));
                deviceConfig.setState(Transformation.byteArryToHexString(obNode.getState()));
                deviceConfig.setDevice_type(Transformation.byte2HexString((byte) obNode.getParentType()));
                deviceConfig.setDevice_child_type(Transformation.byte2HexString((byte) obNode.getType()));
                deviceConfig.setVersion(Transformation.byteArryToHexString(obNode.getVersion()));
                dvs.add(deviceConfig);
            }
            groups.setGroup_member(dvs);
            groups.setGroup_name(obGroup.getNodeId());
            groups.setGroup_type(Transformation.byte2HexString((byte) obGroup.getGroupPType()));
            groups.setGroup_child_type(Transformation.byte2HexString((byte) obGroup.getGroupType()));
            groups.setGroup_style("00");
            groups.setGroupAddr(Transformation.byte2HexString(obGroup.getAddr()));
            groups.setGroup_state(Transformation.byteArryToHexString(obGroup.getGroupState()));
            groups.setObox_serial_id(Transformation.byteArryToHexString(obGroup.getRfAddr()));
            groupses.add(groups);
        }
        obox.setGroup_config(groupses);
    }

    private void initDeviceConfig(Map<String, List<ObNode>> obNodeMap, Obox obox) {
        if (obNodeMap == null) {
            return;
        }
        List<ObNode> obNodes = obNodeMap.get(obox.getObox_serial_id());
        if (obNodes == null) {
            return;
        }
        List<DeviceConfig> deviceConfigList = new ArrayList<>();
        for (int i = 0; i < obNodes.size(); i++) {
            ObNode obNode = obNodes.get(i);
            if (obox.getObox_serial_id().equals(Transformation.byteArryToHexString(obNode.getRfAddr()))) {
                DeviceConfig deviceConfig = new DeviceConfig();
                deviceConfig.setName(obNode.getNodeId());
                deviceConfig.setSerialId(obNode.getSerNumString());
                deviceConfig.setObox_serial_id(Transformation.byteArryToHexString(obNode.getRfAddr()));
                deviceConfig.setAddr(Transformation.byte2HexString(obNode.getAddr()));
                deviceConfig.setState(Transformation.byteArryToHexString(obNode.getState()));
                deviceConfig.setDevice_type(Transformation.byte2HexString((byte) obNode.getParentType()));
                deviceConfig.setDevice_child_type(Transformation.byte2HexString((byte) obNode.getType()));
                deviceConfig.setVersion(Transformation.byteArryToHexString(obNode.getVersion()));
                deviceConfigList.add(deviceConfig);
            }
        }
        obox.setDevice_config(deviceConfigList);
    }

    @Override
    public void onRequest(String action) {

    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.DELETE_OBOX:
                onRemoveOboxSuc();
                break;
            case CloudConstant.CmdValue.ADD_OBOX:
                onAddOboxSuc();
                break;
        }
    }

    /**
     * 添加到obox成功的回调
     */
    public abstract void onAddOboxSuc();

    /**
     * 删除obox成功的回调
     */
    public abstract void onRemoveOboxSuc();


    @Override
    public void onFaild(String action, Exception e) {
        onFaild(action);
    }

    /**
     * 与服务器连接网络出错导致的失败
     *
     * @param action 出错时执行的动作
     */
    public abstract void onFaild(String action);

    @Override
    public void onFaild(String action, int state) {
        onFaild(action);
    }

    @Override
    public void onRespond(String action) {

    }

    /**
     * 删除obox失败的回调
     *
     * @param action 失败操作时的动作
     * @param json   失败原因代码
     */
    public abstract void onRemoveOboxFailed(String action, String json);

    /**
     * 添加obox失败的回调
     *
     * @param action 失败操作时的动作
     * @param json   失败原因代码
     */
    public abstract void onAddOboxFailed(String action, String json);

    @Override
    public void operationFailed(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.DELETE_OBOX:
                onRemoveOboxFailed(action, json);
                break;
            case CloudConstant.CmdValue.ADD_OBOX:
                if (retryIndex < 3) {
                    retryIndex++;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                                retry(obox);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    onAddOboxFailed(action, json);
                }
                break;
        }
    }

    /**
     * 用以保存重试的引用
     */
    private Obox obox;
    /**
     * 重试次数
     */
    private int retryIndex;

    /**
     * 重试
     *
     * @param obox
     */
    private void retry(Obox obox) {
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_OBOX,
                GetParameter.onAddObox(obox));
    }
}
