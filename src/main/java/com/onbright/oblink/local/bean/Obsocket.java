package com.onbright.oblink.local.bean;

import java.util.HashMap;
import java.util.Map;

/**插座、开关
 * Created by adolf_dong on 2016/6/23.
 */
public class Obsocket extends ObNode implements SceneCondition{
    public static final int ISOPEN = 0;
    private Map<String, byte[]> conditionMap = new HashMap<>();

    public Obsocket(byte num, byte[] rfAddr, byte addr, byte[] id, byte[] serNum, byte parentType, byte type, byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        super(num, rfAddr, addr, id, serNum, parentType, type, version, surplusSence, gourpAddr, state);
    }

    @Override
    public int getconditionType() {
        return SceneCondition.SENSOR;
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

    @Override
    public byte[] getConditionaddr() {
        return getCplAddr();
    }
}
