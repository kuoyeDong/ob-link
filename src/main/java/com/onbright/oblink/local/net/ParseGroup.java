package com.onbright.oblink.local.net;

import android.os.Bundle;
import android.os.Message;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.Obox;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by shifan_xiao on 2017/8/30.
 * 解析组新建，删除，组内节点关系的数据处理
 */

public class ParseGroup {
    private static int[] index = new int[65];
    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }
    public static void onUpdateListen(byte[] bf) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putByteArray(OBConstant.StringKey.KEY, bf);
        message.setData(bundle);
        int cmd = ((bf[4] & 0xff) << 8) + (bf[5] & 0xff);
        switch (cmd) {
            /*新增删除重命名现有节点、组回复*/
            case 0xa004:
                /*byte7是否成功,8-14节点完整地址，接节点状态*/
                if (bf[7] == OBConstant.ReplyType.SUC) {
                    message.what = OBConstant.ReplyType.EDIT_NODE_OR_GROUP_SUC;
                } else if (bf[7] == OBConstant.ReplyType.FAL) {
                    message.what = OBConstant.ReplyType.EDIT_NODE_OR_GROUP_FAL;
                }
                byte[] bytes = ParseUtil.getBytes(message);
                byte num = bytes[index[10]];
                byte addr = bytes[index[11]];
                byte[] id = Arrays.copyOfRange(bytes, index[12], index[12] + 16);
                byte[] indexs = Arrays.copyOfRange(bytes, index[28], index[28] + 32);
                byte[] datas = ParseUtil.getBytes(message);

                ObNode obNode = null;
                ObGroup newObGroup = null;
                ObGroup obGroup1 = null;
                Obox obox1;
                List<ObGroup> obGroupList = new ArrayList<>();
                byte nodeAddr = bf[index[10] + 6];//节点地址
                byte groupAddr = bf[index[10] + 5];//组地址
                boolean isGroup = ParseUtil.getBytes(message)[index[15]] != 0;
                byte[] oboxStrArry = Arrays.copyOfRange(bf, index[10], index[10] + 5);
                String oboxStr = Transformation.byteArryToHexString(oboxStrArry);
                List<ObNode> obNodes = LocalDataPool.newInstance().getObnodesForOneObox(oboxStr);
                for (int i = 0;i< LocalDataPool.newInstance().getOboxs().size();i++) {
                    if (LocalDataPool.newInstance().getOboxs().get(i).getObox_serial_id().equals(oboxStr)) {
                        obox1 = LocalDataPool.newInstance().getOboxs().get(i);
                        obGroupList = LocalDataPool.newInstance().getObGroupsForOneObox(obox1);
                    }
                }
                switch (MathUtil.byteIndexValid(datas[index[9]], 0, 2)) {
                    /*删除*/
                    case 0:
                        if (obNodes != null) {
                            for (int i = 0; i < obNodes.size(); i++) {
                                if (Transformation.byte2HexString(obNodes.get(i).getAddr()).equals(Transformation.byte2HexString(nodeAddr))) {
                                    obNode = obNodes.get(i);
                                }
                            }
                        }
                        if (obGroupList != null) {
                            for (int j = 0; j < obGroupList.size(); j++) {
                                if (Transformation.byte2HexString(obGroupList.get(j).getAddr()).equals(Transformation.byte2HexString(groupAddr))) {
                                    obGroup1 = obGroupList.get(j);
                                }
                            }
                        }
                        if (isGroup) {
                            ParseUtil.onEditNodeOrGroup(message, false, obNode, obGroup1);
                        } else {
                            ParseUtil.onEditNodeOrGroup(message, true, obNode, obGroup1);
                        }
                        break;
                    /*新增*/
                    case 1:
                        newObGroup = new ObGroup(num, addr, id, indexs);
                        if (obNodes != null) {
                            for (int i = 0;i< obNodes.size();i++) {
                                if (Transformation.byte2HexString(obNodes.get(i).getAddr()).equals(Transformation.byte2HexString(nodeAddr))) {
                                    obNode = obNodes.get(i);
                                }
                            }
                        }
                        if (isGroup) {
                            ParseUtil.onEditNodeOrGroup(message, false, null, newObGroup);
                        } else {
                            ParseUtil.onEditNodeOrGroup(message, true, obNode, newObGroup);
                        }
                        break;
                    /*重命名*/
                    case 2:
                        if (obNodes != null) {
                            for (int i = 0; i < obNodes.size(); i++) {
                                if (Transformation.byte2HexString(obNodes.get(i).getAddr()).equals(Transformation.byte2HexString(nodeAddr))) {
                                    obNode = obNodes.get(i);
                                }
                            }
                        }
                        if (obGroupList != null) {
                            for (int j = 0; j < obGroupList.size(); j++) {
                                if (Transformation.byte2HexString(obGroupList.get(j).getAddr()).equals(Transformation.byte2HexString(groupAddr))) {
                                    obGroup1 = obGroupList.get(j);
                                }
                            }
                        }
                        if (isGroup) {
                            ParseUtil.onEditNodeOrGroup(message, false, obNode, obGroup1);
                        } else {
                            ParseUtil.onEditNodeOrGroup(message, true, obNode, obGroup1);
                        }
                        break;
                }
                break;
            /*设置节点组地址回复*/
            case 0xa006:
                boolean isSuc = bf[index[8]] == OBConstant.ReplyType.SUC;
                if (isSuc) {
                    message.what = OBConstant.ReplyType.ON_ORGNZ_GROUP_SUC;
                } else {
                    message.what = OBConstant.ReplyType.ON_ORGNZ_GROUP_FAL;
                }
                byte[] oboxStrArryRes = Arrays.copyOfRange(bf, index[9], index[9] + 5);
                String oboxStrRes = Transformation.byteArryToHexString(oboxStrArryRes);
                byte groupAddrRes = bf[index[9] + 5];//组地址
                byte nodeAddrRes = bf[index[9] + 6];//节点地址
                Obox obox;
                List<ObGroup> obGroups = new ArrayList<>();
                ObGroup obGroup = null;
                List<ObNode> obNodesRes = LocalDataPool.newInstance().getObnodesForOneObox(oboxStrRes);
                for (int i = 0;i< LocalDataPool.newInstance().getOboxs().size();i++) {
                    if (LocalDataPool.newInstance().getOboxs().get(i).getObox_serial_id().equals(oboxStrRes)) {
                        obox = LocalDataPool.newInstance().getOboxs().get(i);
                        obGroups = LocalDataPool.newInstance().getObGroupsForOneObox(obox);
                    }
                }
                if (Transformation.byte2HexString(groupAddrRes).equals("00")) {//编辑已有组，组地址为0，通过节点的组地址去找到响应的组
                        if (obNodesRes != null) {
                            for (ObNode obNodeRes : obNodesRes) {
                                if (Transformation.byte2HexString(obNodeRes.getAddr()).equals(Transformation.byte2HexString(nodeAddrRes))) {
                                    for (int i = 0; i < obGroups.size(); i++) {
                                        if (Transformation.byte2HexString(obGroups.get(i).getAddr()).equals(Transformation.byte2HexString(obNodeRes.getGroupAddr()))) {
                                            obGroup = obGroups.get(i);
                                        }
                                    }
                                }

                            }

                    }
                } else {//新建组，组地址不为0，通过返回的组地址去找到响应的组
                    for (int i = 0;i<obGroups.size();i++) {
                        if (Transformation.byte2HexString(obGroups.get(i).getAddr()).equals(Transformation.byte2HexString(groupAddrRes))) {
                            obGroup = obGroups.get(i);
                            break;
                        }
                    }
                }

                if (obNodesRes != null) {
                    for (ObNode obNodeRes : obNodesRes) {
                        if (Transformation.byte2HexString(obNodeRes.getAddr()).equals(Transformation.byte2HexString(nodeAddrRes))) {
                            ParseUtil.onOrganizGoup(obNodeRes, obGroup, message);
                            break;
                        }
                    }
                }
                break;
        }
    }
}
