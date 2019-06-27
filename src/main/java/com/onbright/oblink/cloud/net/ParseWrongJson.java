package com.onbright.oblink.cloud.net;

import android.util.Log;

/**
 * 解析非正常的json数据获取红外码库
 * Created by dky on 14:39.
 */

public class ParseWrongJson {
    private static final String TAG = "ParseWrongJson";
    private String oriJsonStr = "{\"beRmodel\":\"3(V3)\",\"codeset\":0,\"name\":\"格力\",\"one\":\"23,64\",\"rcCommand\":\"{\\\"a_s0__u0_l0_p0\\\":{\\\"src\\\":\\\"012617161740ffff04000154AD1090040Affff0A171617401716170002FD48040007ffff0118\\\",\\\"short\\\":\\\"\\\"},\\\"a_s0__u0_l1_p0\\\":{\\\"src\\\":\\\"012617161740ffff04000154AD1290040Affff0A171617401716170002FD4B040005ffff0118\\\",\\\"short\\\":\\\"\\\"},\\\"a_s0__u1_l0_p0\\\":{\\\"src\\\":\\\"012617161740ffff04000154AD1290040Affff0A171617401716170002FD88040007ffff0118\\\",\\\"short\\\":\\\"\\\"},\\\"a_s0__u1_l1_p0\\\":{\\\"src\\\":\\\"012617161740ffff04000154AD1290040Affff0A171617401716170002FC8B040005ffff0118\\\",\\\"short\\\":\\\"\\\"},\\\"a_s1__u0_l0_p0\\\":{\\\"src\\\":\\\"012617161740ffff04000154AD1890040Affff0A171617401716170002FD48040007ffff0118\\\",\\\"short\\\":\\\"\\\"}}";
    private char ori2Short = '\\';

    public ParseWrongJson(String oriJsonStr) {
        this.oriJsonStr = oriJsonStr;
    }

    public ParseWrongJson() {
    }

    /**去除转义字符的数据
     */
    public String getStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < oriJsonStr.length(); i++) {
            char c = oriJsonStr.charAt(i);
            if (c != ori2Short) {
                String str = String.valueOf(c);
                sb.append(str);
            }
        }
        String str = sb.toString();
        Log.d(TAG, "getStr: =" + sb.toString());
        return str;
    }
}
