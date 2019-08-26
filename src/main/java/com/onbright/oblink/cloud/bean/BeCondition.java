package com.onbright.oblink.cloud.bean;

/**
 * 可当成场景条件的节点接口
 *
 * @author dky
 * 2019/8/26
 */
public interface BeCondition {
    /**
     * 转化为条件
     *
     * @param parameters 条件参数
     * @return 条件对象
     */
    Condition toCondition(Object... parameters);

}
