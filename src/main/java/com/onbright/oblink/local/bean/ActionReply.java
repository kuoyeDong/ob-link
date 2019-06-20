package com.onbright.oblink.local.bean;

/**
 * 设置一次情景的action之后的结果
 */
public class ActionReply {
    private int index;
    private boolean isFinish;
    public ActionReply(int index, boolean isFinish) {
        this.index = index;
        this.isFinish = isFinish;
    }


    public int getIndex() {
        return index;
    }

    public boolean isFinish() {
        return isFinish;
    }
}
