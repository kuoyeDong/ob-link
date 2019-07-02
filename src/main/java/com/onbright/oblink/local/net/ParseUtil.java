package com.onbright.oblink.local.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.Obox;
import com.onbright.oblink.local.bean.ActionReply;
import com.onbright.oblink.local.bean.AirClean;
import com.onbright.oblink.local.bean.Aircon;
import com.onbright.oblink.local.bean.Als;
import com.onbright.oblink.local.bean.Ammeter;
import com.onbright.oblink.local.bean.Body;
import com.onbright.oblink.local.bean.Co;
import com.onbright.oblink.local.bean.Cooker;
import com.onbright.oblink.local.bean.DoorWindowSensor;
import com.onbright.oblink.local.bean.ElectricCard;
import com.onbright.oblink.local.bean.EnvironmentSensor;
import com.onbright.oblink.local.bean.Environmental;
import com.onbright.oblink.local.bean.Fan;
import com.onbright.oblink.local.bean.Flood;
import com.onbright.oblink.local.bean.Handset;
import com.onbright.oblink.local.bean.Humidifier;
import com.onbright.oblink.local.bean.Lamp;
import com.onbright.oblink.local.bean.Light;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.bean.ObSensor;
import com.onbright.oblink.local.bean.Obsocket;
import com.onbright.oblink.local.bean.Pm;
import com.onbright.oblink.local.bean.Pm25Sensor;
import com.onbright.oblink.local.bean.PowerCheck;
import com.onbright.oblink.local.bean.Radar;
import com.onbright.oblink.local.bean.RedOut;
import com.onbright.oblink.local.bean.RedSensor;
import com.onbright.oblink.local.bean.Remoter;
import com.onbright.oblink.local.bean.SceneAction;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.bean.SensingPanelSensor;
import com.onbright.oblink.local.bean.SmartLock;
import com.onbright.oblink.local.bean.Smoke;
import com.onbright.oblink.local.bean.TempHumid;
import com.onbright.oblink.local.bean.TheCurtain;
import com.onbright.oblink.local.bean.Timing;
import com.onbright.oblink.local.bean.Tv;
import com.onbright.oblink.local.bean.UltraSound;
import com.onbright.oblink.local.bean.WinCurtain;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 解析类
 * Created by adolf_dong on 2016/5/19.
 */
public class ParseUtil {


    public static byte[] getBytes(Message msg) {
        return msg.getData().getByteArray(OBConstant.StringKey.KEY);
    }

