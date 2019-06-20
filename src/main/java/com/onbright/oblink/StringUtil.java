package com.onbright.oblink;


import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具
 * <p>
 * Created by adolf_dong on 2016/6/16.
 */
public class StringUtil {
    /**
     * @param str  判断字符
     * @param type 判断数据类型 0中英 1 phone  2 psw
     * @param len  字符串转成utf-8字节数组的长度
     * @return 合法
     */
    public static boolean isLegit(String str, int type, int len) {
//        String limitEx="^[a-zA-Z0-9\u4E00-\u9FA5]{0,5}|[a-zA-Z0-9]{0,16}$";
        // FIXME: 2016/6/12
        String limitEx;
        final int phone = 1;
        final int psw = 2;
        final int obox_psw = 3;
        switch (type) {
            case phone:
                limitEx = "[0-9]{11}";
                break;
            case psw:
                limitEx = "[A-z0-9]{4}";
                break;
            case obox_psw:
                limitEx = "[A-z0-9]{8}";
                break;
            default:
                limitEx = "[A-z0-9\u4e00-\u9fa5]{1,16}";
                break;
        }

        Pattern pattern = Pattern.compile(limitEx);
        Matcher m = pattern.matcher(str);
        boolean lenVial = false;
        try {
            lenVial = str.getBytes(OBConstant.StringKey.UTF8).length <= len;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return m.matches() && lenVial;
    }

    /**
     * 获取utf-8编码String
     *
     * @param src 字节数组
     */
    public static String getUtf8(byte[] src) {
        String goal = null;
        try {
            goal = new String(src, OBConstant.StringKey.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return goal;
    }
}
