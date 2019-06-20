package com.onbright.oblink.local.bean;


import com.onbright.oblink.MathUtil;
import com.onbright.oblink.local.net.OBConstant;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地组,可通过组关系统一设置若干的单节点设备
 * Created by Adolf_Dong on 2016/5/24.
 */
public class ObGroup extends SceneAction implements Serializable {
    private byte num;
    /**
     * 组地址
     */
    private byte addr;
    /**
     * 返回组id，用以表示组的名称
     */
    private byte[] id;
    private byte[] indexs;
    private List<Integer> indexList;
    /**
     * 组内节点列表
     */
    private List<ObNode> obNodes;

    public byte[] getRfAddr() {
        return rfAddr;
    }

    /**
     * rf地址，即所属obox的地址
     */
    private byte[] rfAddr;

    public byte getNum() {
        return num;
    }

    public List<Integer> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<Integer> indexList) {
        this.indexList = indexList;
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

    /**
     * 获得组索引数组
     */
    public byte[] getIndexs() {
        return indexs;
    }

    public void setIndexs(byte[] indexs) {
        this.indexs = indexs;
    }

    public ObGroup() {

    }

    public ObGroup(byte num, byte addr, byte[] id, byte[] indexs) {
        this.num = num;
        this.addr = addr;
        this.id = MathUtil.validArray(id);
        this.indexs = indexs;
        indexList = new ArrayList<>();
        MathUtil.index2List(indexs, indexList);
    }

    public List<ObNode> getObNodes() {
        if (obNodes == null) {
            obNodes = new ArrayList<>();
        }
        return obNodes;
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

    /**设置组内单节点的状态
     * @param status 要设置的状态 7字节
     */
    public void setStatus(byte[] status) {
        for (ObNode obNode :
                obNodes) {
            obNode.setState(status);
        }
    }


}