    public static int[] index = new int[65];

    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }


    /**
     * 获取oboxID
     *
     * @param msg                 msg
     * @param obox
     * @param mySharedPreferences sharep数据
     * @param tcpSend             网络发送端
     * @param oboxSSID            保存当前连接名字的列表
     * @param context             环境
     * @param wrongPwd            是否在网络错误时刻      @return oboxID
     */
    public static String getOboxId(Message msg, Obox obox, SharedPreferences mySharedPreferences,
                                   TcpSend tcpSend, List<String> oboxSSID,
                                   Context context, boolean wrongPwd) {
        byte[] bytes = getBytes(msg);
        return getWifiName(mySharedPreferences, obox,
                bytes, tcpSend, oboxSSID,
                context, wrongPwd);
    }

    public static boolean isActivator(Message msg) {
        byte[] bytes = getBytes(msg);
        return MathUtil.byteIndexValid(bytes[index[9]], 0) == 1;
    }

    public static boolean isOnServer(Message msg) {
        byte[] bytes = getBytes(msg);
        return MathUtil.byteIndexValid(bytes[index[9]], 1) == 1;
    }

    /**
     * 解析单节点并决定流程终止或继续
     *
     * @param msg     msg
     * @param obNodes 节点装载容器
     * @param tcpSend 当前对应连接
     * @return 取单节点流程结束返回true，否则false,可根据返回值确定之后执行流程
     */
    public static boolean parseDevice(Message msg, List<ObNode> obNodes, TcpSend tcpSend) {
        byte[] bytes = getBytes(msg);
        byte num = bytes[9];
        byte[] rfaddr = Arrays.copyOfRange(bytes, 10, 15);
        byte addr = bytes[15];
        if (addr == 0) {
            return true;
        }
        byte[] id = Arrays.copyOfRange(bytes, 16, 32);
        byte[] serNum = Arrays.copyOfRange(bytes, 32, 37);
        byte parentType = (byte) MathUtil.byteIndexValid(bytes[37], 0, 7);
        byte type = bytes[38];
        byte[] version = Arrays.copyOfRange(bytes, 39, 47);
        byte surplusSence = bytes[47];
        byte groupAddr = 0;
        /*首次实例化对象没有组新信息和状态信息*/
        ObNode obNode = new ObNode(num, rfaddr, addr, id,
                serNum, parentType, type,
                version, surplusSence, groupAddr, null);
        addNodeWithType(obNodes, obNode);
        int index = MathUtil.validByte(num);
        if (index != 0xff) {
            tcpSend.getDevice(++index, false);
            return false;
        }
        return true;
    }


    /**
     * 根据不同类型做不同添加
     */
    private static void addNodeWithType(List<ObNode> obNodes, ObNode obNode) {
        byte num = obNode.getNum();
        byte[] rfAddr = obNode.getRfAddr();
        byte addr = obNode.getAddr();
        byte[] id = obNode.getId();
        byte[] serNum = obNode.getSerNum();
        byte parentType = (byte) obNode.getParentType();
        byte type = (byte) obNode.getType();
        byte[] version = obNode.getVersion();
        byte surplusSence = obNode.getSurplusSence();
        byte gourpAddr = obNode.getGroupAddr();
        byte[] state = obNode.getState();
        switch (MathUtil.validByte((byte) obNode.getParentType())) {
            case OBConstant.NodeType.IS_LAMP:
                Lamp lam = new Lamp(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(lam);
                break;

            case OBConstant.NodeType.IS_COOKER:
                Cooker cooker = new Cooker(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(cooker);
                break;

            case OBConstant.NodeType.IS_HUMIDIFIER:
                Humidifier hum = new Humidifier(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(hum);
                break;
            case OBConstant.NodeType.IS_OBSOCKET:
                Obsocket obsocket = new Obsocket(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(obsocket);
                break;
            case OBConstant.NodeType.IS_CURTAIN:
                onParseCurtain(obNodes, obNode, num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                break;
            case OBConstant.NodeType.IS_FAN:
                Fan fan = new Fan(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(fan);
                break;
            case OBConstant.NodeType.IS_AIR_CLEAN:
                AirClean airClean = new AirClean(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(airClean);
                break;
            case OBConstant.NodeType.IS_TV:
                Tv tv = new Tv(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(tv);
                break;

            case OBConstant.NodeType.CONTROL_PANEL:
                onParseControlPanel(obNodes, obNode, num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
                break;
            case OBConstant.NodeType.IS_SENSOR:
                onParseSensor(obNodes, obNode, num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                break;
            case OBConstant.NodeType.AMMETER:
                Ammeter am = new Ammeter(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(am);
                break;
            case OBConstant.NodeType.SMART_LOCK:
                SmartLock smartLock = new SmartLock(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(smartLock);
                break;
            case OBConstant.NodeType.RED_OUT:
                RedOut redOut = new RedOut(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(redOut);
                break;
            case OBConstant.NodeType.REMOTER:
                Remoter remoter = new Remoter(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(remoter);
                break;
            case OBConstant.NodeType.REMOTE_MULTI_LED:
                ObNode remoteLed = new ObNode();
                remoteLed.setAddr((byte) 0xfe);
                remoteLed.setRfAddr(rfAddr);
                remoteLed.setSerNum(serNum);
                remoteLed.setId("RemoteLed".getBytes());
                remoteLed.setParentType((byte) 22);
                obNodes.add(remoteLed);
                break;
        }
    }

    /**
     * 解析控制面板
     */
    private static void onParseControlPanel(List<ObNode> obNodes, ObNode obNode, byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        switch (obNode.getType()) {
            case OBConstant.NodeType.AIR_CON_PANEL:
                Aircon aircon = new Aircon(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(aircon);
                break;
        }

    }

    /**
     * 解析幕布
     */
    private static void onParseCurtain(List<ObNode> obNodes, ObNode obNode, byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        switch (obNode.getType()) {
            case OBConstant.NodeType.WINDOW_CURTAINS:
                WinCurtain winCurtain = new WinCurtain(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(winCurtain);
                break;
            case OBConstant.NodeType.THE_CURTAINS:
                TheCurtain theCurtain = new TheCurtain(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(theCurtain);
                break;
        }
    }

    /**
     * 此方法只处理收到新节点的情况，其他2003返回在msg.what处理
     * 设备类型，设备子类型，设备id，序列号， 完整地址
     *
     * @param msg 数据
     * @return 本机已经有缓存数据，返回false,否则返回true
     */
    public static ObNode parseNewNode(Message msg, Map<String, List<ObNode>> obNodeListMap) {
        byte[] bytes = getBytes(msg);
        byte parentType = (byte) MathUtil.byteIndexValid(bytes[index[9]], 0, 7);
        byte type = bytes[index[10]];
        byte[] id = Arrays.copyOfRange(bytes, index[11], index[11] + 16);
        byte[] sernums = Arrays.copyOfRange(bytes, index[11] + 16, index[11] + 16 + 5);
        byte[] rfAddr = Arrays.copyOfRange(bytes, index[11] + 16 + 5, index[11] + 16 + 5 + 5);
        byte groupAddr = bytes[index[11] + 16 + 5 + 5];
        byte addr = bytes[index[11] + 16 + 5 + 5 + 1];
        ObNode newObNode = new ObNode(parentType, type, id, sernums,
                rfAddr, groupAddr, addr);
        if (parentType == 1) {
            newObNode.setState(new byte[]{20, 0, 0, 0, 0, 0, 1});
        }
        // FIXME: 2017/10/25 新入网的节点可能已经存在
        // rf地址没变化，覆盖参数；rf地址有变化，从原先的obox节点列表删除，添加到新入网时所在obox节点列表
        for (List<ObNode> obNodes : obNodeListMap.values()) {
            for (ObNode obNode : obNodes) {
                if (Arrays.equals(obNode.getSerNum(), newObNode.getSerNum())) {
                    if (Arrays.equals(obNode.getRfAddr(), newObNode.getRfAddr())) {
                        obNode.setState(newObNode.getState());
                        obNode.setGroupAddr(newObNode.getGroupAddr());
                        obNode.setAddr(newObNode.getAddr());
                        obNode.setCplAddr(newObNode.getCplAddr());
                        obNode.setId(newObNode.getId());
                    } else {
                        obNodes.remove(obNode);
                        addNodeWithType(obNodeListMap.get(Transformation.byteArryToHexString(newObNode.getRfAddr())), newObNode);
                    }
                    return newObNode;
                }
            }
        }
        addNodeWithType(obNodeListMap.get(Transformation.byteArryToHexString(newObNode.getRfAddr())), newObNode);
        return newObNode;
    }


    private static String getWifiName(SharedPreferences mySharedPreferences,
                                      Obox obox, byte[] data, TcpSend tcpSend, List<String> oboxSSID,
                                      Context context, boolean wrongPwd) {

        int encryptionType = MathUtil.byteIndexValid(data[index[9]], 0);
        boolean isConnetCloud = MathUtil.byteIndexValid(data[index[9]], 1) == 1;
        byte[] temp = new byte[16];
        System.arraycopy(data, index[10], temp, 0, 16);
        int i = 0;
        for (int j = 0; j < temp.length; j++) {
            if ((temp[j] & 0xff) == 0xff) {
                i = j;
                break;
            }
        }
//        temp[8] = '\0';
        String ID;
        String id = null;
        try {
            ID = new String(temp, "utf-8");
            id = ID.substring(0, i);
            boolean oboxNameExist = false;
            tcpSend.setOboxName(id);
            obox.setObox_name(id);
            tcpSend.setEncryptionType(encryptionType);
            for (String oboxName : oboxSSID) {
                if (oboxName.equalsIgnoreCase(id)) {
                    oboxNameExist = true;
                    break;
                }
            }
            if (!oboxNameExist) {
                oboxSSID.add(id);
//                tcpSend.setOboxName(id);
//                obox.setObox_name(id);
//                tcpSend.setEncryptionType(encryptionType);
            }
            /*ap模式并且没有被激活 */
//            if ((encryptionType == 0) && NetUtil.getWorkMode() == OBConstant.NetState.ON_AP) {
//                activateObox(tcpSend, context, mySharedPreferences, wrongPwd);
//            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return id;
    }


    /**
     * 当解析的参数是传感器
     */
    private static void onParseSensor(List<ObNode> obNodes, ObNode obNode, byte num, byte[] rfAddr,
                                      byte addr, byte[] id, byte[] serNum, byte parentType, byte type,
                                      byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        switch (obNode.getType()) {
            case OBConstant.NodeType.ALS:
                Als als = new Als(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(als);
                break;
            case OBConstant.NodeType.FLOOD:
                Flood flood = new Flood(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(flood);
                break;
            case OBConstant.NodeType.RADAR:
                Radar radar = new Radar(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(radar);
                break;
            case OBConstant.NodeType.CO:
                Co co = new Co(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(co);
                break;
            case OBConstant.NodeType.ENVIRONMENTAL:
                Environmental en = new Environmental(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(en);
                break;
            case OBConstant.NodeType.BODY:
                Body body = new Body(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(body);
                break;
            case OBConstant.NodeType.PM2_5:
                Pm pm = new Pm(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(pm);
                break;
            case OBConstant.NodeType.POWER_CHECK:
                PowerCheck pc = new PowerCheck(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(pc);
                break;

            case OBConstant.NodeType.VR_RADAR:
                Radar radar1 = new Radar(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(radar1);
                break;
            case OBConstant.NodeType.LIGHT_SENSOR:
                Light light = new Light(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(light);
                break;
            case OBConstant.NodeType.TEMP_HUMID_SENSOR:
                TempHumid tempHumid = new TempHumid(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(tempHumid);
                break;
            case OBConstant.NodeType.SMOKE_SENSOR:
                Smoke smoke = new Smoke(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(smoke);
                break;

            case OBConstant.NodeType.ULTRASOUND:
                UltraSound ultraSound = new UltraSound(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(ultraSound);
                break;
            case OBConstant.NodeType.PM2_5_SENSOR:
                Pm25Sensor pm25Sensor = new Pm25Sensor(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(pm25Sensor);
                break;
            case OBConstant.NodeType.HOTEL_RADAR:
                Radar radar2 = new Radar(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(radar2);
                break;
            case OBConstant.NodeType.RED_SENSOR:
            case OBConstant.NodeType.DC_RED_SENSOR:
                RedSensor redSensor = new RedSensor(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(redSensor);
                break;
            case OBConstant.NodeType.ENVROMENT_SENSOR:
                EnvironmentSensor environmentSensor = new EnvironmentSensor(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(environmentSensor);
                break;
            case OBConstant.NodeType.SENSING_PANEL:
                SensingPanelSensor sensingPanelSensor = new SensingPanelSensor(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(sensingPanelSensor);
                break;
            case OBConstant.NodeType.DOOR_WINDOW_MAGNET:
                DoorWindowSensor doorWindowSensor = new DoorWindowSensor(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(doorWindowSensor);
                break;
            case OBConstant.NodeType.ELECTRIC_CARD:
                ElectricCard electricCard = new ElectricCard(num, rfAddr, addr, id,
                        serNum, parentType, type,
                        version, surplusSence, gourpAddr, state);
                obNodes.add(electricCard);
                break;
        }
    }


    /**
     * 获取组并返回取组节点是否结束
     *
     * @param msg          msg
     * @param groupDevices 组装载容器
     * @param tcpSend      当前连接
     * @return 取组结束返回true
     */
    public static boolean parseGroup(Message msg, List<ObGroup> groupDevices, TcpSend tcpSend) {
        byte[] bytes = getBytes(msg);
        byte num = bytes[index[10]];
        byte addr = bytes[index[11]];
        if (addr == 0) {
            return true;
        }
        byte[] id = Arrays.copyOfRange(bytes, index[12], index[12] + 16);
        byte[] indexs = Arrays.copyOfRange(bytes, index[28], index[28] + 32);
        ObGroup obGroup = new ObGroup(num, addr, id, indexs);
        groupDevices.add(obGroup);
        int index = MathUtil.validByte(num);
        if (index != 0xff) {
            tcpSend.getDevice(++index, true);
            return false;
        }
        return true;
    }

    /**
     * 设置设备状态
     *
     * @param msg     msg
     * @param obNodes 待设置状态设备容器
     * @param tcpSend 当前连接
     * @return 流程结束返回true
     */
    public static boolean parseDeviceState(Message msg, List<ObNode> obNodes, TcpSend tcpSend) {
        int stateStart = 15;
        byte[] bytes = getBytes(msg);
        byte[] rfAddr = Arrays.copyOfRange(bytes, 7, 12);
        int addr = bytes[13] & 0xff;
        int len = obNodes.size();
        boolean isTwo = false;
        for (int i = 0; i < len; i++) {
            ObNode ld = obNodes.get(i);
            /*比较rf和节点地址即可，组地址不考虑*/
            if (Arrays.equals(ld.getRfAddr(), rfAddr) && (ld.getAddr() & 0xff) == addr) {
                byte[] state;
                switch (ld.getParentType()) {
                    case OBConstant.NodeType.IS_LAMP:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 7]);
                        break;
                    case OBConstant.NodeType.IS_COOKER:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 7]);
                        break;
                    case OBConstant.NodeType.IS_HUMIDIFIER:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 7]);
                        break;
                    case OBConstant.NodeType.IS_OBSOCKET:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 7]);
                        break;
                    case OBConstant.NodeType.IS_CURTAIN:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 7]);
                        break;
                    case OBConstant.NodeType.IS_FAN:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 7]);
                        break;
                    case OBConstant.NodeType.CONTROL_PANEL:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 7]);
                        break;
                    case OBConstant.NodeType.IS_SENSOR:
                        switch (ld.getType()) {
                            case OBConstant.NodeType.ENVROMENT_SENSOR:
                                isTwo = true;
                                backIndex++;
                                if (backIndex < 2) {
                                    tcpSend.setMustRec(true);
                                }
                                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 6]);
                                if (MathUtil.byteIndexValid(state[0], 4, 4) == 0) {
                                    System.arraycopy(state, 0, ld.getState(), 0, 6);
                                } else {
                                    System.arraycopy(state, 0, ld.getState(), 6, 6);
                                }
                                if (backIndex < 2) {
                                    return false;
                                } else {
                                    tcpSend.setMustRec(false);
                                }
                                break;
                            default:
                                state = parseSensorState(stateStart, bytes, ld);
                                break;
                        }
                        break;
                    case OBConstant.NodeType.AMMETER:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 8]);
                        break;
                    case OBConstant.NodeType.IS_AIR_CLEAN:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 5]);
                        break;
                    default:
                        state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 8]);
                        break;
                }
                backIndex = 0;
                if (!isTwo) {
                    ld.setState(state);
                }
                int index = i + 1;
                if (index < obNodes.size()) {
                    tcpSend.getDeviceState(obNodes.get(index).getCplAddr(), null);
                    return false;
                }
            }

        }
        return true;
    }

    private static int backIndex;

    /**
     * 返回当前传感器的状态
     *
     * @param stateStart 截取开始位置
     * @param bytes      传入字节数组
     * @param ld         当前节点
     * @return 对应的状态数组
     */
    private static byte[] parseSensorState(int stateStart, byte[] bytes, ObNode ld) {
        byte[] state;
        switch (ld.getType()) {
            case OBConstant.NodeType.ALS:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 4]);
                break;
            case OBConstant.NodeType.FLOOD:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 4]);
                break;
            case OBConstant.NodeType.RADAR:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 4]);
                break;

            case OBConstant.NodeType.CO:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 4]);
                break;
            case OBConstant.NodeType.ENVIRONMENTAL:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 8]);
                break;
            case OBConstant.NodeType.BODY:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 6]);
                break;
            case OBConstant.NodeType.PM2_5:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 5]);
                break;
            case OBConstant.NodeType.POWER_CHECK:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 5]);
                break;
            case OBConstant.NodeType.HOTEL_RADAR:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 8]);
                break;
            case OBConstant.NodeType.LIGHT_SENSOR:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 8]);
                break;
            default:
                state = Arrays.copyOfRange(bytes, index[stateStart], index[stateStart + 8]);
                break;
        }
        return state;
    }

    public static byte[] parseNodeVersion(Message msg) {
        byte[] datas = ParseUtil.getBytes(msg);
        byte[] version = Arrays.copyOfRange(datas, index[17], index[17] + 16);
        return version;
    }


    /**
     * 解析obox,对obox各项参数进行设置,序列号，版本号
     *
     * @param msg     msg
     * @param obox    obox
     * @param tcpSend 当前选择的连接
     */
    public static void parseObox(Message msg, Obox obox, TcpSend tcpSend, SharedPreferences mySharedPreferences) {
        byte[] bytes = getBytes(msg);
        byte[] serNum = Arrays.copyOfRange(bytes, index[11], index[11] + 5);
        byte[] version = Arrays.copyOfRange(bytes, index[11] + 5, index[11] + 5 + 8);
        String serNumStr = Transformation.byteArryToHexString(serNum);
        tcpSend.setPSW(mySharedPreferences.getString(serNumStr, OBConstant.StringKey.PSW));
        obox.setObox_pwd(tcpSend.getPSW());
        tcpSend.setRfAddr(serNum);
        obox.setObox_serial_id(serNum);
        obox.setObox_version(version);
        if (isOnServer(msg)) {
            obox.setObox_status("1");
        } else {
            obox.setObox_status("0");
        }
        if (isActivator(msg)) {
            obox.setObox_activate("1");
        } else {
            obox.setObox_activate("0");
        }
//        obox.setObox_person("0");
//        obox.setObox_control("0");
    }


    /**
     * 200e
     * 解析和情景相关的返回,当编号是1，则解析情景id，编号2则解析情景条件  3则解析情景绑定的行为
     * 每当一个流程结束后，要么替换当前连接继续找寻情景，要么执行到此结束
     *
     * @param msg      传入消息
     * @param obScenes 本地情景列表
     * @param tcpSend  当前连接
     * @return 结束返回true
     */
    public static boolean parseScene(Message msg, List<ObScene> obScenes, List<ObNode> nodes, List<ObGroup> obGroups, TcpSend tcpSend) {
        byte[] data = getBytes(msg);
        int type = data[index[8]];
        switch (type) {
            case ObScene.OBSCENE_ID:
                return parseSceneId(obScenes, tcpSend, data);
            case ObScene.OBSCENE_CONDITION:
                return parseSceneCondition(obScenes, nodes, tcpSend, data);
            case ObScene.OBSCENE_ACTION:
                return parseSceneAction(obScenes, nodes, obGroups, tcpSend, data);
        }
        return false;
    }

    /**
     * 解析情景内的行为节点
     *
     * @return 当结束后也即当前obox情景部分信息收集完毕
     */
    private static boolean parseSceneAction(List<ObScene> obScenes, List<ObNode> nodes, List<ObGroup> obGroups, TcpSend tcpSend, byte[] data) {
        int sceneSernum = MathUtil.validByte(data[index[10]]);
                /*此处num只是帧数标志，每个帧，只要此位置不为0xff则其内必然包含三个行为节点，
                否则，若此位置为0xff则表示情景内部节点读取结束，需判断帧内具体的行为节点数量*/
        ObScene obScene = getObSceneforSer(obScenes, sceneSernum);
        /*通过完整地址查找对应节点添加到数据*/
        boolean isFault = false;
        for (int i = 0; i < 3; i++) {
            byte[] cplAddr = Arrays.copyOfRange(data, index[12 + 15 * i], index[19 + 15 * i]);
            if (MathUtil.byteArrayIsZero(cplAddr)) {
                isFault = true;
            }
            byte[] action = Arrays.copyOfRange(data, index[19 + 15 * i], index[27 + 15 * i]);
            /*组地址不为0则为组，否则为单节点*/
            boolean isGroup = MathUtil.validByte(cplAddr[index[6]]) != 0;
            initAction(isGroup, obGroups, nodes, sceneSernum, obScene, cplAddr, action);
        }
        int num = MathUtil.validByte(data[index[11]]);
        if (num != 0xff && (!isFault)) {
            tcpSend.reqScene(ObScene.OBSCENE_ACTION, sceneSernum, ++num);
        } else {
            int scenenum = obScene.getNum();
            int scenegroup = obScene.getSceneGroup();
            /*是最后一个情景？*/
            if (scenenum == 0xff || scenegroup == 0xffff) {
                return true;
            } else {
                tcpSend.reqScene(ObScene.OBSCENE_ID, 0, ++scenenum);
            }
        }
        return false;
    }

    /**
     * 根据完整地址实例化情景内节点
     */
    private static void initAction(boolean isgourp, List<ObGroup> obGroups, List<ObNode> obNodes,
                                   int sceneSernum, ObScene obScene, byte[] cplAddr, byte[] action) {
        for (int nodeIndex = 0; nodeIndex < (isgourp ? obGroups.size() : obNodes.size()); nodeIndex++) {
            SceneAction sceneAction;
            if (isgourp) {
                sceneAction = obGroups.get(nodeIndex);
                if (Arrays.equals(Arrays.copyOf(sceneAction.getAddrs(), 5), Arrays.copyOf(cplAddr, 5))
                        && MathUtil.validByte(sceneAction.getAddrs()[index[6]]) == MathUtil.validByte(cplAddr[index[6]])) {
                    sceneAction.putAction(sceneSernum, action);
                    obScene.getObNodes().add(sceneAction);
                    break;
                }
            } else {
                sceneAction = obNodes.get(nodeIndex);
                /*单节点不比较组地址*/
                if (Arrays.equals(Arrays.copyOf(sceneAction.getAddrs(), 5), Arrays.copyOf(cplAddr, 5))
                        && MathUtil.validByte(sceneAction.getAddrs()[index[7]]) == MathUtil.validByte(cplAddr[index[7]])) {
                    sceneAction.putAction(sceneSernum, action);
                    obScene.getObNodes().add(sceneAction);
                    break;
                }
            }

        }
    }

    /**
     * 解析情景条件
     */
    private static byte conditionIndex = 1;

    private static boolean parseSceneCondition(List<ObScene> obScenes, List<ObNode> nodes, TcpSend tcpSend, byte[] data) {
        ++conditionIndex;
        int option = MathUtil.byteIndexValid(data[index[9]], 0, 3);
        int sernum = MathUtil.validByte(data[index[10]]);
        ObScene obScene = getObSceneforSer(obScenes, sernum);
        List<SceneCondition> sceneConditions = new ArrayList<>();
        if (obScene == null) {
            return true;
        }
        if ((option != 0) && (MathUtil.validByte(data[index[12]]) != 0xff)) {
            for (int i = 0; i < 3; i++) {
                int conditionType = MathUtil.byteIndexValid(data[index[12]], 2 * i, 2);
                byte[] conditionaddr = Arrays.copyOfRange(data, index[13 + i * 15], index[20 + i * 15]);
                final byte[] condition = Arrays.copyOfRange(data, index[20 + i * 15], index[28 + i * 15]);
                SceneCondition sceneCondition = null;
                switch (conditionType) {
                    case SceneCondition.SIMPLE:
                        // FIXME: 2016/7/6 如果所有的情景条件都是simple表示此情景为一般情景
                        continue;
                    case SceneCondition.TIMING:
                        sceneCondition = new Timing(condition);
                        sceneConditions.add(sceneCondition);
                        break;
                    case SceneCondition.SENSOR:
                        for (int index = 0; index < nodes.size(); index++) {
                            ObNode obNode = nodes.get(index);
                            if (Arrays.equals(conditionaddr, obNode.getCplAddr())) {
                                if (obNode instanceof ObSensor) {
                                    sceneCondition = (ObSensor) obNode;
                                    sceneCondition.setCondition("" + obScene.getSerisNum(), condition);
                                    sceneConditions.add(sceneCondition);
                                } else if (obNode instanceof Obsocket) {
                                    sceneCondition = (Obsocket) obNode;
                                    sceneCondition.setCondition("" + obScene.getSerisNum(), condition);
                                    sceneConditions.add(sceneCondition);
                                } else if (obNode instanceof SmartLock) {
                                    sceneCondition = (SmartLock) obNode;
                                    sceneCondition.setCondition("" + obScene.getSerisNum(), condition);
                                    sceneConditions.add(sceneCondition);
                                }

                                break;
                            }
                        }
                        break;
                    case SceneCondition.CONTROL:
                        /*获取绑定的obox列表*/
                        Handset hs = new Handset();
                        hs.setBindOboxs(null);
                        sceneCondition = hs;
                        sceneConditions.add(sceneCondition);
                        break;
                    default:
                        break;
                }
                // FIXME: 2016/7/6 此处暂时先把所有的情景内条件统一管理
            }
            if (sceneConditions.size() > 0) {
                obScene.getSceneCondition().add(sceneConditions);
            }
        }
        /*ff没意义*/
        int num = MathUtil.validByte(data[index[11]]);
//        if (num != 0xff) {
        if (conditionIndex != 4) {
            tcpSend.reqScene(ObScene.OBSCENE_CONDITION, obScene.getSerisNum(), conditionIndex);
        } else {
            conditionIndex = 1;
            tcpSend.reqScene(ObScene.OBSCENE_ACTION, obScene.getSerisNum(), 1);
        }
        return false;
    }

    /**
     * 根据序列号返回目标情景
     *
     * @param obScenes 情景列表
     * @param sernum   序列号
     * @return 目标情景
     */
    private static ObScene getObSceneforSer(List<ObScene> obScenes, int sernum) {
        ObScene obScene = null;
        for (int i = 0; i < obScenes.size(); i++) {
            obScene = obScenes.get(i);
            if (obScene.getSerisNum() == sernum) {
                break;
            } else {
                obScene = null;
            }
        }
        return obScene;
    }

    /**
     * 解析情景id
     */
    private static boolean parseSceneId(List<ObScene> obScenes, TcpSend tcpSend, byte[] data) {
        int option = MathUtil.validByte(data[index[9]]);
        // FIXME: 2017/10/16 选项为f0同样也是结束标志
        if (option == 0xf0) {
            return true;
        }
        int serNum = MathUtil.validByte(data[index[10]]);
        int number = MathUtil.validByte(data[index[11]]);
        if (serNum != 0) {
            boolean isEnable = MathUtil.byteIndexValid(data[index[9]], 4) == 1;
            byte[] id = new byte[OBConstant.NodeType.ID_LEN];
            System.arraycopy(data, index[12], id, 0, id.length);
            int sceneGroup = MathUtil.validByte(data[index[12] + id.length]);
            ObScene obScene = new ObScene(serNum, id, isEnable, number);
            obScene.setRfAddr(tcpSend.getRfAddr());
            obScene.setSceneGroup(sceneGroup);
            obScenes.add(obScene);
        } else {
            /*情景序号为0且最后一个情景*/
            if (number == 0xff) {
                return true;
            } else {
                /*情景序号为0且不是最后一个情景*/
                tcpSend.reqScene(ObScene.OBSCENE_ID, 0, ++number);
                return false;
            }
        }
        tcpSend.reqScene(ObScene.OBSCENE_CONDITION, serNum, 1);
        return false;
    }

    /**
     * 设置节点状态返回
     *
     * @param message      传入message
     * @param isSingle     是否单节点
     * @param isGroup
     * @param obNode
     * @param obGroup
     */
    public static void onSetStatusRec(Message message, boolean isSingle, boolean isGroup, ObNode obNode, ObGroup obGroup) {
        byte[] bytes = getBytes(message);
        if (bytes == null) {
            return;
        }
        if (obNode == null) {
            return;
        }
        byte[] status = Arrays.copyOfRange(bytes, 15, 15 + 7);
        if (isSingle) {
            switch (message.what) {
                case OBConstant.ReplyType.SET_STATUS_SUC:
                    obNode.setState(status);
                    break;
                case OBConstant.ReplyType.SET_STATUS_FAL:

                    break;
            }
        } else if (isGroup) {
            if (obGroup == null) {
                return;
            }
            switch (message.what) {
                case OBConstant.ReplyType.SET_STATUS_SUC:
                    obGroup.setStatus(status);
                    break;
                case OBConstant.ReplyType.SET_STATUS_FAL:

                    break;
            }
        }
    }

    /**
     * 读取红外配置时候的解析
     *
     * @return 9字节的数据
     */
    public static byte[] onReadIrcfgRec(Message message) {
        byte[] bytes = getBytes(message);
        switch (message.what) {
            case OBConstant.ReplyType.SET_STATUS_SUC:
                return Arrays.copyOfRange(bytes, 15, 15 + 9);
            case OBConstant.ReplyType.SET_STATUS_FAL:

                break;
        }
        return null;
    }

    /**
     * 新增分组，删除或者重命名组或者节点的处理
     *
     * @param message
     * @param isSingle
     * @param obNode
     * @param obGroup
     */
    public static void onEditNodeOrGroup(Message message, boolean isSingle,
                                         ObNode obNode, ObGroup obGroup
                                         ) {
        switch (message.what) {
            case OBConstant.ReplyType.EDIT_NODE_OR_GROUP_SUC:
                byte[] datas = ParseUtil.getBytes(message);
                switch (MathUtil.byteIndexValid(datas[index[9]], 0, 2)) {
                    /*删除*/
                    case 0:
                        if (isSingle) {
                            LocalDataPool.newInstance().editNode(Transformation.byteArryToHexString(obNode.getRfAddr()), obNode, false);
                        } else {
                            if (obGroup.getObNodes() != null) {
                                for (int i = 0; i < obGroup.getObNodes().size(); i++) {
                                    obGroup.getObNodes().get(i).setGroupAddr((byte) 0);
                                }
                            }
                            LocalDataPool.newInstance().editGroup(Transformation.byteArryToHexString(obGroup.getRfAddr()), obGroup, false);
                        }
                        break;
                    /*新增*/
                    case 1:
                        if (!isSingle) {
                            obGroup.setRfAddrs(Arrays.copyOfRange(datas, index[10], index[10] + 5));
                            obGroup.setAddr(datas[index[15]]);
                            LocalDataPool.newInstance().editGroup(Transformation.byteArryToHexString(obGroup.getRfAddr()), obGroup, true);
                        }
                        break;
                    /*重命名*/
                    case 2:
                        byte[] ids = Arrays.copyOfRange(datas, index[17], index[17] + 16);
                        if (isSingle) {
                            if (obNode != null) {
                                obNode.setId(ids);
                            }
                        } else {
                            if (obGroup != null) {
                                obGroup.setId(ids);
                            }
                        }
                        break;
                }
                break;
            case OBConstant.ReplyType.EDIT_NODE_OR_GROUP_FAL:

                break;
        }
    }

    /**
     * 组操作处理
     *
     * @param obNode  操作的节点
     * @param obGroup 节点所在组或者节点要添加的组
     * @param msg     message
     */
    public static void onOrganizGoup(ObNode obNode, ObGroup obGroup, Message msg) {
        boolean isAdd = getBytes(msg)[index[14]] != 0;
        if (isAdd) {
            obNode.setGroupAddr(obGroup.getAddr());
            obGroup.getObNodes().add(obNode);
        } else {
            obNode.setGroupAddr((byte) 0);
            obGroup.getObNodes().remove(obNode);
        }
    }


    /**
     * 操作场景成功的返回处理
     *
     * @param isLink   是否链式场景
     * @param obScene  被操作的场景
     * @param obScenes 被操作的场景对象所在容器，删除或者增加传，用于在源数据内删除
     * @param message  返回message
     */
    public static void onEditScene(boolean isLink, ObScene obScene, List<ObScene> obScenes, Message message, byte[] msgbytes, List<ObNode> obNodes) {
        byte[] bytes;
        if (message != null) {
            bytes = getBytes(message);
        } else {
            bytes = msgbytes;
        }
        int operaType = bytes[index[9]];
        switch (operaType) {
            case ObScene.OBSCENE_ID:
                /*使能*/
                int vailable = MathUtil.byteIndexValid(bytes[index[10]], 4, 2);
                /*操作详情*/
                int operaOption = MathUtil.byteIndexValid(bytes[index[10]], 0, 4);
                obScene.setEnable(vailable == ObScene.ENABLE);
                switch (operaOption) {
                    case ObScene.CRETE:
                        /*去掉冗余字节*/
                        byte[] id = new byte[OBConstant.NodeType.ID_LEN];
                        obScene.setSerisNum(bytes[index[11]]);
                        System.arraycopy(bytes, index[12], id, 0, id.length);
                        int sceneGroup = MathUtil.validByte(bytes[index[12] + id.length]);
                        obScene.setSceneGroup(sceneGroup);
                        obScene.setId(MathUtil.validArray(id));
                        if (!isLink) {
                            obScenes.add(obScene);
                        }
                        break;
                    case ObScene.DELETE:
                        if (!isLink) {
                            obScenes.remove(obScene);
                        }
                        break;
                    case ObScene.MODIFY:
                        /*去掉冗余字节*/
                        byte[] id1 = new byte[OBConstant.NodeType.ID_LEN];
                        System.arraycopy(bytes, index[12], id1, 0, id1.length);
                        sceneGroup = MathUtil.validByte(bytes[index[12] + id1.length]);
                        obScene.setSceneGroup(sceneGroup);
                        obScene.setId(MathUtil.validArray(id1));
                        break;
                }
                break;
            case ObScene.OBSCENE_CONDITION:
                int conditionIndex = bytes[index[12]] - 1;
                if (!(conditionIndex < obScene.getSceneCondition().size())) {
                    return;
                }
                List<SceneCondition> conditions = obScene.getSceneCondition().get(conditionIndex);
                conditions.clear();
                for (int i = 0; i < 3; i++) {
                    int conditionType = MathUtil.byteIndexValid(bytes[index[13]], 2 * i, 2);
                    byte[] conditionaddr = Arrays.copyOfRange(bytes, index[14 + i * 15], index[21 + i * 15]);
                    final byte[] condition = Arrays.copyOfRange(bytes, index[21 + i * 15], index[29 + i * 15]);
                    SceneCondition sceneCondition = null;
                    switch (conditionType) {
                        case SceneCondition.SIMPLE:
                            // FIXME: 2016/7/6 如果所有的情景条件都是simple表示此情景为一般情景
                            continue;
                        case SceneCondition.TIMING:
                            sceneCondition = new Timing(condition);
                            break;
                        case SceneCondition.SENSOR:
                            for (int index = 0; index < obNodes.size(); index++) {
                                ObNode obNode = obNodes.get(index);
                                if (Arrays.equals(conditionaddr, obNode.getCplAddr())) {
                                    if (obNode instanceof SceneCondition) {
                                        sceneCondition = (SceneCondition) obNode;
                                        sceneCondition.setCondition("" + obScene.getSerisNum(), condition);
                                    }
                                    break;
                                }
                            }
                            break;
                        case SceneCondition.CONTROL:
                            // FIXME: 2016/7/1 暂不实现
                            sceneCondition = new Handset();
                            break;
                        default:
                            break;
                    }
                    conditions.add(sceneCondition);
                }
                break;
            case ObScene.OBSCENE_ACTION:
                /*就目前的obox回复不需要处理*/
                boolean needHandle = false;
                if (needHandle) {
                    for (int i = 0; i < 3; i++) {
                        switch (MathUtil.byteIndexValid(bytes[index[10]], 2 * i, 2)) {
                            case ObScene.ACTION_DELETE:
                                break;
                            case ObScene.ACTION_MODIFY:
                                break;
                            case ObScene.ACTION_HOLD:
                                break;
                        }
                    }
                }
                break;
        }
    }


    /**
     * 批量处理action
     *
     * @param srcActions   待处理数据源
     * @param index        start下标值
     * @param isDel        是否删除
     * @param tcpSend      当前连接
     * @param isAlldellete 是否全部清空
     * @return 操作之后的下标  当actionreply 返回 0和true的时候，并没有发送数据，请自行处理
     */
    public static ActionReply batAction(List<SceneAction> srcActions, int index, boolean isDel, TcpSend tcpSend, boolean isAlldellete) {
        List<SceneAction> sceneActions = new ArrayList<>();
        if (srcActions.size() == 0) {
            return new ActionReply(0, true);
        }
        int afterIndex = index;
        if (index + 3 < srcActions.size()) {
            for (int i = 0; i < 3; i++) {
                sceneActions.add(srcActions.get(index + i));
                afterIndex++;
            }
        } else {
            while (afterIndex < srcActions.size()) {
                sceneActions.add(srcActions.get(afterIndex));
                afterIndex++;
            }
        }
        tcpSend.editSceneAction(LocalDataPool.getCurentScene().getSerisNum(), sceneActions, isDel, isAlldellete);
        return new ActionReply(afterIndex, afterIndex >= srcActions.size());
    }

    /**
     * 修改场景的条件
     *
     * @param obScene 当前修改的本地场景
     * @param index   第几个条件组
     * @param tcpSend 当前连接
     */
    public static void batCondition(ObScene obScene, int index, TcpSend tcpSend) {
        if (obScene.getSceneCondition().size() > index) {
            tcpSend.editSceneCondition(obScene.getSerisNum(), index + 1, obScene.getSceneCondition().get(index));
        } else {
            tcpSend.editSceneCondition(obScene.getSerisNum(), index + 1, new ArrayList<SceneCondition>());
        }
    }

    /**
     * 获取obox的flash修改时间，此时间为obox内单节点、组结构、情景结构变化的时间
     *
     * @param message un
     * @return obox的flash修改时间
     */
    public static byte[] getOboxFlashTime(Message message) {
        byte[] bytes = getBytes(message);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) == 0xff) {
                index = i;
            }
        }
        return Arrays.copyOfRange(bytes, index + 1, index + 1 + 8);
    }
}
