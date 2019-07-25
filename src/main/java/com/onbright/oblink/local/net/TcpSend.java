package com.onbright.oblink.local.net;

import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

import com.onbright.oblink.LogUtil;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.bean.SceneAction;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.smartconfig.ConnectDeviceHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

/**
 * 发送类,执行网络交互请求
 * Created by adolf_dong on 2016/5/13.
 */
public class TcpSend extends Thread {

    static {
        System.loadLibrary("tcpsend");
    }

    private static final String TAG = "TcpSend";
    private Sbox msBox;
    private byte[] psw = {0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38, 0x38};

    private String mIp = "192.168.2.3";

    private Socket mSocket;

    private Handler mHandler;

    private TcpReceive mRecive;

    /**
     * 加密方式
     */
    private int encryptionType;
    private String oboxName;
    private String PSW;
    private byte[] rfAddr;

    /**
     * 设置上报解析器的obox序列号
     *
     * @param oboxSer obox序列号
     */
    public void setPaseUpLocad(String oboxSer) {
        mRecive.setPaseUpLocad(oboxSer);
    }

    /**
     * 设置当前连接的名称，ps获取后设置
     */
    public void setOboxName(String oboxName) {
        this.oboxName = oboxName;
    }

    public String getOboxName() {
        return oboxName;
    }

    public void setEncryptionType(int encryptionType) {
        this.encryptionType = encryptionType;
    }

