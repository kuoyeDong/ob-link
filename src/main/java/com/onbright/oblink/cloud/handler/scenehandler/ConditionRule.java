package com.onbright.oblink.cloud.handler.scenehandler;

/**
 * 场景条件规则
 *
 * @author dky
 * 2019/8/28
 */
public enum ConditionRule {
    /**
     * 大于
     */
    GREATER_THAN(0x49),
    /**
     * 小于
     */
    LESS_THAN(0x4c),
    /**
     * 等于
     */
    EQUAL_TO(0x4a),
    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL_TO(0x4b),
    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL_TO(0x4e);
    private final int val;

    ConditionRule(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}