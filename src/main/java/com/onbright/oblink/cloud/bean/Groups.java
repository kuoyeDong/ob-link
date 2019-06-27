package com.onbright.oblink.cloud.bean;

import java.util.ArrayList;
import java.util.List;

/**服务器组节点元素
 * Created by shifan_xiao on 2016/10/20.
 * 为什么要继承DeviceConfig
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

    public Groups(List<DeviceConfig> group_member, String group_id, String group_name,
                  String group_type, String group_state, String group_child_type, String group_style, String obox_serial_num, String group_addr) {
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

    public void addChildrenItem(DeviceConfig child) {
        children.add(child);
    }

    public int getChildrenCount() {
        return children.size();
    }

    public DeviceConfig getChildItem(int index) {
        return children.get(index);
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public String getGroup_state() {
        return group_state;
    }

    public void setGroup_state(String group_state) {
        this.group_state = group_state;
    }

    public String getGroup_child_type() {
        return group_child_type;
    }

    public void setGroup_child_type(String group_child_type) {
        this.group_child_type = group_child_type;
    }

    public List<DeviceConfig> getGroup_member() {
        return group_member;
    }

    public void setGroup_member(List<DeviceConfig> group_member) {
        this.group_member = group_member;
    }

    public String getGroup_style() {
        return group_style;
    }

    public void setGroup_style(String group_style) {
        this.group_style = group_style;
    }

    public String getObox_serial_num() {
        return obox_serial_num;
    }

    public void setObox_serial_num(String obox_serial_num) {
        this.obox_serial_num = obox_serial_num;
    }

    public String getGroup_addr() {
        return group_addr;
    }

    public void setGroup_addr(String group_addr) {
        this.group_addr = group_addr;
    }
}
