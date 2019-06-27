package com.onbright.oblink.cloud;

import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.AliConfig;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.bean.DeviceConfig;
import com.onbright.oblink.cloud.bean.Groups;
import com.onbright.oblink.local.Obox;
import com.onbright.oblink.local.net.OBConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地连接装载数据的池
 * Created by adolf_dong on 2016/5/11.
 */
public class CloudDataPool {
    /**
     * 服务器当前单节点数据
     */
    private static List<DeviceConfig> devices = new ArrayList<>();
    private static CloudScene curentScene;

    private static List<Condition> curentConditions;
    private static Condition condition;
    private static Action curentAction;
    /**
     * 当前操作的链式情景列表
     */
    private static ArrayList<CloudScene> curentScenes;
    /**
     * 当前操作链式情景的action集
     */
    private static List<Action> curentSceneActions;
    /**
     * 智能门锁权限密码
     */
    private static String authToken;

    /**
     * 指向当前被选中DeviceConfig
     */
    private static DeviceConfig deviceConfig;

    public static DeviceConfig getDeviceConfig() {
        return deviceConfig;
    }

    public static void setDeviceConfig(DeviceConfig deviceConfig) {
        CloudDataPool.deviceConfig = deviceConfig;
    }

    private static Groups groups;

    public static Groups getGroups() {
        return groups;
    }

    public static void setGroups(Groups groups) {
        CloudDataPool.groups = groups;
    }

    public static List<DeviceConfig> getDevices() {
        return devices;
    }


    /**
     * 根据类型取节点
     *
     * @param nodeType 类型值
     */
    public static List<DeviceConfig> getDevicesForType(int nodeType) {
        List<DeviceConfig> deviceConfigs = new ArrayList<>();
        for (int i = 0; i < devices.size(); i++) {
            DeviceConfig deviceConfig = devices.get(i);
            int type = Integer.parseInt(deviceConfig.getDevice_type(), 16);
            if (type == nodeType) {
                deviceConfigs.add(deviceConfig);
            }
        }
        return deviceConfigs;
    }

    private static List<Obox> oboxList = new ArrayList<>();
    private static List<Obox> oboxAllList = new ArrayList<>();
    private static List<Groups> groupsList = new ArrayList<>();
    private static List<CloudScene> scenesList = new ArrayList<>();
    private static List<String> cameraCaptureList = new ArrayList<>();

    /**
     * 获取当前的obox列表
     */
    public static List<Obox> getOboxList() {
        if (oboxList == null) {
            oboxList = new ArrayList<>();
        }
        return oboxList;
    }

    /**
     * 添加obox到当前列表
     */
    public static void addObox(Obox obox) {
        if (oboxList == null) {
            oboxList = new ArrayList<>();
        }
        oboxList.add(obox);
    }

    /**
     * 获取当前的obox列表
     */
    public static List<Obox> getAllOboxList() {
        if (oboxAllList == null) {
            oboxAllList = new ArrayList<>();
        }
        return oboxAllList;
    }


    /**
     * 添加obox到当前列表
     */
    public static void addAllOboxs(Obox obox) {
        if (oboxAllList == null) {
            oboxAllList = new ArrayList<>();
        }
        oboxAllList.add(obox);
    }

    /**
     * 添加scenes到当前列表
     */
    public static void addScenes(CloudScene cloudScene) {
        if (scenesList == null) {
            scenesList = new ArrayList<>();
        }
        scenesList.add(cloudScene);
    }

    /**
     * 获取当前的scenes列表
     */
    public static List<CloudScene> getScenesList() {
        if (scenesList == null) {
            scenesList = new ArrayList<>();
        }
        return scenesList;
    }

    /**
     * 判断是否可以添加场景
     *
     * @param oboxSerString obox序列号
     * @param len           新增加的场景长度
     * @return 可以则返回true
     */
    public static boolean canAddScene(String oboxSerString, int len) {
        if (oboxSerString != null && !oboxSerString.equals("")) {
            int currentlen = 0;
            for (int i = 0; i < getScenesList().size(); i++) {
                CloudScene cs = getScenesList().get(i);
                String oboxSer = cs.getObox_serial_id();
                if (oboxSer != null && oboxSer.equals(oboxSerString)) {
                    currentlen++;
                }
            }
            return currentlen + len < OBConstant.MAX_SCENE_LIMIT;
        } else {
            return true;
        }
    }

    /**
     * 添加group到当前列表
     */
    public static void addGroups(Groups groups) {
        if (groupsList == null) {
            groupsList = new ArrayList<>();
        }
        groupsList.add(groups);
    }

    /**
     * 获取当前的group列表
     */
    public static List<Groups> getGroupList() {
        if (groupsList == null) {
            groupsList = new ArrayList<>();
        }
        return groupsList;
    }

    /**
     * 添加cameraCapture到当前列表
     */
    public static void addCameraCapture(String picUrl) {
        if (cameraCaptureList == null) {
            cameraCaptureList = new ArrayList<>();
        }
        cameraCaptureList.add(picUrl);
    }

