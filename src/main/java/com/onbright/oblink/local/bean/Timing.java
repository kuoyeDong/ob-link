package com.onbright.oblink.local.bean;



import com.onbright.oblink.MathUtil;

import java.util.Arrays;

/**
 * 表示情景的定时属性
 */
public class Timing implements SceneCondition {
    /*循环，时区，等等*/
    public final  static int CIRCLE = 0;
    public final  static int TIMEZON = 1;
    public final  static int YEAR = 2;
    public final  static int MONTH = 3;
    public final  static int DAY = 4;
    public final  static int HOUR = 5;
    public final  static int MIN = 6;


    public final static int ISALL = 7;
    public final static int IS1 = 1;
    public final static int IS2 = 2;
    public final  static int IS3 = 3;
    public final  static int IS4 = 4;
    public final  static int IS5 = 5;
    public final  static int IS6 = 6;
    public final  static int IS7 = 0;


    private byte[] time;

    public Timing(byte[] time) {
        this.time = time;
    }

    @Deprecated
    public byte[] getTime() {
        return time;
    }

    @Deprecated
    public void setTime(byte[] time) {
        this.time = time;
    }

    public int getStateforIndex(int index) {
        return MathUtil.validByte(time[index]);
    }

    /**
     * 返回星期的某一天是否激活
     *
     */
    public int getcircleStateforIndex(int index) {
        return MathUtil.byteIndexValid(time[CIRCLE], index);
    }


    /**
     * 设置条件的星期几参数
     * 表示是否希望开启当天的响应，注意bit7-bit0依次对应每天，6543217
     *
     * @param tag boolean数组，传入顺序周日，1-6，每天，
     */
    public void setCircle(boolean[] tag) {
        int rep = 0x80;
        if (tag[7]) {
            time[CIRCLE] = (byte) rep;
            return;
        }
        for (int i = 0; i < tag.length; i++) {
            rep += (tag[i] ? 1 : 0) << i;
        }
        time[CIRCLE] = (byte) rep;
    }

    @Override
    public int getconditionType() {
        return SceneCondition.TIMING;
    }


    @Override
    public byte[] getCondition(String key) {
        return time;
    }

    @Override
    public void setCondition(String key, byte[] condition) {
        this.time = condition;
    }


    @Override
    public byte[] getConditionaddr() {
        return new byte[7];
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Timing) {
            Timing timing = (Timing) o;
            return Arrays.equals(timing.getCondition("0"), getCondition("0"));
        }
        return false;
    }

}
