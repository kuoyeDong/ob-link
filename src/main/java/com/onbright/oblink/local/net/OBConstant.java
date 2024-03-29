package com.onbright.oblink.local.net;


public interface OBConstant {

    int MAX_SCENE_LIMIT = 24;

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
        String YS_ACCESS_TOKEN = "ys_access_token";
        String CLOUD_USER = "cloud_user";
        String CLOUD_USER_WEIGHT = "cloud_user_weight";
        String CLOUD_PSW = "cloudpsw";
        String IS_LOCAL = "is_local";
        /**
         * 微信登录
         */
        String IS_WECHAT_LOGIN = "is_wechat_login";
        String IS_FIRST_RUN = "is_first_run";
        String DANGWEI = "dangwei";
        String WINDTYPE = "wind_type";
        String FRAGMENT_ELEMENT = "fragment_element";
        String SPKEY = "share";
        String PHONE = "phone";
        String PHONE_LIST = "phone_list";

        String PARENTTYPE = "parenttype";
        String TYPE = "type";
        String TITLE_NAME = "title_name";
        String IS_SINGLE = "is_single";
        String IS_GROUP = "is_group";
        String IS_ACTION = "is_action";
        /**
         *
         */
        String UPDATE_OBOX_STATUS = "update_obox_status";
        String UPDATE_NODES_CLOUD = "update_nodes_cloud";
        String UPDATE_NODES_LOCAL = "update_nodes_local";
        String UPDATE_SCENE_LOCAL = "update_scene_local";
        String UPDATE_MODIFY_USER = "update_modify_user";
        String UPDATE_FLIGHT_CLOUD = "update_flight_cloud";
        String UPDATE_FLIGHT_LOCAL = "update_flight_local";
        /**
         * 门锁重置权限密码推送
         */
        String LOCK_ADMIN_PWD_RESET = "lock_admin_pwd_reset";
        /**
         * 房间内节点位置信息变更
         */
        String UPDATE_SET_DEVICE_LOCATION = "update_set_device_location";
        String UPDATE_SCENE_CLOUD_SETTING = "update_scene_cloud_setting";
        String UPDATE_SCENE_LOCAL_SETTING = "update_scene_local_setting";
        String UPDATE_SCENE_CLOUD_EXUTE = "update_scene_cloud_exute";
        String UPDATE_SCENE_LOCAL_EXUTE = "update_scene_local_exute";
        String UPDATE_SCENE_LOCAL_PREVIEW = "update_scene_local_preview";
        /**
         * 房间绑定情景的更新
         */
        String UPDATE_SCENE_LOCATION = "update_scene_location";
        /**
         * 创建和删除房间的更新
         */
        String UPDATE_CREATE_LOCATION = "update_create_location";
        String UPDATE_ADD_OBOX = "update_add_obox";
        String UPDATE_GROUPS_INFO = "update_groups_info";
        /**
         * 扫描设备新入网设备的广播
         */
        String UPDATE_SCAN_INFO = "update_scan_info";
        String OBOX_HEART_INFO = "obox_heart_info";
        /**
         * 单品wifi上下线推送时广播key
         */
        String WIFI_HEART_INFO = "wifi_heart_info";
        /**
         * obox状态改变主动上报
         */
        String STATUS_CHANGE_REPORT = "update_nodes_cloud_2500";
        /**
         * 刷新门锁是否有验证用户
         */
        String UPDATE_LOCK_ISAUTH = "update_lock_isauth";
        /**
         * wifi红外转发器学习按键广播的action
         */
        String LEARN_REMOTE = "learnRemote";
        /**
         * MQTT返回一键匹配测试码库结果广播的action
         */
        String PAIR_IR_REMOTE = "pairIrRemote";
        /**
         * MQTT返回下载码库到设备的广播action
         */
        String DOWN_IR_REMOTE = "down_ir_remote";
        /**
         * 广播action,添加wifi单品设备后刷新请求
         */
        String UPDATE_WIFI_DEVICE = "update_wifi_device";
        /**
         * 广播action,刷新码库方案
         */
        String FRES_PROGRAM = "fres_program";
        /**
         * 存储口令的键
         */
        String TOKEN = "token";
        /**
         * 微信openId键
         */
        String WX_OPEN_ID = "wx_open_id";

