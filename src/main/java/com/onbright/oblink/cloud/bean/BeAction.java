package com.onbright.oblink.cloud.bean;

/**
 * 可当成场景行为的节点接口
 *
 * @author dky
 * 2019/8/26
 */
public interface BeAction {
    /**
     * @param parameters 行为参数
     * @return 行为对象
     */
    Action toAction(Object... parameters);

}
