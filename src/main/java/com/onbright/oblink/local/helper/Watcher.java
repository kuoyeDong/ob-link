package com.onbright.oblink.local.helper;

import android.os.Handler;
import android.os.Message;

import com.onbright.oblink.local.net.Respond;

import java.util.LinkedList;
import java.util.List;

/**
 * 注册监听，移除监听的类
 *
 * 要注册监听，请使用{@link #registWatcher(Respond)}
 *
 * 要移除监听，请使用{@link #unRegistWatcher(Respond)}
 *
 */

public class Watcher {

    private static Watcher mWatcher;

    public static Watcher getInstance() {
        if (mWatcher==null) {
            mWatcher = new Watcher();
        }
        return mWatcher;
    }
    private List<Respond> responds = new LinkedList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (Respond respond : responds) {
                respond.onReceive(msg);
            }
        }
    };

    /**
     * 注册该对象使之可以接收网络回执之后的回调
     *
     * @param respond 可接收网络回执的对象
     */
    public void registWatcher(Respond respond) {
        if (!responds.contains(respond)) {
            responds.add(respond);
        }
    }

    /**
     * 注销该对象使之不再接收网络回执之后的回调
     *
     * @param respond 可接收网络回执的对象
     */
    public void unRegistWatcher(Respond respond) {
        if (responds.contains(respond)) {
            responds.remove(respond);
        }
    }

    public Handler getHandler() {
        return handler;
    }
}
