package com.onbright.oblink.cloud.bean.infraredtransponderbean;

/**
 * 按键类型枚举
 *
 * @author dky
 * 2019/8/12
 */
public enum KeyTypeEnum {
    /**
     * 标准按键
     */
    STANDARD_TYPE(0),
    /**
     * 拓展(自定义)按键
     */
    EXTENTDS_TYPE(1),
    /**
     * 发送测试码，该类型在备选码库中挑选合适码库时使用
     */
    TEST_CODE_TYPE(2);
    private final int keyType;

    KeyTypeEnum(int type) {
        this.keyType = type;
    }

    public int getKeyType() {
        return keyType;
    }

}