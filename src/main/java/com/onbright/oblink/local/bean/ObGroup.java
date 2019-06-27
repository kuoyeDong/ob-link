package com.onbright.oblink.local.bean;

import android.support.annotation.NonNull;


import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地组元素
 * Created by Adolf_Dong on 2016/5/24.
 */
public class ObGroup extends SceneAction implements Serializable,Comparable<ObGroup> {
    public transient boolean isOnline = true;
    private byte num;
    /**
     * 组地址
     */
    private byte addr;
    private byte[] id;
    private List<Integer> indexList;
    /**
     * 组内节点
     */
    private List<ObNode> obNodes;

    public byte[] getRfAddr() {
        return rfAddr;
    }

    /**
     * rf地址
     */
    private byte[] rfAddr;

    public byte getNum() {
        return num;
    }

    public List<Integer> getIndexList() {
        return indexList;
    }


    public void setNum(byte num) {
        this.num = num;
    }

    public byte getAddr() {
        return addr;
    }

    public void setAddr(byte addr) {
        this.addr = addr;
    }

    public byte[] getId() {
        return id;
    }
    /**
     * 返回字符串名称
     */
    public String getNodeId() {
        try {
            return new String(id, OBConstant.StringKey.UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setId(byte[] id) {
        this.id = id;
    }


    public ObGroup() {

    }

    public ObGroup(byte num, byte addr, byte[] id, byte[] indexs) {
        this.num = num;
        this.addr = addr;
        this.id = MathUtil.validArray(id);
        indexList = new ArrayList<>();
        for (int index = 0; index < 32; index++) {
            if (indexs[index] == 0) {
                continue;
            }
            for (int i = 0; i < 8; i++) {
                if (((indexs[index] >> i) & 0x01) != 0) {
                    int indexVal = index * 8 + i + 1;
                    indexList.add(indexVal);
                }
            }
        }
    }

    public List<ObNode> getObNodes() {
        if (obNodes == null) {
            obNodes = new ArrayList<>();
        }
        return obNodes;
    }

    @Override
    public void putAction(int SceneSerNum, byte[] action) {
        super.putAction(SceneSerNum, action);
        for (ObNode obNode :
                getObNodes()) {
            obNode.putAction(SceneSerNum,action);
        }
    }

    public void setObNodes(List<ObNode> obNodes) {
        this.obNodes = obNodes;
    }

    public void setRfAddrs(byte[] rfAddr) {
        this.rfAddr = rfAddr;
    }


    @Override
    public byte[] getAddrs() {
        byte[] cplAddr = new byte[7];
        System.arraycopy(rfAddr, 0, cplAddr, 0, rfAddr.length);
        cplAddr[5] = addr;
        cplAddr[6] = (byte) 0xff;
        return cplAddr;
    }


    public byte[] getCplAddr() {
        return getAddrs();
    }

    public int getGroupPType() {
        if (obNodes != null && obNodes.size() != 0) {
            return obNodes.get(0).getParentType();
        }
        return 0;
    }

    public int getGroupType() {
        if (obNodes != null && obNodes.size() != 0) {
            return obNodes.get(0).getType();
        }
        return 0;
    }

    public byte[] getGroupState() {
        if (obNodes != null && obNodes.size() != 0) {
            return obNodes.get(0).getState();
        }
        return new byte[7];
    }

    public void setStatus(byte[] status) {
        for (ObNode obNode :
                obNodes) {
            obNode.setState(status);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public int compareTo(@NonNull ObGroup o) {
        return Transformation.byteArryToHexString(this.getAddrs()).compareTo(Transformation.byteArryToHexString(o.getAddrs()));
    }
}
