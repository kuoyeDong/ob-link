package com.onbright.oblink.local.helper;

import com.onbright.oblink.local.net.TcpSend;

/**
 * 设置obox的工作模式，注意此类不会收到回调，独立工作模式的时候灯闪烁，station模式灯长亮
 * <p>
 * 要把obox加入到目标路由中请连接obox的wifi热点，确保使用过获取配置类{@link InitConfigHelper#startGetConfig()}成功获取过配置，
 * 再使用{@link #toStationMode(boolean, String, String, byte[], String)},十五秒后观察obox指示灯，如长亮则成功，闪烁请重试。
 * <p>
 * 要把obox从路由器网络中踢出，请连接目标路由器wifi，同样确保使用过获取配置类{@link InitConfigHelper#startGetConfig()}成功获取过配置，
 * 再调用{@link #toApMode()},十秒后观察obox指示灯，如闪烁则成功，仍然长亮请重试。
 */

public class WorkModeHelper {
    private TcpSend mTcpSend;

    public WorkModeHelper(TcpSend mTcpSend) {
        this.mTcpSend = mTcpSend;
    }

    /**
     * 设置到ap模式，即把目标obox设置为独立工作模式
     */
    public void toApMode() {
        mTcpSend.setWifiConfig(2);
    }

    /**
     * 把obox加入到目标路由器网段中，即设置obox的工作模式为以路由器为中心的工作模式
     *
     * @param linkServer   是否要连接到服务器，true则会连接到服务器，false则只添加到目标路由器网段
     * @param routSSID     WIFI名称
     * @param routPsw      WIFI密码
     * @param serverIpbyte 服务器ip， 可传null则使用昂宝服务器
     * @param code         验证码 ，可不传
     */
    public void toStationMode(boolean linkServer, String routSSID, String routPsw, byte[] serverIpbyte, String code) {
        mTcpSend.setOboxToStation(routSSID.getBytes(), routPsw.length() == 0 ? "123".getBytes() :
                        routPsw.getBytes(), linkServer ? (serverIpbyte == null ? new byte[]{(byte) 210, 21, 98, 82} : serverIpbyte) : new byte[4],
                code == null ? "".getBytes() : code.getBytes());
    }
}
