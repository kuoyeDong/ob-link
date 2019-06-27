package com.onbright.oblink.local.bean;

import android.support.annotation.NonNull;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.StringUtil;
import com.onbright.oblink.local.net.Transformation;

/**
 * 本地登录模式Device，不包含版本信息和设备序列号信息，版本信息存与灯中，
 * 所以在此不包含版本信息
 * Created by Adolf_Dong on 2016/5/24.
 */
public class ObNode extends SceneAction implements Comparable<ObNode>{
    public transient boolean isOnline = true;
    /**
     * 所属obox
     */
    private String oboxName;

    /**
     * 获取单节点流程中标识的编号
     */
    private byte num;

    /**
     * rf地址
     */
    private byte[] rfAddr;
    /**
     * 节点地址
     */
    private byte addr;

    /**
     * 节点所在组地址，当组地址不等于0的时候，就说明该节点处于某个组内
     */
    private byte groupAddr;

    /**
     * id
     */
    private byte[] id;
    /**
     * 节点序列号
     */
    private byte[] serNum;

    /**
     * 父类型
     */
    private byte parentType;
    /**
     * 子类型
     */
    private byte type;

    /**
     * 8字节版本信息
     */
    private byte[] version;
    /**
     * 节点可支持剩余场景数
     */
    private byte surplusSence;

    /**
     * 状态、根据派生类实际情况而定
     */
    protected byte[] state;

    /**
     * 7字节完整地址
     */
    private byte[] cplAddr;


    public ObNode() {

    }

    public ObNode(byte[] rfAddr, byte groupAddr, byte addr, byte[] state) {
        this.rfAddr = rfAddr;
        this.groupAddr = groupAddr;
        this.addr = addr;
        this.state = state;
    }

    /**
     * 用于收集节点
     */
    public ObNode(byte num, byte[] rfAddr, byte addr, byte[] id,
                  byte[] serNum, byte parentType, byte type,
                  byte[] version, byte surplusSence, byte gourpAddr, byte[] state) {
        this.num = num;
        this.rfAddr = rfAddr;
        this.addr = addr;
        this.id = MathUtil.validArray(id);
        this.serNum = serNum;
        this.parentType = parentType;
        this.type = type;
        this.version = version;
        this.surplusSence = surplusSence;
        this.groupAddr = gourpAddr;
        this.state = state;
    }


    public ObNode(byte parentType, byte type, byte[] id, byte[] serNum, byte[] rfAddr, byte groupAddr, byte addr) {
        this.parentType = parentType;
        this.type = type;
        this.id = id;
        this.serNum = serNum;
        this.rfAddr = rfAddr;
        this.groupAddr = groupAddr;
        this.addr = addr;
    }

    public byte getNum() {
        return num;
    }

    public void setNum(byte num) {
        this.num = num;
    }

    public byte[] getRfAddr() {
        return rfAddr;
    }

    public void setRfAddr(byte[] rfAddr) {
        this.rfAddr = rfAddr;
    }

    public byte getAddr() {
        return addr;
    }

    public void setAddr(byte addr) {
        this.addr = addr;
    }

    public byte getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(byte groupAddr) {
        this.groupAddr = groupAddr;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getSerNum() {
        return serNum;
    }

    public String getSerNumString() {
        return Transformation.byteArryToHexString(serNum);
    }

    public void setSerNum(byte[] serNum) {
        this.serNum = serNum;
    }

    public int getParentType() {
        return parentType & 0xff;
    }

    public void setParentType(byte parentType) {
        this.parentType = parentType;
    }

    public int getType() {
        return type & 0xff;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public byte getSurplusSence() {
        return surplusSence;
    }

    public void setSurplusSence(byte surplusSence) {
        this.surplusSence = surplusSence;
    }

    public byte[] getState() {
        if (state == null) {
            state = new byte[7];
        }
        return state;
    }

    public void setState(byte[] state) {
        this.state = state;
    }

    public byte[] getCplAddr() {
        if (cplAddr == null) {
            cplAddr = new byte[7];
        }
        System.arraycopy(rfAddr, 0, cplAddr, 0, rfAddr.length);
        cplAddr[5] = groupAddr;
        cplAddr[6] = addr;
        return cplAddr;
    }

    public void setCplAddr(byte[] cplAddr) {
        this.cplAddr = cplAddr;
    }

    public String getOboxName() {
        return oboxName;
    }

    public void setOboxName(String oboxName) {
        this.oboxName = oboxName;
    }

    /**
     * 返回节点名称
     */
    public String getNodeId() {
        return StringUtil.getUtf8(id);
    }

    @Override
    public byte[] getAddrs() {
        return getCplAddr();
    }

    @Override
    public int compareTo(@NonNull ObNode o) {
        return this.getSerNumString().compareTo(o.getSerNumString());
    }
}

