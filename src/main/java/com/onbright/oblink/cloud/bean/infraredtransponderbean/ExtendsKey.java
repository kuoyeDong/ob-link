package com.onbright.oblink.cloud.bean.infraredtransponderbean;

import java.io.Serializable;

/**
 * 自定义按键
 *
 * @author dky
 * 2019/8/12
 */
public class ExtendsKey implements Serializable {
    /**
     * 拓展按键的名称用于控制、删除、重命名前端数据截取"|"前用户输入的功能名用于显示
     * 1. 根据唯一的extendsKey进行列表显示
     * 2. 键名生成规则为：用户输入的功能键名 + "|” + 时间戳From UTC 2001/1/1 00:00:00（秒),生成规则由前端控制，以保证数据唯一
     */
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
