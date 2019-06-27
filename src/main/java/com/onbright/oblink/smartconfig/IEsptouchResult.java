package com.onbright.oblink.smartconfig;

import java.net.InetAddress;

/**isp返回接口
 * Created by Adolf_Dong on 2018/3/14.
 */

public interface IEsptouchResult {
    /**
     * check whether the esptouch task is executed suc
     *
     * @return whether the esptouch task is executed suc
     */
    boolean isSuc();

    /**
     * get the device's bssid
     *
     * @return the device's bssid
     */
    String getBssid();

    /**
     * check whether the esptouch task is cancelled by user
     *
     * @return whether the esptouch task is cancelled by user
     */
    boolean isCancelled();

    /**
     * get the ip address of the device
     *
     * @return the ip device of the device
     */
    InetAddress getInetAddress();
}
