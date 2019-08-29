package com.onbright.oblink.cloud.handler.scenehandler;

import com.onbright.oblink.cloud.bean.BeCondition;
import com.onbright.oblink.cloud.bean.Condition;

/**
 * @author dky
 * 2019/8/29
 */
public class TimeCondition implements BeCondition {

    public Condition simpleTimeToCondition() {

    }

    public Condition circleTimeToCondition(boolean everyDay, boolean week6, boolean week5, boolean week4, boolean week3, boolean week2, boolean week1, boolean week7) {
        每日 6 - 1 日 拆解到每个handler处理解析情景数据
    }

    @Override
    public Condition toCondition(String conditionProperty) throws Exception {
        return null;
    }

}
