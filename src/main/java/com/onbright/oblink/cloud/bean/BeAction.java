package com.onbright.oblink.cloud.bean;

/**
 * 可当成场景行为的节点接口
 *
 * @author dky
 * 2019/8/26
 */
public interface BeAction {
    /**
     * @param actionProperty 行为参数
     * @return 行为对象
     * @throws Exception 找不到设备不能生成可用场景行为对象,此时请退出重新初始化Sdk，或确认该序列号设备在系统中
     */
    Action toAction(String actionProperty) throws Exception;

}
