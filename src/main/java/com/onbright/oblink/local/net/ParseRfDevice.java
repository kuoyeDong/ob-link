package com.onbright.oblink.local.net;

import android.os.Bundle;
import android.os.Message;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.LocalDataPool;

import java.util.Arrays;

/**
 * Created by shifan_xiao on 2017/9/05.
 * 解析设备的扫描，释放的数据处理
 */

public class ParseRfDevice {
    private static int[] index = new int[65];
    static {
        for (int i = 0; i < index.length; i++) {
            index[i] = i - 1;
        }
    }
    public static void parseRfDevice(byte[] bf,String oboxSer) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putByteArray(OBConstant.StringKey.KEY, bf);
        message.setData(bundle);
        int cmd = ((bf[4] & 0xff) << 8) + (bf[5] & 0xff);
        switch (cmd) {
             /*扫描节点处理*/
            case 0x2003:
                byte[] serNm = Arrays.copyOfRange(bf, index[27], index[27 + 5]);
                boolean isSucces = bf[index[8]] == OBConstant.ReplyType.SUC;
                if (!MathUtil.byteArrayIsZero(serNm)) {
                    message.what = isSucces ? OBConstant.ReplyType.ON_GET_NEW_NODE : OBConstant.ReplyType.SEARCH_NODE_FAL;
                    if (isSucces) {
                        ParseUtil.parseNewNode(message, LocalDataPool.newInstance().getObNodeMap());
                    }
                } else {
                    byte setType = bf[index[9]];
                    switch (setType) {
                        case 0:
                            message.what = isSucces ? OBConstant.ReplyType.STOP_SEARCH_SUC : OBConstant.ReplyType.STOP_SEARCH_FAL;
                            break;
                        case 1:
                            message.what = isSucces ? OBConstant.ReplyType.START_SEARCH_SUC : OBConstant.ReplyType.START_SEARCH_FAL;
                            break;
                        case 2:
                            message.what = isSucces ? OBConstant.ReplyType.FORCE_SEARCH_SUC : OBConstant.ReplyType.FORCE_SEARCH_FAL;
                            break;
                    }
                }
                break;
             /*释放节点返回*/
            case 0xa00a:
                message.what = bf[index[8]] == OBConstant.ReplyType.SUC ?
                        OBConstant.ReplyType.ON_REALEASE_SUC : OBConstant.ReplyType.ON_REALEASE_FAL;
                if (message.what ==OBConstant.ReplyType.ON_REALEASE_SUC) {
                    LocalDataPool.newInstance().getObnodesForOneObox(oboxSer).clear();
                    LocalDataPool.newInstance().getObScenesForOneObox(oboxSer).clear();
                    LocalDataPool.newInstance().getObGroupsForOneObox(oboxSer).clear();
                }
                break;
        }
    }
}
