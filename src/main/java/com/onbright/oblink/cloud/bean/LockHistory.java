package com.onbright.oblink.cloud.bean;

import java.io.Serializable;
import java.util.List;

/**
 * use by:门锁开门历史记录
 * create by dky at 2019/7/18
 */
public class LockHistory implements Serializable {
    /**
     *时间戳
     */
    private String dateLine;
    /**
     * OB门锁开门详情列表
     */
    private List<LockHistoryBean> list;

    public String getDateLine() {
        return dateLine;
    }

    public void setDateLine(String dateLine) {
        this.dateLine = dateLine;
    }

    public List<LockHistoryBean> getList() {
        return list;
    }

    public void setList(List<LockHistoryBean> list) {
        this.list = list;
    }
}
