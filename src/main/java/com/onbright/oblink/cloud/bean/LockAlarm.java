package com.onbright.oblink.cloud.bean;

import java.io.Serializable;
import java.util.List;

/**
 * use by:门锁报警数据
 * create by dky at 2019/7/18
 */
public class LockAlarm implements Serializable {
    /**
     *时间戳
     */
    private String dateLine;
    /**
     * OB门锁报警详情列表
     */
    private List<LockAlarmBean> list;



    public String getDateLine() {
        return dateLine;
    }

    public void setDateLine(String dateLine) {
        this.dateLine = dateLine;
    }


    public List<LockAlarmBean> getList() {
        return list;
    }

    public void setList(List<LockAlarmBean> list) {
        this.list = list;
    }
}
