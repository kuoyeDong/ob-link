package com.onbright.oblink.cloud.bean.infraredtransponderbean;

import java.io.Serializable;

/**
 * 设备品牌,参数来源规则按遥控云的标准定义
 *
 * @author dky
 * 2019/8/12
 */
public class Brand implements Serializable {
    /**
     * 品牌ID
     */
    private int bid;
    /**
     * 常用品牌标识
     */
    private int common;
    /**
     * 品牌名字
     */
    private String name;

    public Brand(int bid, int common, String name) {
        this.bid = bid;
        this.common = common;
        this.name = name;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getCommon() {
        return common;
    }

    public void setCommon(int common) {
        this.common = common;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
