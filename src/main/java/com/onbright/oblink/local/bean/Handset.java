package com.onbright.oblink.local.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 遥控器，对于app，遥控器的可视方面只有情景部分
 * Created by adolf_dong on 2016/7/1.
 */
public class Handset implements SceneCondition {
    private Map<String, byte[]> conditionMap = new HashMap<>();

    @Override
    public int getconditionType() {
        return SceneCondition.CONTROL;
    }

    @Override
    public byte[] getConditionaddr() {
        return new byte[7];
    }

    @Override
    public byte[] getCondition(String key) {
        return conditionMap.get(key);
    }

    @Override
    public void setCondition(String key, byte[] condition) {
        if (conditionMap == null) {
            conditionMap = new HashMap<>();
    }
        conditionMap.put(key, condition);
    }

    public void setBindOboxs(List<String> bindOboxs) {
        this.bindOboxs = bindOboxs;
    }

    private List<String> bindOboxs = new ArrayList<>();
    public List<String> getBindObox() {
        return bindOboxs;
    }
}
