package com.onbright.oblink.local.net;

import android.os.Bundle;
import android.os.Message;

import com.onbright.oblink.local.LocalDataPool;
import com.onbright.oblink.local.Obox;
import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shifan_xiao on 2017/9/05.
 * 解析节点，组的状态数据处理
 */

public class ParseNewStatus {
    private static int[] index = new int[65];
    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }
    public static void parseNewStatus(byte[] bf) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putByteArray(OBConstant.StringKey.KEY, bf);
        message.setData(bundle);
        int cmd = ((bf[4] & 0xff) << 8) + (bf[5] & 0xff);
        switch (cmd) {
            /*设置节点状态回复*/
            case 0xa100:
                 /*byte7是否成功,8-14节点完整地址，接节点状态*/
                if (bf[7] == OBConstant.ReplyType.SUC) {
                    message.what = OBConstant.ReplyType.SET_STATUS_SUC;
                } else if (bf[7] == OBConstant.ReplyType.FAL) {
                    message.what = OBConstant.ReplyType.SET_STATUS_FAL;
                }

                ObNode obNodeS = null;
                ObGroup obGroupS = null;
                Obox oboxS;
                List<ObGroup> obGroupLists = new ArrayList<>();
                byte nodeAddrS = bf[14];//节点地址
                byte groupAddrS = bf[13];//组地址
                boolean isGroupS = false;
                boolean isSingle = false;
                if (Transformation.byte2HexString(groupAddrS).equals("00")) {
                    isSingle = true;
                } else {
                    isGroupS = true;
                }
                byte[] oboxStrArryS = Arrays.copyOfRange(bf, index[8], index[8] + 5);
                String oboxStrS = Transformation.byteArryToHexString(oboxStrArryS);
                List<ObNode> obNodesS = LocalDataPool.newInstance().getObnodesForOneObox(oboxStrS);
                for (int m = 0;m< LocalDataPool.newInstance().getOboxs().size();m++) {
                    if (LocalDataPool.newInstance().getOboxs().get(m).getObox_serial_id().equals(oboxStrS)) {
                        oboxS = LocalDataPool.newInstance().getOboxs().get(m);
                        obGroupLists = LocalDataPool.newInstance().getObGroupsForOneObox(oboxS);
                    }
                }
                if (obNodesS != null) {
                    for (int k = 0; k < obNodesS.size(); k++) {
                        if (Transformation.byte2HexString(obNodesS.get(k).getAddr()).equals(Transformation.byte2HexString(nodeAddrS))) {
                            obNodeS = obNodesS.get(k);
                        }
                    }
                }
                if (obGroupLists != null) {
                    for (int j = 0; j < obGroupLists.size(); j++) {
                        if (Transformation.byte2HexString(obGroupLists.get(j).getAddr()).equals(groupAddrS)) {
                            obGroupS = obGroupLists.get(j);
                        }
                    }
                }
                ParseUtil.onSetStatusRec(message, isSingle, isGroupS, obNodeS,obGroupS);
                break;
        }
    }
}
