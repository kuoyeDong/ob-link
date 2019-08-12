package com.onbright.oblink.cloud.bean.infraredtransponderbean;

import java.io.Serializable;

/**
 * 码库命令数据，仅用于匹配的遥控器,用于云与遥控云间处理key的映射关系，原则上前端不需要使用，可以在调试过程中查看数据是否出错
 *
 * @author dky
 * 2019/8/12
 */
public class RcCommand implements Serializable {
    /**
     * 国际化键显示名
     */
    private String kn;
    /**
     * 原始码
     */
    private String src;

    public RcCommand(String kn, String src) {
        this.kn = kn;
        this.src = src;
    }

    public String getKn() {
        return kn;
    }

    public void setKn(String kn) {
        this.kn = kn;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

}
