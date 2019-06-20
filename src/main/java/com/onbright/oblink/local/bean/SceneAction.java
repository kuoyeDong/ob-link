package com.onbright.oblink.local.bean;

import android.widget.Checkable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 本地模式情景行为节点
 * Created by adolf_dong on 2016/7/1.
 */
public abstract class SceneAction implements Checkable,Serializable {
    /**
     * 用于比较数据变化
     */
    public static transient  int COMPARESER = 0;
    /**
     * 如果是情景内节点的时候此参数将被实例化
     * 代表其在某个情景内的响应状态
     * 键以当前情景序列号
     */
    private Map<String, byte[]> actions;

    /**
     * @param SceneSerNum 情景序列号
     */
    public byte[] getActions(int SceneSerNum) {
        if (actions == null) {
            actions = new HashMap<>();
        }
        String key = SceneSerNum + "";
        return actions.get(key);
    }

    private Map<String, Boolean> inSceneMap;

    public void putAction(int SceneSerNum, byte[] action) {
        if (actions == null) {
            actions = new HashMap<>();
        }
        String key = SceneSerNum + "";
        actions.put(key, action);
    }

    public abstract byte[] getAddrs();

    private boolean isChecked;

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        isChecked = !isChecked;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SceneAction) {
            SceneAction sceneAction = (SceneAction) o;
            return Arrays.equals(getAddrs(), sceneAction.getAddrs()) &&
                    Arrays.equals(getActions(COMPARESER), sceneAction.getActions(COMPARESER));
        }
        return false;
    }

    public boolean isInScene(int serisNum) {
        if (inSceneMap == null) {
            inSceneMap = new HashMap<>();
        }
        String key = serisNum + "";
        return inSceneMap.get(key) == null ? true : inSceneMap.get(key);
    }

    @SuppressWarnings("unused")
    public void putInScene(int serisNum, boolean isInScene) {
        if (inSceneMap == null) {
            inSceneMap = new HashMap<>();
        }
        String key = serisNum + "";
        inSceneMap.put(key, isInScene);
    }
}
