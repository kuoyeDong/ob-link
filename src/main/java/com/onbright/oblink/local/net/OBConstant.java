package com.onbright.oblink.local.net;


public interface OBConstant {

    /**
     * 键值
     */
    interface StringKey {
        String UTF8 = "utf-8";
        String PSW = "88888888";
        String KEY = "obkeydata";
        String RFPSWKEY = "rfpsw";
        String PSKEY = "psw";
        String ISLOCALKEY = "islocal";
        String JSON_OBOX = "cloud_obox";
        String NODETYPE = "nodetype";
        String CTRL_NODE = "ctrl_node";
        String LAMP = "lamp";
        String SENSOR = "sensor";
        String GATEWAY = "gateway";
        String POSITION_ROOM = "position_room";
        String YS_BINDING = "ys_binding";
        String YS_ACCESS_TOKEN= "ys_access_token";
        String CLOUD_USER = "cloud_user";
        String CLOUD_PSW = "cloudpsw";
        String IS_LOCAL = "is_local";
        String IS_FIRST_RUN = "is_first_run";
        String DANGWEI = "dangwei";
        String WINDTYPE = "wind_type";
        String FRAGMENT_ELEMENT = "fragment_element";
        String SPKEY = "share";

        String PARENTTYPE = "parenttype";
        String TYPE = "type";
        String TITLE_NAME = "title_name";
        String IS_SINGLE = "is_single";
        String IS_GROUP = "is_group";
        String IS_ACTION = "is_action";
    }

    /**
     * {@link #ADMIN}与的主要区别在于其可以创建楼层概念，楼宇概念可以用来区分智能楼宇和智能家居
     * 服务器登录后详细的用户类型
     */
    interface CloudDitalMode {
        /**
         * 超级管理员，拥有所有权限
         */
        int SUPERROOT = 0;
        /**
         * 总管理员，拥有创建楼宇管理员权限，次于超级管理员的所有权限，当有授权码的时候就可以创建ADMINOFBD
         */
        int ROOT = 1;
        /**
         * 楼宇管理员，权限:创建guest，创建楼层，创建房间，增加位置信息，修改组节点关系，创建情景,设备升级权限，
         */
        int ADMIN = 2;

        /**
         * 访客，拥有节点控制，组控制，情景使能，位置信息读取权限
         */
        int GUEST = 3;
    }

    /**
     * 回复类型
     */
    interface ReplyType {
        /**
         * 失败
         */
        int FAL = 0;
        /**
         * 成功
         */
        int SUC = 1;

        int BURN_BACK = 100;
        int UP_BACK = 101;
        int WIPE_BACK = 102;
        int PROTECT_BACK = 103;

        /**
         * crc校验出错
         */
        int WRONG_CRC = 1001;
        /**
         *
         */
        int WRONG_TIME_OUT = 1002;
        int WRONG_NOT_SUPPORT = 1003;
        int WRONG_WRONG_PWD = 1004;
        int NOT_REPLY = 1000;

        /**
         * 获取obox名称的返回
         */
        int GET_OBOX_NAME_BACK = 1;
        /**
         * 拿单灯返回，表示obox中还有灯，可将拿灯方法非组id参数累加1继续拿灯
         */
        int GET_SINGLENODE_BACK = 2;
        /**
         * 拿组返回
         */
        int GET_GROUP_BACK = 3;
        int CLOSE_CLOUD_FAL = 4;
        int GET_STATE = 5;
        int CHANG_RF_PSW_FAL = 6;
        int CHANG_RF_PSW_SUC = 7;
        int ON_SET_MODE = 8;
        int GET_OBOX_MSG_BACK = 9;
        int OPEN_CLOUD_FAL = 10;
        int CLOSE_CLOUD_SUC = 11;
        int OPEN_CLOUD_SUC = 12;
        int UPLOAD_CLOUD = 13;
        int GET_SCENE_BACK = 14;
        int START_SEARCH_SUC = 15;
        int START_SEARCH_FAL = 15;
        int FORCE_SEARCH_SUC = 16;
        int FORCE_SEARCH_FAL = 17;
        int STOP_SEARCH_SUC = 18;
        int STOP_SEARCH_FAL = 19;
        int SEARCH_NODE_FAL = 20;
        int ON_GET_NEW_NODE = 21;
        int SET_STATUS_SUC = 22;
        int SET_STATUS_FAL = 23;
        /**
         * 组和单节点的重命名删除。 新增组成功返回
         */
        int EDIT_NODE_OR_GROUP_SUC = 24;
        /**
         * 组和单节点的重命名删除。 新增组失败返回
         */
        int EDIT_NODE_OR_GROUP_FAL = 25;

        int ON_ORGNZ_GROUP_SUC = 26;
        int ON_ORGNZ_GROUP_FAL = 27;
        int ON_REALEASE_SUC = 28;
        int ON_REALEASE_FAL = 29;
        /**
         * 设置场景参数相关,包括重命名删除，修改场景内条件和场景内的节点（不包括立即执行）
         */
        int ON_SETSCENE_SUC = 30;
        int ON_SETSCENE_FAL = 31;
        /**
         * 执行场景的成功返回
         */
        int ON_EXCUTE_SCENE_SUC = 32;
        int ON_EXCUTE_SCENE_FAL = 33;


