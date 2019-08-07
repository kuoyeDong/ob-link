package com.onbright.oblink.cloud;

import com.onbright.oblink.cloud.bean.WifiDevice;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.bean.Group;
import com.onbright.oblink.Obox;
import com.onbright.oblink.local.net.OBConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据仓库，设定观察者，数据发生变化时发出相应通知
 *
 * @author dky
 * 2019/7/3.
 */
public class CloudDataPool {
    private static List<Obox> oboxs = new ArrayList<>();

    /**
     * 服务器当前单节点数据
     */
    private static List<Device> devices = new ArrayList<>();
    private static List<Group> groups = new ArrayList<>();
    private static List<CloudScene> cloudScenes = new ArrayList<>();
    private static List<WifiDevice> wifiDevices = new ArrayList<>();


    public static List<Device> getDevices() {
        return devices;
    }

    /**
     * 获取当前的obox列表
     */
    public static List<Obox> getOboxs() {
        return oboxs;
    }

    /**
     * 添加obox到当前列表
     */
    public static void addObox(Obox obox) {
        oboxs.add(obox);
    }

    /**
     * 获取当前的scenes列表
     */
    public static List<CloudScene> getCloudScenes() {
        if (cloudScenes == null) {
            cloudScenes = new ArrayList<>();
        }
        return cloudScenes;
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
            for (int i = 0; i < getCloudScenes().size(); i++) {
                CloudScene cs = getCloudScenes().get(i);
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
    public static void addGroups(Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }

    /**
     * 获取当前的group列表
     */
    public static List<Group> getGroups() {
        return groups;
    }

    /**
     * 删除obox数据，设备，组，
     *
     * @param oboxSer 删除数据的obox序列号
     */
    public static void deleteOboxData(String oboxSer) {
        if (devices != null) {
            for (int i = 0; i < devices.size(); i++) {
                if (devices.get(i).getObox_serial_id().equals(oboxSer)) {
                    devices.remove(i);
                    i--;
                }
            }
        }
        if (groups != null) {
            for (int i = 0; i < groups.size(); i++) {
                Group group = groups.get(i);
                if (group.getObox_serial_id() != null) {
                    if (group.getGroup_style().equals("00") && group.getObox_serial_id().equals(oboxSer)) {
                        groups.remove(i);
                        i--;
                    }
                } else if (group.getGroup_member() != null) {
                    for (int j = 0; j < group.getGroup_member().size(); j++) {
                        if (group.getGroup_member().get(j).getObox_serial_id().equals(oboxSer)) {
                            group.getGroup_member().remove(j);
                            j--;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return 连接阿里云的wifi设备
     */
    public static List<WifiDevice> getWifiDevices() {
        return wifiDevices;
    }

    /**
     * 添加wifi单品设备,防止数据重复
     *
     * @param wifiDevice 被添加的WiFi单品设备
     */
    public static void addWifiDevice(WifiDevice wifiDevice) {
        for (int i = 0; i < wifiDevices.size(); i++) {
            WifiDevice wifiDeviceIndex = wifiDevices.get(i);
            if (wifiDeviceIndex.getDeviceId().equals(wifiDevice.getDeviceId())) {
                return;
            }
        }
        wifiDevices.add(wifiDevice);
    }

    /**
     * 删除wifi节点设备
     *
     * @param wifiDeviceId wifi节点设备序列号
     */
    public static void deleteWifiDevice(String wifiDeviceId) {
        for (WifiDevice wifiDevice :
                wifiDevices) {
            if (wifiDeviceId.equals(wifiDevice.getDeviceId())) {
                wifiDevices.remove(wifiDevice);
                break;
            }
        }
    }
}