    public void setPSW(String psw) {
        this.PSW = psw;
        mRecive.setPswbytes(psw.getBytes());
        try {
            this.psw = psw.getBytes(OBConstant.StringKey.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收到2500上报后，回0500应答
     */
    public void sendAck() {
        byte[] cmd = new byte[62];
        cmd[4] = (byte) 0x05;
        cmd[61] = 0x55;
        LogUtil.log(this, "收到2500上报后，回0500应答");
        sendACKCMD(cmd);
    }

    private void sendACKCMD(final byte[] cmd) {
        System.arraycopy(cmd, 0, lastBytes, 0, cmd.length);
        final OutputStream out;
        try {
            out = mSocket.getOutputStream();
            final byte[] goal = msBox.pack(cmd, psw, encryptionType, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        out.write(goal);
                        Log.d(TAG, "local send cmd:= >> " + Transformation.byteArryToHexString(cmd));
                        Log.d(TAG, "local send goal:= >> " + Transformation.byteArryToHexString(goal));
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (mRecive != null) {
                            mRecive.disConnect();
                        }
                        reConnect();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            if (mRecive != null) {
                mRecive.disConnect();
            }
            reConnect();
        }
    }

    /**
     * 读取版本信息
     */
    public void getVersion(byte[] addr) {
        byte[] cmd = new byte[62];
        cmd[5] = (byte) 0x0c;
        cmd[8] = (byte) 0x01;
        System.arraycopy(addr, 0, cmd, index[10], addr.length);
        cmd[61] = 0x55;
        LogUtil.log(this, "读取版本信息");
        sendCMD(cmd);
    }

    public static int[] index = new int[65];

    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }

    /**
     * @return String密码
     */
    public String getPSW() {
        return PSW;
    }

    public byte[] getPsw() {
        return psw;
    }

    public TcpSend(String ip, Handler handler) {
        mSocket = new Socket();
        mIp = ip;
        mHandler = handler;
        msBox = new Sbox();
    }

    public TcpSend(Handler handler) {
        mSocket = new Socket();
        mHandler = handler;
        msBox = new Sbox();
    }

    public TcpSend() {
        mSocket = new Socket();
        msBox = new Sbox();
    }

    /**
     * 作为tcpserver调用此方法
     *
     * @param socket  socket,由tcpserver接受获得
     * @param handler handler
     */
    public TcpSend(Socket socket, Handler handler, ConnectDeviceHandler smartConfigAddOboxHandler) {
        this.mSocket = socket;
        mHandler = handler;
        msBox = new Sbox();
        if (mRecive == null) {
            mRecive = new TcpReceive(mSocket, mHandler, msBox, this, smartConfigAddOboxHandler);
            mRecive.start();
        }
    }

    private byte[] lastBytes = new byte[62];

    public void sendBefore() {
        sendCMD(lastBytes);
    }

    private void sendCMD(final byte[] cmd) {
        System.arraycopy(cmd, 0, lastBytes, 0, cmd.length);
        final OutputStream out;
        try {
            out = mSocket.getOutputStream();
            NetLock.setSrc(cmd);
            final byte[] goal = msBox.pack(cmd, psw, encryptionType, false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        out.write(goal);
                        mRecive.setMy(true);
                        mHandler.sendEmptyMessageDelayed(OBConstant.ReplyType.NOT_REPLY, 5000);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (mRecive != null) {
                            mRecive.disConnect();
                        }
                        reConnect();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            if (mRecive != null) {
                mRecive.disConnect();
            }
            reConnect();
        }
    }

    /**
     * @param ip courrent socket's ip address
     */
    public void setIp(String ip) {
        mIp = ip;
    }

    /**
     * @return courrent socket's ip address
     */
    @SuppressWarnings("unused")
    public String getIp() {
        return mIp;
    }

    /**
     * 设置是否强制接收
     */
    public void setMustRec(boolean mustRec) {
        if (mRecive != null) {
            mRecive.setMustRec(mustRec);
        }
    }

    /**
     * connect
     *
     * @return connect successful
     */
    public boolean connect() {
        SocketAddress mSocketAddress = new InetSocketAddress(mIp, 5000);
        try {
            mSocket.connect(mSocketAddress, 20000);
            if (mRecive == null) {
                mRecive = new TcpReceive(mSocket, mHandler, msBox, this);
                mRecive.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void reConnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SocketAddress mSocketAddress = new InetSocketAddress(mIp, 5000);
                try {
                    mSocket = new Socket();
                    mSocket.connect(mSocketAddress, 20000);
                    mRecive = new TcpReceive(mSocket, mHandler, msBox, TcpSend.this);
                    mRecive.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 尝试连接obox
     *
     * @return 连接成功返回true
     */
    public boolean tryCon() {
        SocketAddress mSocketAddress = new InetSocketAddress(mIp, 5000);
        boolean isCon = false;
        try {
            mSocket.connect(mSocketAddress, 500);
            isCon = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isCon;
    }

    /**
     * disConnect current connect
     */
    public void disConnect() {
        if (mRecive != null) {
            mRecive.disConnect();
        }
        try {
            mSocket.shutdownInput();
            mSocket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private native void tsOboxSSID(byte[] array);

    /**
     * 通知OBOX返回SSID
     */
    public void reqOboxSSID() {
        byte[] cmd = new byte[62];
        tsOboxSSID(cmd);
        LogUtil.log(this, "获取ssid");
        sendCMD(cmd);
    }

    private native void tsChangeOboxRfPsw(byte[] array, byte[] oldPsw, byte[] newPsw, int oldlen, int newlen);

    /**
     * 修改rf密码
     */
    public void changeOboxRfPsw(byte[] oldPsw, byte[] newPsw) {
        byte[] cmd = new byte[62];
        tsChangeOboxRfPsw(cmd, oldPsw, newPsw, (byte) oldPsw.length, (byte) newPsw.length);
        LogUtil.log(this, "修改密码");
        sendCMD(cmd);
    }

    private native void tsOboxToStation(byte[] array, byte[] ssid, byte[] pswdata, byte[] ipByte, byte[] codebytes,
                                        int ssidLen, int pswLen, int ipLen, int codeLen);

    /**
     * 设置OBox工作方式为station模式
     *
     * @param ssid      WIFI名称
     * @param pswdata   WIFI密码
     * @param codebytes 验证码
     */
    // FIXME: 2016/7/6 待添加ip地址
    public void setOboxToStation(byte[] ssid, byte[] pswdata, byte[] ipByte, byte[] codebytes) {
        byte[] cmd = new byte[62];
        //System.arraycopy(psw, 0, cmd, 0, 4);
        tsOboxToStation(cmd, ssid, pswdata, ipByte, codebytes, ssid.length, pswdata.length, ipByte.length, codebytes.length);
        LogUtil.log(this, "setOboxToStation: 设置obox工作模式");
        sendCMD(cmd);
    }

    private native void tsGetDevice(byte[] array, int index, boolean isGroup);

    /**
     * 获取单节点或者组节点
     *
     * @param index   编号
     * @param isGroup 是否取组
     */
    public void getDevice(int index, boolean isGroup) {
        byte[] cmd = new byte[62];
        tsGetDevice(cmd, index, isGroup);
        LogUtil.log(this, "getDevice: 获取设备是否获取组" + isGroup);
        sendCMD(cmd);
    }


    private native void tsGetDeviceState(byte[] array, byte[] cplAddr, byte[] dataMark, int cplAddrLen, int dataMarkLen);

    /**
     * 获取节点状态
     *
     * @param cplAddr  节点完整地址
     * @param datamark 数据标识
     */
    public void getDeviceState(byte[] cplAddr, byte[] datamark) {
        byte[] cmd = new byte[62];
        tsGetDeviceState(cmd, cplAddr, datamark, cplAddr.length, datamark.length);
        LogUtil.log(this, "getDeviceState: 获取状态");
        getStatusIndex++;
        sendCMD(cmd);
    }

    /**
     * 获取状态的时候，灯没上电，会回复超时，此时需要记录当前获取index
     */
    private int getStatusIndex;


    public int getGetStatusIndex() {
        return getStatusIndex;
    }

    private native void tsReqOboxMsg(byte[] array);

    /**
     * 获取obox的信息
     */
    public void reqOboxMsg() {
        byte[] cmd = new byte[62];
        tsReqOboxMsg(cmd);
        LogUtil.log(this, "reqOboxMsg: 获取obox信息");
        sendCMD(cmd);
    }

    private native void tsMakeOboxCloudState(byte[] array, boolean isAdd, byte[] ipByte, int ipByteLen);

    /**
     * 设置obox的服务器连接状态
     *
     * @param isAdd  添加还是删除
     * @param ipByte 服务器地址
     */
    @SuppressWarnings("unused")
    public void makeOboxCloudState(boolean isAdd, byte[] ipByte) {
        byte[] cmd = new byte[62];
        tsMakeOboxCloudState(cmd, isAdd, ipByte, ipByte.length);
        LogUtil.log(this, "makeOboxCloudState: 设置obox连接服务器 " + isAdd);
        sendCMD(cmd);
    }


    private native void tsRfCmd(byte[] array, int mode, byte[] startId, int time, int startIdLen);

    /**
     * 开始或者停止扫描节点
     *
     * @param mode    1.开启扫描，0.关闭扫描，2.通知所有节点可以重新入网
     * @param startId 开始id位置,不大于255*255*255-1
     * @param time    扫描时间 30s-60s
     */
    public void rfCmd(int mode, byte[] startId, int time) {
        byte[] cmd = new byte[62];
        tsRfCmd(cmd, mode, startId, time, startId.length);
        LogUtil.log(this, "rfCmd: 扫描设备" + mode);
        sendCMD(cmd);
    }

    private native void tsRelease(byte[] array);

    /**
     * 释放所有设备
     */
    public void release() {
        byte[] cmd = new byte[62];
        tsRelease(cmd);
        LogUtil.log(this, "release: 释放设备");
        sendCMD(cmd);
    }

    private native void tsSetOboxTime(byte[] array, int year, int month, int montyDay, int weekDay,
                                      int hour, int minute, int second);

    /**
     * 设定obox的时间
     *
     * @param time 建议传入系统当前时间
     */
    @SuppressWarnings("deprecation")
    public void setOboxTime(Time time) {
        time.setToNow();
        byte[] cmd = new byte[62];
        tsSetOboxTime(cmd, time.year, time.month, time.monthDay, time.weekDay, time.hour, time.minute, time.second);
        LogUtil.log(this, "设置时间");
        sendCMD(cmd);
    }

    private int actionIndex = 1;

    private native void tsReqScene(byte[] array, int factorsOfScene, int serNum, int num, int actionIndex);

    /**
     * 获取obox的情景信息
     *
     * @param factorsOfScene 获取情景的因素 ，{@link ObScene#OBSCENE_ID }
     *                       {@link ObScene#OBSCENE_CONDITION},{@link ObScene#OBSCENE_ACTION}
     * @param serNum         情景序号,或者编号，取id的时候传编号，取其他信息的时候传序号
     */
    public void reqScene(int factorsOfScene, int serNum, int num) {
        byte[] cmd = new byte[62];
        tsReqScene(cmd, factorsOfScene, serNum, num, actionIndex);
        if (factorsOfScene == ObScene.OBSCENE_ACTION) {
            ++actionIndex;
        } else {
            actionIndex = 1;
        }
        LogUtil.log(this, "reqScene: 获取场景 = " + factorsOfScene);
        sendCMD(cmd);
    }

    private native void tsSetNodeState(byte[] array, byte[] cplAddr, byte[] status, boolean isGroup, int cplAddrLen, int statusLen);

    /**
     * 设置任何设备的状态，主要的状态设置在设置页面中设定
     *
     * @param obNode  obnode设备
     * @param status  状态
     * @param isGroup 是否为组
     */
    public void setNodeState(ObNode obNode, byte[] status, boolean isGroup) {
        byte[] cmd = new byte[62];
        tsSetNodeState(cmd, obNode.getCplAddr(), status, isGroup, obNode.getCplAddr().length, status.length);
        LogUtil.log(this, "setNodeState: 设置状态，是否设置组" + isGroup);
        sendCMD(cmd);
    }

    private native void tsEditNodeOrGroup(byte[] array, int opreType, int nodeAddr, int groupAddr, byte[] id, boolean isGroup,
                                          int idLen, byte[] rfAddr, int rfAddrLen);

    /**
     * 新增（只对组）、删除、重命名设置节点或者组名称
     *
     * @param opreType 0 删除  1新增  2重命名
     * @param obNode   单节点操作传，否则为null
     * @param obGroup  组操作传，否则为null
     * @param id       重命名或者新建组传入，否则null
     * @param isGroup  是否为组操作
     */
    public void editNodeorGroup(int opreType, ObNode obNode, ObGroup obGroup, byte[] id, boolean isGroup) {
        byte[] cmd = new byte[62];
        tsEditNodeOrGroup(cmd, opreType, obNode == null ? 0 : obNode.getAddr(), obGroup == null ? 0 : obGroup.getAddr(), id == null ? new byte[8] : id, isGroup, id == null ? 8 : id.length, rfAddr, rfAddr.length);
        LogUtil.log(this, "editNodeorGroup: 设置节点或者组信息");
        sendCMD(cmd);
    }

    private native void tsOrganizGroup(byte[] array, byte groupAddr, byte nodeAddr, boolean isAdd, byte[] rfAddr, int rfAddrLen);

    /**
     * 组织节点的组关系
     */
    public void organizGoup(ObNode obNode, ObGroup obGroup, boolean isAdd) {
        byte[] cmd = new byte[62];
        tsOrganizGroup(cmd, obGroup.getAddr(), obNode.getAddr(), isAdd, rfAddr, rfAddr.length);
        LogUtil.log(this, "organizGoup: 管理组关系");
        sendCMD(cmd);
    }

    private native void tsEditSceneId(byte[] array, int vailable, int operaType, int sceneSer, byte[] id, int idLen, int sceneGroup);

    /**
     * 新增传使能1，立即生效传修改其余都传0，要执行非立即执行操作则使能位不能为2
     * 因为obox在收到立即生效指令的时候只处理此事件，而其他情况使能和不使能是可以同时处理修改名字指令
     * 改变使能状态的时候操作类型传2即修改，并且传入场景原先id,以免id丢失
     * 同样修改场景的id的时候也要传入场景原先的使能状态，以免使能状态丢失
     *
     * @param vailable  使能 0 不使能  1 使能 2立即生效  {@link ObScene#DISABLE}
     *                  {@link ObScene#ENABLE},{@link ObScene#EXUTE}
     * @param operaType 操作类型 0 删除  1新增（序号传0） 2修改id {@link ObScene#DELETE},{@link ObScene#CRETE},{@link ObScene#MODIFY}
     * @param sceneSer  场景序号
     * @param id        场景id,
     */
    public void editSceneId(final int vailable, final int operaType, int sceneSer, byte[] id, int sceneGroup) {
        final byte[] cmd = new byte[62];
        tsEditSceneId(cmd, vailable, operaType, sceneSer, id, id.length, sceneGroup);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    LogUtil.log(this, ": 管理场景id》》使能状态 = " + vailable + "操作类型 =" + operaType);
                    sendCMD(cmd);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private native void tsEditSceneCondition(byte[] array, int sceneSer, int conditionGroupNum, int sceneConditionsLen);

    private native void tsEditSceneConditionDetial(byte[] array, byte[] conditionAddr, byte[] condition,
                                                   int conditionAddrLen, int conditionLen, int circleIndex);

    private native void tsEditSceneConditionEnd(byte[] array, int conditionType);

    /**
     * 修改条件采用覆盖方式,因为返回过来的数据为两个条件组的时候第二条编号是ff，而实际上可能对应obox内的编号为1,3或者1，2
     * 不能确定， 所以对任何条件的操作都发送三帧数据，不包含条件的条件组全部发0即可抹除obox残余数据
     *
     * @param sceneSer         场景序号
     * @param conditonGroupNum 条件组编号，1,2,3表示第几个条件
     * @param sceneConditions  条件组内容
     */
    public void editSceneCondition(int sceneSer, int conditonGroupNum, List<SceneCondition> sceneConditions) {
        final byte[] cmd = new byte[62];
        tsEditSceneCondition(cmd, sceneSer, conditonGroupNum, sceneConditions.size());
        /*条件类型 0 无  1定时 2 传感器 3 遥控器*/
        int conditionType = 0;
        for (int i = 0; i < sceneConditions.size(); i++) {
            SceneCondition sceneCondition = sceneConditions.get(i);
            conditionType += sceneCondition.getconditionType() << (i * 2);

            System.arraycopy(sceneCondition.getConditionaddr(), 0, cmd, index[13] + i * 15, 7);
            System.arraycopy(sceneCondition.getCondition("" + sceneSer), 0, cmd, index[13] + 7 + i * 15, 8);
            tsEditSceneConditionDetial(cmd, sceneCondition.getConditionaddr(), sceneCondition.getCondition("" + sceneSer),
                    sceneCondition.getConditionaddr().length, sceneCondition.getCondition("" + sceneSer).length, i);
        }
        tsEditSceneConditionEnd(cmd, conditionType);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    LogUtil.log(this, ": 管理场景条件");
                    sendCMD(cmd);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private native void tsEditSceneAction(byte[] array, byte[] actionAddr, byte[] action, int actionAddrLen,
                                          int actionLen, boolean isGroup, int circlrIndex);

    private native void tsEditSceneActionEnd(byte[] array, int sceneSer, int option, boolean isAllDelete, int actionsLen);

    /**
     * 设置场景的行为节点
     *
     * @param sceneSer     场景序列号
     * @param sceneActions 行为节点
     * @param isDel        是否删除
     */
    public void editSceneAction(int sceneSer, List<SceneAction> sceneActions, final boolean isDel, boolean isAllDelete) {
        final byte[] cmd = new byte[62];
        /*选项*/
        int option = 0;
        final int len = sceneActions.size();
        for (int i = 0; i < len; i++) {
            SceneAction sceneAction = sceneActions.get(i);
            option += (isDel ? 0 : 2) << (i * 2);
            boolean isGroup = false;
            if (sceneAction instanceof ObGroup) {
                isGroup = true;
            } else if (sceneAction instanceof ObNode) {
                isGroup = false;
            }
            if (sceneAction.getActions(sceneSer) != null) {
                LogUtil.log(this, "editSceneAction: ==" + Transformation.byteArryToHexString(sceneAction.getActions(sceneSer)));
            }
            tsEditSceneAction(cmd, sceneAction.getAddrs(), sceneAction.getActions(sceneSer) == null ? new byte[8] : sceneAction.getActions(sceneSer),
                    sceneAction.getAddrs().length, 8, isGroup, i);
        }
        tsEditSceneActionEnd(cmd, sceneSer, option, isAllDelete, len);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    LogUtil.log(this, ": 管理场景行为是否删除 " + isDel + "len := " + len);
                    sendCMD(cmd);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void setRfAddr(byte[] rfAddr) {
        this.rfAddr = rfAddr;
    }

    public String getRfAddr() {
        return Transformation.byteArryToHexString(rfAddr);
    }

    private native void tsSetWifiConfig(byte[] array, int config);

    /**
     * 设置wifi配置
     * 2 ap模式,4恢复rf默认密码
     */
    public void setWifiConfig(int config) {
        byte[] cmd = new byte[62];
        tsSetWifiConfig(cmd, config);
        LogUtil.log(this, ": 设置wifi配置 ,设置模式 --》" + config);
        sendCMD(cmd);
    }
}