    /**
     * 获取当前的cameraCapture列表
     */
    public static List<String> getcameraCaptureList() {
        if (cameraCaptureList == null) {
            cameraCaptureList = new ArrayList<>();
        }
        return cameraCaptureList;
    }

    public static void setCurentScene(CloudScene curentScene) {
        CloudDataPool.curentScene = curentScene;
    }

    public static CloudScene getCurentScene() {
        return curentScene;
    }

    public static void setCurentConditions(List<Condition> curentConditions) {
        CloudDataPool.curentConditions = curentConditions;
    }

    public static List<Condition> getCurentConditions() {
        return curentConditions;
    }

    public static Condition getCurentCondition() {
        return condition;
    }

    public static void setCurentCondition(Condition condition) {
        CloudDataPool.condition = condition;
    }

    public static void setCurentAction(Action curentAction) {
        CloudDataPool.curentAction = curentAction;
    }

    public static Action getCurentAction() {
        return curentAction;
    }

    public static void deleteOboxData(String oboxSer) {
        if (devices != null) {
            for (int i = 0; i < devices.size(); i++) {
                if (devices.get(i).getObox_serial_id().equals(oboxSer)) {
                    devices.remove(i);
                    i--;
                }
            }
        }
        if (groupsList != null) {
            for (int i = 0; i < groupsList.size(); i++) {
                Groups groups = groupsList.get(i);
                if (groups.getObox_serial_id() != null) {
                    if (groups.getGroup_style().equals("00") && groups.getObox_serial_id().equals(oboxSer)) {
                        groupsList.remove(i);
                        i--;
                    }
                } else if (groups.getGroup_member() != null) {
                    for (int j = 0; j < groups.getGroup_member().size(); j++) {
                        if (groups.getGroup_member().get(j).getObox_serial_id().equals(oboxSer)) {
                            groups.getGroup_member().remove(j);
                            j--;
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<CloudScene> getCurentScenes() {
        return curentScenes;
    }

    public static void setCurentScenes(ArrayList<CloudScene> curentScenes) {
        CloudDataPool.curentScenes = curentScenes;
    }

    public static void setCurentSceneActions(List<Action> curentSceneActions) {
        CloudDataPool.curentSceneActions = curentSceneActions;
    }

    public static List<Action> getCurentSceneActions() {
        return curentSceneActions;
    }

    public static void setDeviceState(DeviceConfig dev) {
        for (int i = 0; i < CloudDataPool.getDevices().size(); i++) {
            if (dev.getSerialId().equals(CloudDataPool.getDevices().get(i).getSerialId())) {
                CloudDataPool.getDevices().get(i).setState(dev.getState());
            }
        }
    }

    /**
     * 获取特定obox下特定类型的组
     *
     * @param obox_serial_id obox序列号
     * @param deviceType     类型
     * @param child_type     子类型
     * @return 目标列表
     */
    public static List<Groups> getGroupListForObox(String obox_serial_id, int deviceType, int child_type) {
        List<Groups> groupses = new ArrayList<>();
        for (int i = 0; i < getGroupList().size(); i++) {
            Groups groups = getGroupList().get(i);
            if (obox_serial_id.equals("") || (groups.getObox_serial_num() != null && groups.getObox_serial_num().equals(obox_serial_id))) {
                String groupType = groups.getGroup_type();
                if (deviceType == 0 || (groupType != null && Integer.parseInt(groupType, 16) == deviceType)) {
                    String childType = groups.getGroup_child_type();
                    if (child_type == 0 || (childType != null && Integer.parseInt(childType, 16) == child_type)) {
                        groupses.add(groups);
                    }
                }
            } else {
                String groupType = groups.getGroup_type();
                if (obox_serial_id.equals(groups.getObox_serial_id())) {
                    if (deviceType == 0 || (groupType != null && Integer.parseInt(groupType, 16) == deviceType)) {
                        String childType = groups.getGroup_child_type();
                        if (child_type == 0 || (childType != null && Integer.parseInt(childType, 16) == child_type)) {
                            groupses.add(groups);
                        }
                    }
                }
            }
        }
        return groupses;
    }


    private static List<AliConfig> aliConfigs = new ArrayList<>();

    /**
     * 返回连接阿里云的单品设备
     *
     * @return 设备集合
     */
    public static List<AliConfig> getAliDeviceList() {
        return aliConfigs;
    }

    /**
     * 添加wifi单品设备
     *
     * @param aliConfig 被添加的WiFi单品设备
     */
    public static void addAliDevice(AliConfig aliConfig) {
        for (int i = 0; i < aliConfigs.size(); i++) {
            AliConfig aliConfigIndex = aliConfigs.get(i);
            if (aliConfigIndex.getDeviceId().equals(aliConfig.getDeviceId())) {
                return;
            }
        }
        getAliDeviceList().add(aliConfig);
    }
}

