package com.onbright.oblink.local.net;

import android.content.Context;
import android.content.Intent;

import com.onbright.oblink.LogUtil;
import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.local.bean.ObNode;

import java.util.Arrays;
import java.util.List;

/**
 * 解析obox的上传
 * Created by adolf_dong on 2017/7/17.
 */

public class ParseUpLoad {
    /**
     * 是否需要回复2500
     */
    private static final boolean NeedReply = false;
    private static int[] index = new int[65];

    private NotMySetSceneChange notMySetSceneChange;

    private TcpSend tcpSend;
    private String oboxSer;

    public ParseUpLoad(Context context, TcpSend tcpSend) {
        this.context = context;
        this.tcpSend = tcpSend;
        notMySetSceneChange = new NotMySetSceneChange(context);
    }

    private Context context;

    /**
     * 传感器
     */
    private static final int NODE = 0;
    /**
     * obox
     */
    private static final int OBOX = 1;
    /**
     * 情景触发
     */
    private static final int SCENE = 2;

    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }

    /**
     * @param upload 接收数据
     */
    public void onUpLoad(byte[] upload) {
        /*非2500交互上报*/
        if (!(upload[index[5]] == 0x25 && upload[index[6]] == 0)) {
            int cmd = ((upload[4] & 0xff) << 8) + (upload[5] & 0xff);
            switch (cmd) {
                /*组和节点数据产生变化*/
                case 0xa004:
                case 0xa006:
                    ParseGroup.onUpdateListen(upload);
                    Intent intent = new Intent();
                    intent.setAction(OBConstant.StringKey.UPDATE_NODES_LOCAL);
                    context.sendBroadcast(intent);
                    break;
                /*设置节点状态回复*/
                case 0xa100:
                    ParseNewStatus.parseNewStatus(upload);
                    Intent intent1 = new Intent();
                    intent1.setAction(OBConstant.StringKey.UPDATE_NODES_LOCAL);
                    context.sendBroadcast(intent1);
                    break;
                /*解析设备的扫描，释放的数据处理*/
                case 0xa00a:
                case 0x2003:
                    ParseRfDevice.parseRfDevice(upload, oboxSer);
                    Intent intent2 = new Intent();
                    intent2.setAction(OBConstant.StringKey.UPDATE_NODES_LOCAL);
                    context.sendBroadcast(intent2);
                    break;
                /*情景数据发生变化*/
                case 0xa00e:
                    notMySetSceneChange.onSceneChange(upload);
                    intent = new Intent();
                    intent.setAction(OBConstant.StringKey.UPDATE_SCENE_LOCAL);
                    context.sendBroadcast(intent);
                    break;
            }
            LogUtil.log(this,"parseupload broad cast");
        } else {
            /*2500上报*/
            byte[] oboxStrArry = Arrays.copyOfRange(upload, index[8], index[8] + 5);
            LogUtil.log(this,"oboxStrArry == " + Transformation.byteArryToHexString(oboxStrArry));
            String oboxStr = Transformation.byteArryToHexString(oboxStrArry);
            if (NeedReply) {
                tcpSend.sendAck();
            }
            int uplaodType = upload[index[8] + 7];
            switch (uplaodType) {
                case NODE:
                    byte[] cpladdr = Arrays.copyOfRange(upload, index[8], index[8] + 7);
                    List<ObNode> obNodes = LocalDataPool.newInstance().getObnodesForOneObox(oboxStr);
                    if (obNodes != null) {
                        for (ObNode obNode : obNodes) {
                            byte[] cachecpladdr = obNode.getCplAddr();
                            if (Arrays.equals(cachecpladdr, cpladdr)) {
                                byte[] payLoad = Arrays.copyOfRange(upload, index[8] + 7 + 1, index[8] + 7 + 1 + 8);
                                int ptype = obNode.getParentType();
                                int type = obNode.getType();
                                byte[] state = new byte[8];
                                switch (ptype) {
                                    case OBConstant.NodeType.IS_SENSOR:
                                        switch (type) {
                                            case OBConstant.NodeType.ENVROMENT_SENSOR:
                                                if (MathUtil.byteIndexValid(payLoad[0], 4, 4) == 0) {
                                                    System.arraycopy(payLoad, 0, obNode.getState(), 0, 6);
                                                } else {
                                                    System.arraycopy(payLoad, 0, obNode.getState(), 6, 6);
                                                }
                                                sendBroadUpdateNodeState(obNode);
                                                return;
                                            default:
                                                System.arraycopy(payLoad, 0, state, 0, 7);
                                                break;
                                        }
                                        break;
                                    case OBConstant.NodeType.IS_OBSOCKET:
                                        switch (type) {
                                            case OBConstant.NodeType.SOCKET:
                                                state[0] = payLoad[1];
                                                state[1] = payLoad[0];
                                                System.arraycopy(payLoad, 2, state, 2, state.length - 2);
                                                break;
                                            case OBConstant.NodeType.SINGLE_SWITCH_SCENE_PANEL:
                                            case OBConstant.NodeType.DOUBLE_SWITCH_SCENE_PANEL:
                                            case OBConstant.NodeType.THREE_SWITCH_SCENE_PANEL:
                                            case OBConstant.NodeType.THREE_SWITCH_RED_SCENE_PANEL:
                                            case OBConstant.NodeType.TWO_SWITCH_TWO_SCENE_PANEL:
                                                state[0] = payLoad[1];
                                                state[1] = payLoad[3];
                                                System.arraycopy(payLoad, 2, state, 2, state.length - 2);
                                                break;
                                            default:
                                                state[0] = payLoad[1];
                                                System.arraycopy(payLoad, 1, state, 1, state.length - 1);
                                                break;
                                        }
                                        break;
                                    default:
                                        System.arraycopy(payLoad, 0, state, 0, state.length);
                                        break;
                                }
                                obNode.setState(state);
                                sendBroadUpdateNodeState(obNode);
                                break;
                            }
                        }
                    }
                    break;
                case OBOX:
                    break;
                case SCENE:

                    break;
            }
        }
    }

    /**
     * 发送更新状态通知
     *
     * @param obNode 被更新的节点
     */
    private void sendBroadUpdateNodeState(ObNode obNode) {
        Intent broadIntent = new Intent();
        broadIntent.setAction(OBConstant.StringKey.UPDATE_OBOX_STATUS);
        broadIntent.putExtra("state", Transformation.byteArryToHexString(obNode.getState()));
        broadIntent.putExtra("serialId", Transformation.byteArryToHexString(obNode.getSerNum()));
        context.sendBroadcast(broadIntent);
    }

    /**
     * 设置obox对应的obox序列号
     *
     * @param oboxSer obox序列号
     */
    public void setPaseUpLocad(String oboxSer) {
        notMySetSceneChange.setPaseUpLocad(oboxSer);
        this.oboxSer = oboxSer;
    }

}
