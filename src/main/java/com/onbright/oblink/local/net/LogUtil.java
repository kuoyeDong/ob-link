package com.onbright.oblink.local.net;

import android.util.Log;

/**打印
 * Created by adolf_dong on 2017/1/4.
 */

public class LogUtil {
    private static boolean isLog = true;

    public static void log(String tag, String msg) {
        if (isLog) {
            Log.d(tag, "log: " + msg);
        }
    }
}
