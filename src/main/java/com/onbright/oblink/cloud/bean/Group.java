package com.onbright.oblink.cloud.bean;

import java.util.List;

/**服务器组节点元素
 * Created by adolf on 2019/6/28.
 *
 */
public class Group  {
    private String group_id;
    private String group_name;
    private String group_type;
    private String group_state;
    private String group_child_type;
    private String obox_serial_id;
    private String groupAddr;
    private List<Device> group_member;
    private String group_style;

    public Group(List<Device> group_member, String group_id, String group_name,
                 String group_type, String group_state, String group_child_type, String group_style, String obox_serial_id, String groupAddr) {
        this.group_member = group_member;
        this.group_id = group_id;
        this.group_name = group_name;
        this.group_type = group_type;
        this.group_state = group_state;
        this.group_child_type = group_child_type;
        this.group_style = group_style;
        this.obox_serial_id = obox_serial_id;
        this.groupAddr = groupAddr;
    }
    public Group() {

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
        this.   group_type = group_type;
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

    public List<Device> getGroup_member() {
        return group_member;
    }

    public void setGroup_member(List<Device> group_member) {
        this.group_member = group_member;
    }

    public String getGroup_style() {
        return group_style;
    }

    public void setGroup_style(String group_style) {
        this.group_style = group_style;
    }

    public String getObox_serial_id() {
        return obox_serial_id;
    }

    public void setObox_serial_id(String obox_serial_id) {
        this.obox_serial_id = obox_serial_id;
    }

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }
}