        /**
         * 存储服务器键start
         */
        String SERVER = "server";
        /**
         * rename node id
         */
        String UPDATE_NODE_ID = "update_node_id";
        /**
         * rename group id
         */
        String UPDATE_GROUP_ID = "update_group_id";
        /**
         * 控制指令回复
         */
        String CONTROL_STATUS_CHANGE = "control_status_change";
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
         * 获取obox名称的返回,并且返回flash修改时间
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
        int ON_GET_VERSION_SUCCESS = 40;
        int ON_GET_VERSION_FAIL = 41;
        int ACTIVE_SEARCH_SUC = 42;
        int ACTIVE_SEARCH_FAL = 43;
        int ON_SET_ROUTE_SSID = 44;
        int ON_SET_ROUTE_PWD = 45;
        int ON_SET_HEART_SUCCESS = 46;
        int ON_SET_HEART_FAIL = 47;
        int ADD_REMOTE_SUC = 48;
        int DELETE_REMOTE_SUC = 49;
        int PAIR_REMOTE_SUC = 50;
        int UNPAIR_REMOTE_SUC = 51;
        int TIMER_30S_TOOFF_SUC = 52;
        int NIGHTLIGHT_SUC = 53;
        int ON_REMOTE_SUC = 54;
        int OFF_REMOTE_SUC = 55;
        int RGB_REMOTE_SUC = 56;
        int DOUBLE_REMOTE_SUC = 57;
        int SET_REMOTE_FAL = 58;
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
         * 三路滑条
         */
        int IS_THREE_WAY_LAMP = 13;
        /**
         * 智能电饭煲
         */
        int IS_COOKER = 2;
        /**
         * 智能加湿器
         */
        int IS_HUMIDIFIER = 3;
        /**
         * 插座/开关大类
         */
        int IS_OBSOCKET = 4;
        /**
         * 插座
         */
        int SOCKET = 1;
        /**
         * 单线开关
         */
        int SWITCH = 2;
        /**
         * 1路开关
         */
        int SINGLE_TOUCH_SWITCH = 21;
        /**
         * 2路开关
         */
        int DOUBLE_TOUCH_SWITCH = 22;
        /**
         * 3路开关
         */
        int THREE_TOUCH_SWITCH = 23;
        /**
         * 4路开关
         */
        int FOUR_TOUCH_SWITCH = 24;
        /**
         * 1路开关 + 3路情景面板
         */
        int SINGLE_SWITCH_SCENE_PANEL = 41;
        /**
         * 2路开关 + 3路情景面板
         */
        int DOUBLE_SWITCH_SCENE_PANEL = 42;
        /**
         * 3路开关 + 3路情景面板
         */
        int THREE_SWITCH_SCENE_PANEL = 43;
        /**
         * 一键单线开关
         */
        int ONE_BUTTON_WIRE_SOCKET = 51;
        /**
         * 二键单线开关
         */
        int TWO_BUTTON_WIRE_SOCKET = 52;

        /**
         * 红外对管、3路开关 + 3路情景面板
         */
        int THREE_SWITCH_RED_SCENE_PANEL = 83;
        /**
         * 六键情景
         */
        int SIX_SCENE_PANEL = 36;
        /**
         * 六键情景+红外对管
         */
        int SIX_SCENE_RED_PANEL = 86;

        /**
         * 一路情景面板
         */
        int SINGLE_SCENE_PANEL = 31;
        /**
         * 两路情景面板
         */
        int DOUBLE_SCENE_PANEL = 32;
        /**
         * 3路情景面板
         */
        int THREE_SCENE_PANEL = 33;
        /**
         * 4路情景面板
         */
        int FOUR_SCENE_PANEL = 34;
        /**
         * 2开关+2情景面板
         */
        int TWO_SWITCH_TWO_SCENE_PANEL = 62;
        /**
         * 单一窗帘面板
         */
        int CURTIAN_PANEL = 91;
        /**
         * 两个窗帘面板
         */
        int DOUBLE_CURTIAN_PANEL = 92;
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
         * 超声波传感器
         */
        int ULTRASOUND = 13;
        /**
         * 是个雷达（酒店版本）
         */
        int HOTEL_RADAR = 14;
        /**
         * 环境传感器（7合一）
         */
        int ENVROMENT_SENSOR = 16;
        /**
         * 感应面板
         */
        int SENSING_PANEL = 17;

