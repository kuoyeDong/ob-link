package com.onbright.oblink.cloud.bean.infraredtransponderbean;

import java.io.Serializable;

/**
 * 页面标准按键描述
 *
 * @author dky
 * 2019/8/12
 */
public class StandardKey implements Serializable {
    /**
     * 按键的唯一描述用于控制及更改发射码参照遥控云
     * 根据不同设备有不同规则
     * 在开发过程中总结出json模板供统一使用
     */
    private String key;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
