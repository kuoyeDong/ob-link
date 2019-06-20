package com.onbright.oblink.local.bean;

public class Version {

    byte[] hardVersion;
    int mainVersion;
    int Version;


    /**
     * 不唯一构造方法，建议使用ParseUtil中的getVersionOfNet(Message msg)或者getVersionOfFile(File file)获得本类对象
     *
     * @param hardVersion 硬件信息
     * @param mainVersion 软件信息1
     * @param version     软件信息2
     */
    public Version(byte[] hardVersion, int mainVersion, int version) {
        super();
        this.hardVersion = hardVersion;
        this.mainVersion = mainVersion;
        Version = version;
    }

    /**
     * 返回硬件信息
     *
     * @return 硬件信息
     */
    public byte[] getHardVersion() {
        return hardVersion;
    }

    /**
     * 设置硬件信息
     *
     * @param hardVersion 硬件信息
     */
    public void setHardVersion(byte[] hardVersion) {
        this.hardVersion = hardVersion;
    }

    /**
     * 获得主版本号
     *
     * @return 主版本号
     */
    public int getMainVersion() {
        return mainVersion;
    }

    public void setMainVersion(int mainVersion) {
        this.mainVersion = mainVersion;
    }

    /**
     * 获得次版本号
     *
     * @return 次版本号
     */
    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

}