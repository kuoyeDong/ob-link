package com.onbright.oblink.local.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.bean.ObScene;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;


class TcpReceive extends Thread {
    private Socket mSocket;
    private Handler mHandler;
    private static final String TAG = "Recive";
    private boolean canRec = true;
    private static int[] index = new int[65];
    private boolean mustRec = true;
    private byte[] mPswbytes;
    private ByteBuffer validBuffer = ByteBuffer.allocate(512);
    private Sbox msBox;

    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }

    void setMy(boolean my) {
        isMy = my;
    }

    void setMustRec(boolean mustRec) {
        this.mustRec = mustRec;
    }

    /**
     * 是否本机发送
     */
    private boolean isMy = true;

    /**
     * 是否必须接收
     */


    TcpReceive(Socket mSocket, Handler mHandler, Sbox sbox) {
        this.mSocket = mSocket;
        this.mHandler = mHandler;
        msBox = sbox;
    }

    @Override
    public void run() {
        try {
            while (canRec) {
                int len = 68;
                byte[] sData = new byte[len];
                int rlRead = mSocket.getInputStream().read(sData);
                if (rlRead == 68) {
                    byte[] receiveData = new byte[rlRead];
                    System.arraycopy(sData, 0, receiveData, 0, rlRead);
                    getValidData(receiveData, validBuffer);
                    Arrays.fill(sData, (byte) 0);
                    byte[] goal = msBox.unPack(receiveData, mPswbytes);
                    if (!NetLock.isCompile(goal)) {
                        continue;
                    }
                    if (mustRec || isMy) {
                        isMy = false;
                        onRecieve(goal);
                    }
                    validBuffer.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getValidData(byte[] src, ByteBuffer buf) {
        boolean isValid = true;
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            if (i == 0 && v == 255) {
                isValid = false;
            }
            if (isValid) {
                if (buf.position() < 32) {
                    buf.put(src[i]);
                }
            } else {
                if (v != 255) {
                    isValid = true;
                    if (buf.position() < 32) {
                        buf.put(src[i]);
                    }
                }
            }
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * cmd是45字节，有效数据从第7字节开始
     * 前面4个字节的0，两个字节的cmd，1个字节的长度，接payload
     *
     * @param bf 转换后的64字节数据
     */
    private void onRecieve(byte[] bf) {
        mHandler.removeMessages(OBConstant.ReplyType.NOT_REPLY);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putByteArray(OBConstant.StringKey.KEY, bf);
        msg.setData(bundle);
        if (NetState.isTranse || NetState.isBurn) {
            onUpdate(bf[0], msg);
            return;
        }
        int cmd = ((bf[4] & 0xff) << 8) + (bf[5] & 0xff);
        switch (cmd) {
            case 0x2001:
                msg.what = OBConstant.ReplyType.GET_OBOX_NAME_BACK;
                break;
            case 0xa007:
                onChangPwd(bf[7], msg);
                break;
            /*获取obox信息，obox内节点信息，组信息，版本信息*/
            case 0xa013:
                onGetMsg(bf, msg);
                break;
            /*获取状态返回*/
            case 0x2100:
                onGetState(msg);
                break;
             /* 设置连接与断开服务器*/
            case 0xa012:
                onSetOboxMode(bf, msg);
                break;
            /*获取情景信息*/
            case 0x200e:
                onReceiveScene(msg);
                break;
            /*扫描节点处理*/
            case 0x2003:
                onRfCmd(bf, msg);
                break;
            /*新增删除重命名现有节点、组回复*/
            case 0xa004:
                onEditNodeOrGroup(bf, msg);
                break;
            /*设置节点状态回复*/
            case 0xa100:
                onSetState(bf, msg);
                break;
            /*设置节点组地址回复*/
            case 0xa006:
                onOrganizGoup(bf, msg);
                break;
            /*释放节点返回*/
            case 0xa00a:
                onRealease(bf, msg);
                break;
            /*设置场景信息返回*/
            case 0xa00e:
                onSetScene(bf, msg);
                break;
            /*设置时间*/
            case 0xa00d:
                onSetOboxTime(bf, msg);
                break;
            case 0xa008:
                onSetWifiConfig(bf, msg);
                break;
            /*出错*/
            case 0x200f:
                onWrong(bf, msg);
                break;
            default:
                break;
        }
        mHandler.sendMessage(msg);
    }
    /**设置wifi配置，设置ap模式，恢复默认密码*/
    private void onSetWifiConfig(byte[] bf, Message msg) {
        switch (bf[index[8]]) {
            case OBConstant.ReplyType.SUC:
                switch (bf[index[9]]) {
                    case 2:
                        msg.what = OBConstant.ReplyType.ON_SETAP_SUC;
                        break;
                    case 4:
                        msg.what = OBConstant.ReplyType.ON_INIT_RFPWD_SUC;
                        break;
                }
                break;
            case OBConstant.ReplyType.FAL:
                switch (bf[index[9]]) {
                    case 2:
                        msg.what = OBConstant.ReplyType.ON_SETAP_FAL;
                        break;
                    case 4:
                        msg.what = OBConstant.ReplyType.ON_INIT_RFPWD_FAL;
                        break;
                }
                break;
        }
    }

    private void onSetOboxTime(byte[] bf, Message msg) {
        msg.what = bf[index[8]] == 1 ? OBConstant.ReplyType.ON_SETOBOXTIME_SUC : OBConstant.ReplyType.ON_SETOBOXTIME_FAL;
    }

    private void onSetScene(byte[] bf, Message msg) {
        switch (bf[index[8]]) {
            case OBConstant.ReplyType.SUC:
                if (bf[index[9]] == ObScene.OBSCENE_ID && MathUtil.byteIndexValid(bf[index[10]], 4, 2) == ObScene.EXUTE) {
                    msg.what = OBConstant.ReplyType.ON_EXCUTE_SCENE_SUC;
                } else {
                    msg.what = OBConstant.ReplyType.ON_SETSCENE_SUC;
                }
                break;
            case OBConstant.ReplyType.FAL:
                if (bf[index[9]] == ObScene.OBSCENE_ID && MathUtil.byteIndexValid(bf[index[10]], 4, 2) == ObScene.EXUTE) {
                    msg.what = OBConstant.ReplyType.ON_EXCUTE_SCENE_FAL;
                } else {
                    msg.what = OBConstant.ReplyType.ON_SETSCENE_FAL;
                }
                break;
        }
    }

    private void onWrong(byte[] bf, Message msg) {
        switch (bf[index[8]]) {
            case 1:
                msg.what = OBConstant.ReplyType.WRONG_CRC;
                break;
            case 2:
                msg.what = OBConstant.ReplyType.WRONG_TIME_OUT;
                break;
            case 3:
                msg.what = OBConstant.ReplyType.WRONG_NOT_SUPPORT;
                break;
            case 4:
                msg.what = OBConstant.ReplyType.WRONG_WRONG_PWD;
                break;
        }
    }

    private void onRealease(byte[] bf, Message msg) {
        msg.what = bf[index[8]] == OBConstant.ReplyType.SUC ?
                OBConstant.ReplyType.ON_REALEASE_SUC : OBConstant.ReplyType.ON_REALEASE_FAL;
    }

    private void onOrganizGoup(byte[] bf, Message msg) {
        boolean isSuc = bf[index[8]] == OBConstant.ReplyType.SUC;
        if (isSuc) {
            msg.what = OBConstant.ReplyType.ON_ORGNZ_GROUP_SUC;
        } else {
            msg.what = OBConstant.ReplyType.ON_ORGNZ_GROUP_FAL;
        }
    }

    private void onEditNodeOrGroup(byte[] bf, Message msg) {
 /*byte7是否成功,8-14节点完整地址，接节点状态*/
        if (bf[7] == OBConstant.ReplyType.SUC) {
            msg.what = OBConstant.ReplyType.EDIT_NODE_OR_GROUP_SUC;
        } else if (bf[7] == OBConstant.ReplyType.FAL) {
            msg.what = OBConstant.ReplyType.EDIT_NODE_OR_GROUP_FAL;
        }
    }

    private void onSetState(byte[] bf, Message msg) {
        /*byte7是否成功,8-14节点完整地址，接节点状态*/
        if (bf[7] == OBConstant.ReplyType.SUC) {
            msg.what = OBConstant.ReplyType.SET_STATUS_SUC;
        } else if (bf[7] == OBConstant.ReplyType.FAL) {
            msg.what = OBConstant.ReplyType.SET_STATUS_FAL;
        }
    }

    /**
     * 扫描节点处理
     */
    private void onRfCmd(byte[] bf, Message msg) {
        byte[] serNm = Arrays.copyOfRange(bf, index[27], index[27 + 5]);
        boolean isSuc = bf[index[8]] == OBConstant.ReplyType.SUC;
        if (!MathUtil.byteArrayIsZero(serNm)) {
            msg.what = isSuc ? OBConstant.ReplyType.ON_GET_NEW_NODE : OBConstant.ReplyType.SEARCH_NODE_FAL;
        } else {
            byte setType = bf[index[9]];
            switch (setType) {
                case 0:
                    msg.what = isSuc ? OBConstant.ReplyType.STOP_SEARCH_SUC : OBConstant.ReplyType.STOP_SEARCH_FAL;
                    break;
                case 1:
                    msg.what = isSuc ? OBConstant.ReplyType.START_SEARCH_SUC : OBConstant.ReplyType.START_SEARCH_FAL;
                    break;
                case 2:
                    msg.what = isSuc ? OBConstant.ReplyType.FORCE_SEARCH_SUC : OBConstant.ReplyType.FORCE_SEARCH_FAL;
                    break;
            }
        }
    }

    /**
     * 收到情景相关回复
     */
    private void onReceiveScene(Message msg) {
        msg.what = OBConstant.ReplyType.GET_SCENE_BACK;
    }

    /**
     * 升级情况是特殊情况特殊处理
     */
    private void onUpdate(byte src, Message msg) {
        int type = MathUtil.validByte(src);
        switch (type) {
            case 0xb1:
                msg.what = OBConstant.ReplyType.BURN_BACK;
                break;
            case 0xb2:
                msg.what = OBConstant.ReplyType.UP_BACK;
                break;
            case 0xb3:
                msg.what = OBConstant.ReplyType.WIPE_BACK;
                break;
            case 0xb4:
                msg.what = OBConstant.ReplyType.PROTECT_BACK;
                break;
            default:
                break;
        }
        mHandler.sendMessage(msg);
    }


    private void onGetState(Message msg) {
        msg.what = OBConstant.ReplyType.GET_STATE;
    }

    /**
     * 获得obox内的版本信息
     */
    private void onGetMsg(byte[] bf, Message msg) {
        switch (bf[7]) {
            case 3:
                switch (bf[8]) {
                    /*节点信息 */
                    case 2:
                        msg.what = OBConstant.ReplyType.GET_SINGLENODE_BACK;
                        break;
                    /*组信息 */
                    case 4:
                        msg.what = OBConstant.ReplyType.GET_GROUP_BACK;
                        break;
                    /*obox信息  序列号 版本号 */

                    case 10:
                        msg.what = OBConstant.ReplyType.GET_OBOX_MSG_BACK;
                        break;
                    case 11:
                        msg.what = OBConstant.ReplyType.ON_SET_MODE;
                        break;
                }
        }
    }

    /**
     * 设置obox连接模式，设置obox
     */
    private void onSetOboxMode(byte[] bf, Message msg) {
        switch (bf[7]) {
            /*操作失败*/
            case OBConstant.ReplyType.FAL:
                switch (bf[8]) {
                    case 0:
                        msg.what = OBConstant.ReplyType.CLOSE_CLOUD_FAL;
                        break;
                    case 1:
                        msg.what = OBConstant.ReplyType.OPEN_CLOUD_FAL;
                        break;
                }
                break;
            case OBConstant.ReplyType.SUC:
                switch (bf[8]) {
                    /*关闭服务器连接*/
                    case 0:
                        msg.what = OBConstant.ReplyType.CLOSE_CLOUD_SUC;
                        break;
                    /*打开服务器连接*/
                    case 1:
                        msg.what = OBConstant.ReplyType.OPEN_CLOUD_SUC;
                        break;
                }
                break;
        }
    }

    /**
     * 修改obox的密码
     */
    private void onChangPwd(byte b, Message msg) {
        switch (b) {
            case OBConstant.ReplyType.FAL:
                msg.what = OBConstant.ReplyType.CHANG_RF_PSW_FAL;
                break;
            case OBConstant.ReplyType.SUC:
                msg.what = OBConstant.ReplyType.CHANG_RF_PSW_SUC;
                break;
        }
    }

    /**
     * 断开连接
     */
    void disConnect() {
        canRec = false;
    }

    /**
     * 以字节数组方式设置解析密码
     */
    void setPswbytes(byte[] pswbytes) {
        mPswbytes = pswbytes;
    }


}
