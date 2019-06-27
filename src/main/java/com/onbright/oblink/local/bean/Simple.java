package com.onbright.oblink.local.bean;

/**情景的一般条件
 * Created by adolf_dong on 2016/7/1.
 */
@Deprecated
public class Simple implements SceneCondition {
    @Override
    public int getconditionType() {
        return SceneCondition.SIMPLE;
    }

    @Override
    public byte[] getCondition(String key) {
        return new byte[8];
    }

    @Override
    public void setCondition(String key, byte[] condition) {

    }

    @Override
    public byte[] getConditionaddr() {
        return new byte[7];
    }
}
