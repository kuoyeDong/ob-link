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
     * @param conditionProperty 条件参数
     * @return 条件对象
     * @throws Exception 找不到设备不能生成可用场景条件对象,此时请退出重新初始化Sdk，或确认该序列号设备在系统中
     */
    Condition toCondition(String conditionProperty) throws Exception;

}