        int ON_SETOBOXTIME_SUC = 34;
        int ON_SETOBOXTIME_FAL = 35;
        int ON_SETAP_SUC = 36;
        int ON_INIT_RFPWD_SUC = 37;
        int ON_SETAP_FAL = 38;
        int ON_INIT_RFPWD_FAL = 39;
    }

    /**
     * 网络状态
     */
    interface NetState {
        /**
         * ap 状态
         */
        int ON_AP = 1;
        /**
         * station状态
         */
        int ON_STATION = 2;

        int ON_CLOUD = 3;
        /**
         * 无网络状态
         */
        int UN_NET = 0;
        /**
         * 广播包接收完毕
         */
        int ON_DSFINISH = 1;
        /**
         * obox广播包接收完毕
         */
        int ON_DSFINISH_OBOX = 100;
        /**
         * 服务器广播包接收完毕
         */
        int ON_DSFINISH_SERVER = 101;
    }

    /**
     * 加密类型
     */
    interface PackType {
        int ORIGINAL_ENCRYPTED = 0;
        int ACTIVATED_UNENCRYPTED = 1;
    }
    /**
     * 出错类型
     */
    interface ErrType {
        int CRC_ERR = 1;
        int TIMEOUT = 2;
        int SUPPORT_ERR = 3;
        int PSW_ERR = 4;
        int FLASH_ERR = 5;
    }

    /**
     * 设备类型和子类型
     */
    interface NodeType {

        int IS_UNKNOWN = 0;
        /**
         * 主设备类型是灯
         */
        int IS_LAMP = 1;
        /**
         * 单色灯
         */
        int IS_SIMPLE_LAMP = 1;
        /**
         * 双色灯
         */
        int IS_WARM_LAMP = 2;
        /**
         * 三色灯
         */
        int IS_COLOR_LAMP = 3;
        /**
         * 智能电饭煲
         */
        int IS_COOKER = 2;
        /**
         * 智能加湿器
         */
        int IS_HUMIDIFIER = 3;
        /**
         * 智能插座/开关
         */
        int IS_OBSOCKET = 4;
        /**
         * 智能插座
         */
        int CHAZUO = 1;
        /**
         * 智能开关
         */
        int SWITCH = 2;
        /**
         * 智能开合类设备
         */
        int IS_CURTAIN = 5;
        /**
         * 窗帘
         */
        int THE_CURTAINS = 1;
        /**
         * 投影仪幕布
         */
        int WINDOW_CURTAINS = 2;

        /**
         * 智能风扇
         */
        int IS_FAN = 6;
        /**
         * 智能空气净化器
         */
        int IS_AIR_CLEAN = 7;
        /**
         * 兼容型智能电视
         */
        int IS_TV = 8;
        /**
         * 测试用USB_RF模块
         */
        int USB_RF = 9;
        /**
         * 网关
         */
        int GATEWAY = 10;
        /**
         * 传感器与子类型 智能传感器
         */
        int IS_SENSOR = 11;
        /**
         * 光明
         */

        int ALS = 1;
        /**
         * 水浸
         */
        int FLOOD = 2;
        /**
         * 雷达
         */
        int RADAR = 3;
        /**
         * CO
         */
        int CO = 4;
        /**
         * 环境（光湿温）
         */
        int ENVIRONMENTAL = 5;
        /**
         * 人体感应（雷达+红外）
         */
        int BODY = 6;
        /**
         * 空气质量（PM2.5+VOC）
         */
        int PM2_5 = 7;
        /**
         * 供电检测器
         */
        int POWER_CHECK = 8;
        /**
         * 虚拟雷达
         */
        int VR_RADAR = 9;
        /**
         * 光线传感器
         */
        int LIGHT_SENSOR = 10;
        /**
         * 温度湿度传感器
         */
        int TEMP_HUMID_SENSOR = 11;
        /**
         * 烟雾传感器
         */
        int SMOKE_SENSOR = 12;

        /**
         * 传感器的预留
         */
        int RESERVE = 13;
        /**
         * 是个雷达
         */
        int XIBING_RADAR = 14;

        int SCENE_PANEL = 17;

        /**
         * 智能抄电表
         */
        int AMMETER = 12;
        /**
         * 智能空调
         */
        int AIR_CON = 13;
        /**
         * 智能红外转发
         */
        int RED_OUT = 14;
        /**
         * 智能遥控设备
         */
        int REMOTER = 15;
        /**
         * 飞行设备
         */
        int AEROCRAFT = 16;

        int AEROCRAFT_4WING = 1;
        /**
         * id长度
         */
        int ID_LEN = 16;
    }


    /**
     * 属性动画proper
     */
    interface AnimatorProper {
        String ROTATE = "rotation";
        String TRANSX = "translationX";
        String TRANSY = "translationY";
        String SCALEX = "scaleX";
        String SCALEY = "scaleY";
        String ALPHA = "alpha";
    }

}