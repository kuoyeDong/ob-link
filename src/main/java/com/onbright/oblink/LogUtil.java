package com.onbright.oblink;

import android.util.Log;

import okhttp3.FormBody;

/**
 * use by:log工具
 * create by dky at 2019/7/25
 */
public class LogUtil {
    public static final boolean DEBUG = true;

    public static void log(Object object, Object log) {
        if (DEBUG) {
            if (log instanceof FormBody) {
                FormBody formBody = (FormBody) log;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < formBody.size(); i++) {
                    sb.append(formBody.name(i)).append(" : ").append(formBody.value(i)).append(" , ");
                }
                Log.d(object.getClass().getName(), sb.toString());
            } else if (log instanceof String) {
                String str = (String) log;
                Log.d(object.getClass().getName(), str);
            }

        }
    }
}