        /**
         * dc红外
         */
        int DC_RED_SENSOR = 18;
        /**
         * 红外
         */
        int RED_SENSOR = 19;
        /**
         * PM2.5
         */
        int PM2_5_SENSOR = 20;
        /**
         * 门磁
         */
        int DOOR_WINDOW_MAGNET = 21;

        /**
         * DC人体+光感
         */
        int DC_BODY_ALS = 23;

        /**
         * AC人体+光感
         */
        int AC_BODY_ALS = 24;

        /**
         * 插卡取电感应器，00插卡，01拔出，02断电
         */
        int ELECTRIC_CARD = 15;

        /**
         * 智能抄电表
         */
        int AMMETER = 12;
        /**
         * 线控面板
         */
        int CONTROL_PANEL = 13;
        /**
         * 空调线控面板
         */
        int AIR_CON_PANEL = 1;

        /**
         * 智能红外转发
         */
        int RED_OUT = 14;

        /**
         * 低功耗蓝牙红外转发器
         */
        int RED_OUT_BLE = 1;
        /**
         * wifi蓝牙转发器
         */
        int RED_OUT_WIFI = 2;
        /**
         * 智能遥控设备
         */
        int REMOTER = 15;

        /**
         * 智能摄像类设备
         */
        int SMART_VIDEO = 17;
        int FIXED_SMART_VIDEO = 1;//固定摄像设备（家用）
        int MOVEING_SMART_VIDEO = 2;//防抖摄像设备（配合云台）

        /**
         * 智能门禁，中控指纹机
         */
        int SMART_FINGER = 20;
        /**
         * 智能门锁
         */
        int SMART_LOCK = 21;

        /**
         * 昂宝智能门锁（家居）
         */
        int SMART_LOCK_OB_HOUSE = 3;
        /**
         * 昂宝智能门锁（酒店公寓）
         */
        int SMART_LOCK_OB_HOTEL = 4;
        /**
         * 摄像头
         */
        int SMART_CAMERA = 32;
        /**
         * id长度
         */
        int ID_LEN = 16;
        /**
         * 遥控灯
         */
        int REMOTE_MULTI_LED = 22;

        /**
         * 单品wifi插座
         */
        int WIFI_SOCKET = 80;

        /**
         * 单品wifi红外转发器，不存储版本
         */
        int WIFI_IR = 81;

    }

    /**
     * 红外设备类型
     */
    interface IrType {
        /**
         * 机顶盒
         */
        int TV_BOX = 1;
        /**
         * 电视
         */
        int TV = 2;
        /**
         * dvd
         */
        int DVD = 3;
        /**
         * 投影仪
         */
        int PROJECTOR = 5;
        /**
         * 风扇
         */
        int FAN = 6;
        /**
         * 空调
         */
        int AIR_CON = 7;
        /**
         * 智能灯
         */
        int LAMP = 8;
        /**
         * 互联网机顶盒
         */
        int NET_TV_BOX = 10;
        /**
         * 扫地机
         */
        int CLEAR_DOOR_MACHINE = 12;
        /**
         * 音响
         */
        int SPEAKER = 13;
        /**
         * 空气净化器
         */
        int AIR_CLEAR = 15;
        /**
         * 其他类型
         */
        int OTHER = 0;
    }

    /**
     * 添加设备时wifi设备类型，根据此设备类型控制不同操作
     */
    interface OnAddWifiDeviceType {
        /**
         * obox智能网关
         */
        int OBOX = 10;
        /**
         * wifi红外转发器
         */
        int IR = 14;
        /**
         * wifi插座
         */
        int SOCKET = 4;
    }

    /**
     * 包归属
     */
    interface PackInfoKey {
        int SMART = 0;
        int GTQ = 2;
    }
}