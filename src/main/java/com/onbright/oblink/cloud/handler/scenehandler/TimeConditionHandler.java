package com.onbright.oblink.cloud.handler.scenehandler;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.bean.BeCondition;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.local.net.Transformation;

/**
 * 场景时间条件对象处理
 *
 * @author dky
 * 2019/8/29
 */
public class TimeConditionHandler implements BeCondition {


    /**
     * 取得星期循环的条件对象,为了直观表达，以下形参定义附带阿拉伯数字
     *
     * @param everyDay 每天循环
     * @param week7    周日循环
     * @param week1    周一循环
     * @param week2    周二循环
     * @param week3    周三循环
     * @param week4    周四循环
     * @param week5    周五循环
     * @param week6    周六循环
     * @param hour     时 0-23
     * @param minute   分 0-59
     * @return 条件对象
     */
    public Condition circleTimeToCondition(boolean everyDay, boolean week7, boolean week1, boolean week2,
                                           boolean week3, boolean week4, boolean week5, boolean week6, int hour, int minute) {
        byte[] conditonbyte = new byte[8];
        if (everyDay) {
            conditonbyte[0] = MathUtil.setBitIndex(conditonbyte[0], 7, true);
        } else {
            boolean[] ints = new boolean[]{week7, week1, week2, week3, week4, week5, week6};
            for (int i = 0; i < ints.length; i++) {
                if (ints[i]) {
                    conditonbyte[0] = MathUtil.setBitIndex(conditonbyte[0], i, ints[i]);
                }
            }
        }
        conditonbyte[1] = 8;/*标准时区，默认东八区*/
        conditonbyte[5] = (byte) hour;
        conditonbyte[6] = (byte) minute;
        return toCondition(Transformation.byteArryToHexString(conditonbyte));
    }

    /**
     * 取得时间条件对象
     *
     * @param year   年，通用所有世纪，0-99
     * @param month  月，1-12
     * @param day    日，1-31
     * @param hour   时，0-23
     * @param minute 分，0-59
     * @return 条件对象
     */
    public Condition dateTimeToCondition(int year, int month, int day, int hour, int minute) {
        byte[] timebyte = new byte[8];
        timebyte[1] = 8;/*标准时区，默认东八区*/
        String yStr = String.valueOf(year);
        year = Integer.parseInt(yStr.substring(2));
        timebyte[2] = (byte) year;
        timebyte[3] = (byte) month;
        timebyte[4] = (byte) day;
        timebyte[5] = (byte) hour;
        timebyte[6] = (byte) minute;
        return toCondition(Transformation.byteArryToHexString(timebyte));
    }

    @Override
    public Condition toCondition(String conditionProperty) {
        Condition condition = new Condition();
        condition.setCondition(conditionProperty);
        condition.setCondition_type("00");
        return condition;
    }


}
