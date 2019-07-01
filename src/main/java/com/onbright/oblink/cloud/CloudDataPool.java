package com.onbright.oblink.cloud;

import com.onbright.oblink.cloud.bean.AliConfig;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Device;
import com.onbright.oblink.cloud.bean.Group;
import com.onbright.oblink.cloud.handler.OboxHandler;
import com.onbright.oblink.local.Obox;
import com.onbright.oblink.local.net.OBConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据仓库
 * Created by adolf_dong on 2016/5/11.
 */
public class CloudDataPool {
    private static List<Obox> oboxs = new ArrayList<>();

    /**
     * 服务器当前单节点数据
     */
    private static List<Device> devices = new ArrayList<>();
    private static List<Group> groups = new ArrayList<>();
    private static List<CloudScene> cloudScenes = new ArrayList<>();
    private static List<AliConfig> aliDevices = new ArrayList<>();


    public static List<Device> getDevices() {
        return devices;
    }

    /**
     * 获取当前的obox列表
     */
    public static List<Obox> getAllOboxList() {
        return oboxs;
    }

    /**
     * 添加obox到当前列表
     */
    public static void addAllOboxs(Obox obox) {
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
     * @param oboxSer
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
    public static List<AliConfig> getAliDevices() {
        return aliDevices;
    }

    /**
     * 添加wifi单品设备,防止数据重复
     *
     * @param aliConfig 被添加的WiFi单品设备
     */
    public static void addAliDevice(AliConfig aliConfig) {
        for (int i = 0; i < aliDevices.size(); i++) {
            AliConfig aliConfigIndex = aliDevices.get(i);
            if (aliConfigIndex.getDeviceId().equals(aliConfig.getDeviceId())) {
                return;
            }
        }
        getAliDevices().add(aliConfig);
    }

}

