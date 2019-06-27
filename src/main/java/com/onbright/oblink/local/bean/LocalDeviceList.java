package com.onbright.oblink.local.bean;

/**
 * Created by shifan_xiao on 2017/3/30.
 */

public class LocalDeviceList implements Comparable<LocalDeviceList>{
    private int deviceType;
    private int childType;

    public LocalDeviceList() {
    }

    public LocalDeviceList(int deviceType, int childType) {
        super();
        this.deviceType = deviceType;
        this.childType = childType;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getChildType() {
        return childType;
    }

    public void setChildType(int childType) {
        this.childType = childType;
    }

    @Override
    public int compareTo(LocalDeviceList o) {
        return String.valueOf(this.getDeviceType()).compareTo(String.valueOf(o.getDeviceType()));
    }

    @Override
    public String toString() {
        return "Cat [name=" + deviceType + ", order=" + childType + "]";
    }
}
