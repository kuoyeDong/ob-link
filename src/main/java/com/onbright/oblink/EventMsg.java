package com.onbright.oblink;


import java.util.HashMap;
import java.util.Map;

/**
 * use by:eventbus msg
 * create by dky at 2019/7/15
 */
public class EventMsg {
    /**
     * 消息tag
     */
    private String action;
    /**
     * 传递实例
     */
    public Map<String, Object> bundles;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 传入参数
     *
     * @param key   键
     * @param value 值
     */
    public void putExtra(String key, Object value) {
        if (bundles == null) {
            bundles = new HashMap<>();
        }
        bundles.put(key, value);
    }

    /**
     * 取出参数
     *
     * @param key 键
     * @return 值
     */
    public Object getExtra(String key) {
        if (bundles == null) {
            bundles = new HashMap<>();
        }
        return bundles.get(key);
    }
}
