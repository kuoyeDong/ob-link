package com.onbright.oblink.smartconfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import com.google.gson.Gson;
import com.onbright.oblink.EventMsg;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.Obox;
import com.onbright.oblink.Share;
import com.onbright.oblink.StringUtil;
import com.onbright.oblink.cloud.CloudDataPool;
import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.AliDevState;
import com.onbright.oblink.cloud.bean.AliSpec;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.bean.Group;
import com.onbright.oblink.cloud.bean.UpLoadWifiIr;
import com.onbright.oblink.cloud.bean.WifiDevice;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;
import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.local.bean.Handset;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.bean.ObSensor;
import com.onbright.oblink.local.bean.Obsocket;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.bean.SmartLock;
import com.onbright.oblink.local.bean.Timing;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.ParseUtil;
import com.onbright.oblink.local.net.Respond;
import com.onbright.oblink.local.net.TcpSend;
import com.onbright.oblink.local.net.Transformation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * author:Adolf_Dong  time: 2019/2/20 18:27
 * use:激活设备基类
 */
public abstract class ConnectDeviceHandler implements Respond, HttpRespond {
    private static final String TAG = "ConnectDeviceHandler";
    /**
     * 上下文
     */
    Context context;
    /**
     * 目标wifi的密码
     */
    String routePwd;

    /**
     * 设备密钥
     * 7
     */
    String deviceSecret;

    /**
     * 设备名称
     * 8
     */
    String deviceName;

    /**
     * 连接域名
     * 9
     */
    String kitCenter;

    /**
     * 产品key
     * 10
     */
    String productKey;
    /**
     * 设备序列号
     */
    String serNum;

    /**
     * 第二次连接后获取obox内参数配置
     */
    TcpSend tcpSend;
    /**
     * 设备类型
     */
    int type;

    /**
     * 标准化设备定义json字符串
     */
    private String configStr;
    private List<String> oboxSSIDs = new ArrayList<>();
    private List<ObNode> obNodes;
    private List<ObGroup> obGroups;
    private List<ObScene> obScenes;
    private Obox obox;
    boolean isReqOboxMsg;


