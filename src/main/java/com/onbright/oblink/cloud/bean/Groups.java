package com.onbright.oblink.cloud.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器组元素，分组即为单节点设定组结构，以便于在状态控制的时候更便捷
 */
public class Groups extends DeviceConfig {
    private String group_id;
    private String group_name;
    private String group_type;
    private String group_state;
    private String group_child_type;
    private String obox_serial_num;
    private String group_addr;
    private List<DeviceConfig> group_member;
    private ArrayList<DeviceConfig> children;
    private String group_style;

    /**
     * @param group_member     组成员
     * @param group_id         组id
     * @param group_name       组名称
     * @param group_type       组设备类型
     * @param group_state      组状态
     * @param group_child_type 组设备的子类型
     * @param group_style      组种类
     * @param obox_serial_num  组所在obox的序列号
     * @param group_addr       组地址
     */
    public Groups(List<DeviceConfig> group_member, String group_id, String group_name,
                  String group_type, String group_state, String group_child_type,
                  String group_style, String obox_serial_num, String group_addr) {
        this.group_member = group_member;
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_type = group_type;
        this.group_state = group_state;
        this.group_child_type = group_child_type;
        this.group_style = group_style;
        this.obox_serial_num = obox_serial_num;
        this.group_addr = group_addr;
        children = new ArrayList<>();
    }

    public Groups() {

    }

    /**
     * 添加单节点到组内
     */
    public void addChildrenItem(DeviceConfig child) {
        children.add(child);
    }

    /**
     * 获取组内节点成员的总数
     */
    public int getChildrenCount() {
        return children.size();
    }

    /**
     * 获取组内节点容器某个position的单节点对象
     *
     * @param index position位置
     */
    public DeviceConfig getChildItem(int index) {
        return children.get(index);
    }

    /**
     * 获取组id
     */
    public String getGroup_id() {
        return group_id;
    }

    /**
     * 设置组id
     */
    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    /**
     * 获取组名称
     */
    public String getGroup_name() {
        return group_name;
    }

    /**
     * 设置组名称
     */
    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }


    /**
     * 获取组设备的类型
     * {@link com.onbright.oblink.local.net.OBConstant.NodeType}
     */
    public String getGroup_type() {
        return group_type;
    }

    /**
     * 设置组设备的类型
     * {@link com.onbright.oblink.local.net.OBConstant.NodeType}
     */
    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    /**
     * 获取组状态
     */
    public String getGroup_state() {
        return group_state;
    }

    /**
     * 设置组状态，7字节状态转化后的十六进制字符串
     */
    public void setGroup_state(String group_state) {
        this.group_state = group_state;
    }

    /**
     * 获取组设备的子类型
     */
    public String getGroup_child_type() {
        return group_child_type;
    }

    /**
     * 设置组设备的子类型{@link com.onbright.oblink.local.net.OBConstant.NodeType}
     */
    public void setGroup_child_type(String group_child_type) {
        this.group_child_type = group_child_type;
    }

    /**
     * 获取组内节点容器
     */
    public List<DeviceConfig> getGroup_member() {
        return group_member;
    }

    /**
     * 设置组内节点容器
     */
    public void setGroup_member(List<DeviceConfig> group_member) {
        this.group_member = group_member;
    }

    /**
     * 获取组存在的方式即仅存在服务器还是下发到了具体的obox
     * 00为下发到了obox  01为仅存在服务器
     */
    public String getGroup_style() {
        return group_style;
    }

    /**
     * 设置组存在的方式即仅存在服务器还是下发到了具体的obox
     * 00为下发到了obox  01为仅存在服务器
     */
    public void setGroup_style(String group_style) {
        this.group_style = group_style;
    }

    /**
     * 获取组所在obox的序列号，如果不是本地组则序列号不存在
     */
    public String getObox_serial_num() {
        return obox_serial_num;
    }

    /**
     * 设置组所在obox的序列号，如果不是本地组则序列号不存在
     */
    public void setObox_serial_num(String obox_serial_num) {
        this.obox_serial_num = obox_serial_num;
    }

    /**
     * 获取组的组地址
     */
    public String getGroup_addr() {
        return group_addr;
    }

    /**
     * 设置组的组地址
     */
    public void setGroup_addr(String group_addr) {
        this.group_addr = group_addr;
    }
}