    ConnectDeviceHandler(Context context, String routePwd, String deviceSecret, String deviceName, String kitCenter, String productKey) {
        this.context = context;
        this.routePwd = routePwd;
        this.deviceSecret = deviceSecret;
        this.deviceName = deviceName;
        this.kitCenter = kitCenter;
        this.productKey = productKey;
        EventBus.getDefault().register(this);
    }

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.ADD_OBOX:
                // FIXME: 2019/2/27 阿里不支持组功能
//                NetUtil.doCloudEven(this, CloudConstant.CmdValue.QUERY_GROUP);
                break;
            case CloudConstant.CmdValue.QUERY_GROUP:
                Gson gson = new Gson();
                try {
                    CloudDataPool.getGroups().clear();
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("groups");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Group groups = gson.fromJson(jsonArray.getString(i), Group.class);
                        CloudDataPool.addGroups(groups);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CloudConstant.CmdValue.UPLOAD_CONFIG:

                break;
        }
    }

    /**
     * 添加obox成功
     */
    private void onCloudAddOboxSuc() {
        String oboxSer = obox.getObox_serial_id();
        if (oboxSer != null) {
            for (int i = 0; i < CloudDataPool.getOboxs().size(); i++) {
                if (oboxSer.equals(CloudDataPool.getOboxs().get(i).getObox_serial_id())) {
                    CloudDataPool.getOboxs().remove(i);
                    for (int j = 0; j < CloudDataPool.getDevices().size(); j++) {
                        if (CloudDataPool.getDevices().get(j).getObox_serial_id().equals(oboxSer)) {
                            CloudDataPool.getDevices().remove(j);
                            j--;
                        }
                    }
                    for (int j = 0; j < CloudDataPool.getGroups().size(); j++) {
                        /*删除本地组*/
                        if (CloudDataPool.getGroups().get(j).getGroup_style().equals("00")) {
                            if (CloudDataPool.getGroups().get(j).getObox_serial_id() != null) {
                                if (CloudDataPool.getGroups().get(j).getObox_serial_id().equals(oboxSer)) {
                                    CloudDataPool.getGroups().remove(j);
                                    j--;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        CloudDataPool.addObox(obox);
        CloudDataPool.getDevices().addAll(obox.getDevice_config());
        sendBroadUpdateDevice();
    }


    /**
     * 刷新设备列表
     */
    private void sendBroadUpdateDevice() {
        Intent intent = new Intent();
        intent.setAction(OBConstant.StringKey.UPDATE_NODES_CLOUD);
        context.sendBroadcast(intent);
    }

    /**
     * 发送刷新wifi单品设备广播
     */
    private void sendBroadUpdateWifiDevice() {
        Intent intent = new Intent();
        intent.setAction(OBConstant.StringKey.UPDATE_WIFI_DEVICE);
        context.sendBroadcast(intent);
    }

    @Override
    public void onFaild(ErrorCode errorCode, int responseNotOkCode, String operationFailedReason, String action) {
        switch (action) {
            case CloudConstant.CmdValue.ADD_OBOX:
            case CloudConstant.CmdValue.UPLOAD_CONFIG:
                onConnectCloudError();
                break;
        }
    }

    /**
     * 执行添加盒子到服务器
     */
    void addOboxToSerVer() {
        initDeviceConfig();
        initGroup();
        initScene();
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_OBOX,
                GetParameter.onAddObox(obox, deviceName, productKey, true), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 设置节点数据
     */
    @SuppressLint("DefaultLocale")
    private void initDeviceConfig() {
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < obNodes.size(); i++) {
            ObNode obNode = obNodes.get(i);
            if (obox.getObox_serial_id().equals(Transformation.byteArryToHexString(obNode.getRfAddr()))) {
                Device device = new Device();
                device.setName(obNode.getNodeId() == null ? "unKnow" : obNode.getNodeId());
                device.setSerialId(obNode.getSerNumString());
                device.setObox_serial_id(Transformation.byteArryToHexString(obNode.getRfAddr()));
                device.setAddr(Transformation.byte2HexString(obNode.getAddr()));
                device.setState(Transformation.byteArryToHexString(obNode.getState()));
                device.setDevice_type(Transformation.byte2HexString((byte) obNode.getParentType()));
                device.setDevice_child_type(Transformation.byte2HexString((byte) obNode.getType()));
                device.setVersion(Transformation.byteArryToHexString(obNode.getVersion()));
                devices.add(device);
            }
        }
        obox.setDevice_config(devices);
        obox.setObox_status("1");
    }

    /**
     * 设置组数据
     */
    @SuppressLint("DefaultLocale")
    private void initGroup() {
        List<Group> groupses = new ArrayList<>();
        for (ObGroup obGroup : obGroups) {
            Group groups = new Group();
            List<Device> dvs = new ArrayList<>();
            List<ObNode> obNodes = obGroup.getObNodes();
            for (ObNode obNode : obNodes) {
                Device device = new Device();
                device.setName(obNode.getNodeId());
                device.setSerialId(obNode.getSerNumString());
                device.setObox_serial_id(Transformation.byteArryToHexString(obNode.getRfAddr()));
                device.setAddr(Transformation.byte2HexString(obNode.getAddr()));
                device.setState(Transformation.byteArryToHexString(obNode.getState()));
                device.setDevice_type(Transformation.byte2HexString((byte) obNode.getParentType()));
                device.setDevice_child_type(Transformation.byte2HexString((byte) obNode.getType()));
                device.setVersion(Transformation.byteArryToHexString(obNode.getVersion()));
                dvs.add(device);
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

    /**
     * 设置场景
     */
    private void initScene() {
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

    /**
     * 场景条件初始化
     *
     * @param obScen 获取的本地场景
     * @param cls    提交服务器的场景列表
     */
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
                } else if (sc instanceof Obsocket) {
                    Obsocket obSensor = (Obsocket) sc;
                    cdt.setCondition_type("01");
                    cdt.setSerialId(obSensor.getSerNumString());
                    cdt.setAddr(Transformation.byteArryToHexString(obSensor.getCplAddr()));
                    cdt.setObox_serial_id(Transformation.byteArryToHexString(obSensor.getRfAddr()));
                    cdt.setConditionID(obSensor.getNodeId());
                    cdt.setDevice_type(Transformation.byte2HexString((byte) obSensor.getParentType()));
                    cdt.setDevice_child_type(Transformation.byte2HexString((byte) obSensor.getType()));
                    cdt.setCondition(Transformation.byteArryToHexString(obSensor.getCondition("" + obScen.getSerisNum())));
                } else if (sc instanceof SmartLock) {
                    SmartLock obSensor = (SmartLock) sc;
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

    /**
     * 根据本地场景设置服务器场景action
     *
     * @param obScen obox内场景
     * @param cls    适用于服务器的场景
     */
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

    /**
     * 快速模式，不获取配置
     */
    public static final boolean IS_FAST_MODE = true;

    private LocalDataPool localDataPool = LocalDataPool.newInstance();
    @Override
    public void onReceive(Message message) {
        switch (message.what) {
            case OBConstant.ReplyType.GET_OBOX_NAME_BACK:
                ParseUtil.getOboxId(message, obox, Share.getSp(context), tcpSend, oboxSSIDs,
                        context, false);
                tcpSend.setOboxTime(new Time());
                break;
            case OBConstant.ReplyType.ON_SETOBOXTIME_SUC:
            case OBConstant.ReplyType.ON_SETOBOXTIME_FAL:
                if (IS_FAST_MODE) {
                    getParmFinish();
                } else {
                    tcpSend.getDevice(1, false);
                }
                break;
            case OBConstant.ReplyType.GET_OBOX_MSG_BACK:
                isReqOboxMsg = false;
                obox = new Obox();
                ParseUtil.parseObox(message, obox, tcpSend, Share.getSp(context));
                localDataPool.getTcpSends().put(obox.getObox_serial_id(), tcpSend);
                tcpSend.setPaseUpLocad(obox.getObox_serial_id());
                localDataPool.setObox(obox);
                obNodes = new ArrayList<>();
                obGroups = new ArrayList<>();
                obScenes = new ArrayList<>();
                tcpSend.reqOboxSSID();
                break;
            case OBConstant.ReplyType.GET_SINGLENODE_BACK:
                if (ParseUtil.parseDevice(message, obNodes, tcpSend)) {
                    if (obNodes.size() == 0) {
                        getParmFinish();
                    } else {
                        tcpSend.getDevice(1, true);
                    }
                }
                Log.d(TAG, "obNodes.size = " + obNodes.size());
                break;
            case OBConstant.ReplyType.GET_GROUP_BACK:
                if (ParseUtil.parseGroup(message, obGroups, tcpSend)) {
                    if (obNodes.size() == 0) {
                        getParmFinish();
                    } else {
                        MathUtil.nodeFindGroup(obGroups, obNodes, false);
                        tcpSend.reqScene(ObScene.OBSCENE_ID, 0, 1);
                    }
                }
                break;
            case OBConstant.ReplyType.GET_SCENE_BACK:
                if (ParseUtil.parseScene(message, obScenes, obNodes, obGroups, tcpSend)) {
                    getParmFinish();
                }
                break;
            case OBConstant.ReplyType.WRONG_TIME_OUT:
                tcpSend.sendBefore();
                break;
            case OBConstant.ReplyType.WRONG_CRC:
                if (isReqOboxMsg) {
                    supplementReqOboxMsg();
                }
                break;
            case OBConstant.ReplyType.NOT_REPLY:
                for (TcpSend tcp :
                        localDataPool.getTcpSends().values()) {
                    Log.d(TAG, "tcp config:: name = " + tcp.getOboxName() + "ip = " + tcp.getIp());
                }
                Log.d(TAG, "NOT_REPLY");
                tcpSend.sendBefore();
                break;
        }
    }

    private void supplementReqOboxMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isReqOboxMsg = true;
                tcpSend.reqOboxMsg();
            }
        }).start();
    }

    /**
     * 获取设备参数结束，发送服务器配置到设备
     */
    abstract void getParmFinish();


    /**
     * 获取设备状态,json格式传输
     *
     * @param type 设备类型
     */
    void getDeviceState(int type) {
        switch (type) {
            case OBConstant.OnAddWifiDeviceType.IR:
                UpLoadWifiIr upLoadWifiIr = new UpLoadWifiIr(serNum);
                configStr = upLoadWifiIr.getJsonStr();
                break;
            case OBConstant.OnAddWifiDeviceType.SOCKET:
                List<AliSpec> aliSpecsAction = new ArrayList<>();
                List<Object> objects = new ArrayList<>();
                objects.add(true);
                objects.add(false);
                List<String> strings = new ArrayList<>();
                strings.add("upload");
                strings.add("download");
                AliSpec aliSpec = new AliSpec("switch", "1", "Switch_1", "switch", "bool", objects, strings);
                aliSpecsAction.add(aliSpec);
                List<AliDevState> aliDevStates = new ArrayList<>();
                List<String> keyStrs = new ArrayList<>();
                List<Boolean> valueStrs = new ArrayList<>();
                /*vlaue是一个包含jsonobject的jsonarray*/
                String key = "1";
                keyStrs.add(key);
                valueStrs.add(false);
                for (int i = 0; i < keyStrs.size(); i++) {
                    AliDevState aliDevState = new AliDevState(keyStrs.get(i), valueStrs.get(i));
                    aliDevStates.add(aliDevState);
                }
                WifiDevice wifiDevice = new WifiDevice(serNum, "Socket", "50", aliSpecsAction, aliDevStates);
                configStr = new Gson().toJson(wifiDevice);
                break;
        }
        HttpRequst.getHttpRequst().request(ConnectDeviceHandler.this, CloudConstant.CmdValue.UPLOAD_CONFIG,
                GetParameter.uploadConfig(deviceName, productKey, configStr), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 连接路由器超时handler标志
     */
    static final int CON_ROUTE_TIMEOUT = 0;
    /**
     * 连接云端超时handler标志
     */
    static final int CON_CLOUD_TIMEOUT = 1;
    /**
     * 连接路由器超时时间
     */
    static final int CON_ROUTE_MAX_TIME = 60000;
    /**
     * 连接云超时时间
     */
    static final int CON_CLOUD_MAX_TIME = 120000;

    @SuppressWarnings("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CON_ROUTE_TIMEOUT:
                    onConnectRouteError();
                    break;
                case CON_CLOUD_TIMEOUT:
                    onConnectCloudError();
                    break;
            }
        }
    };

    /**
     * 开始执行激活操作
     */
    public abstract void start();

    /**
     * 释放资源操作
     */
    public void releaseSource() {
        EventBus.getDefault().unregister(this);
        localDataPool.unRegist(this);
    }

    /**
     * 处理smartconfig交互数据
     *
     * @param bytes smartconfig设备返回数据
     */
    public abstract void onReceive(byte[] bytes);

    /**
     * 设备开始连接到路由器
     */
    public abstract void onSendDeviceToRoute();

    /**
     * 设备成功连接到目标路由器
     */
    public abstract void onScendDeviceToRouteSuc();

    /**
     * 连接路由器出错
     */
    public abstract void onConnectRouteError();

    /**
     * 设备开始连接到云端
     */
    public abstract void onSendDeviceToCloud();

    /**
     * 连接云失败回调，超时未收到设备上线推送则认为连接云失败
     */
    public abstract void onConnectCloudError();

    /**
     * 添加wifi设备成功
     *
     * @param configStr wifi设备的配置详情
     */
    protected abstract void addWifiDeviceSuc(String configStr);

    /**
     * 添加obox成功
     *
     * @param obox 新添加的obox
     */
    protected abstract void addOboxSuc(Obox obox);

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EventMsg eventMsg) {
        String action = eventMsg.getAction();
        if (action != null) {
            switch (action) {
                case OBConstant.StringKey.OBOX_HEART_INFO:
                case OBConstant.StringKey.WIFI_HEART_INFO:
                    String onLine = (String) eventMsg.getExtra("onLine");
                    String serialId = (String) eventMsg.getExtra("serialId");
                    if (serialId.equals(serNum) && onLine.equals("true")) {
                        if (type == OBConstant.OnAddWifiDeviceType.OBOX) {
                            onCloudAddOboxSuc();
                            addOboxSuc(obox);
                        } else {
                            sendBroadUpdateWifiDevice();
                            addWifiDeviceSuc(configStr);
                        }
                        handler.removeMessages(CON_CLOUD_TIMEOUT);
                    }
                    break;
            }
        }
    }

}
