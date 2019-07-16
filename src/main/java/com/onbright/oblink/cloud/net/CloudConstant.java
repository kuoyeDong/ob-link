package com.onbright.oblink.cloud.net;

/**
 * 网络请求参数
 * Created by adolf_dong on 2016/1/7.
 */
public interface CloudConstant {
    class Source {
        public static String HTTPS = "https://";
        public static String SERVER = SourceFrom.SERVER_IOT;
        public static final String CONSUMER_OPEN = "/consumer/common";
    }

    interface SourceFrom {
        /**
         * iot测试
         */
        String SERVER_IOT = "aliiot.on-bright.com";
        /**
         * cloud生产
         */
        String SERVER_CLOUD = "alicloud.on-bright.com";
    }

    /**
     * 请求中的参数
     */
    interface ParameterKey {
        /**
         * 参数的key
         */
        String MSG = "msg";
        String USERNAME = "user_name";
        String USERNAME_N = "username";
        String ROOTNAME = "root_name";
        String ADMINNAME = "admin_name";
        String ACCESS_TOKEN = "access_token";
        String PASS_WORD = "pwd";
        String PASS_WORD_N = "password";
        String MODIFY_TYPE = "modify_type";
        String LICENSE = "license";
        String OBOX = "obox";
        String WEIGHT = "weight";
        String YS_OPTIVATE = "ys_optivate";
        String OBOX_SERIAL_ID = "obox_serial_id";
        String DEVICE_SERIAL_ID = "serialId";
        String TYPE = "type";
        String APPKEY = "appkey";
        String SYSTEM = "system";
        String APP_ID = "appId";
        String COVER = "cover";
        String OPERATION = "operation";
        String GROUP_STYLE = "group_style";
        String FROM_DATE = "from_date";
        String TO_DATE = "to_date";
        String FROM_DATA = "from_data";
        String TO_DATA = "to_data";
        String OBOX_OLD_PWD = "obox_old_pwd";
        String OBOX_NEW_PWD = "obox_new_pwd";
        String OBOX_NEW_NAME = "obox_name";
        String NODES = "nodes";
        String STATES = "state";
        String TIME_OUT = "timeout";
        String NAME = "name";
        String DEVICES = "devices";
        String PERIOD = "period";
        String LIMIT_TIME = "limitTime";
        String HEART_BEAT = "heartbeat";
        /**
         * 删除节点的时候使用，查询的时候如果是查询obox则不传该参数
         */
        String DEVICE_ID = "ID";
        /**
         * pa
         * 0代表查询obox，1代表查询设备
         */
        String QUERY_TYPE = "type";
        /**
         * 80代表组，00代表设备
         */
        String NODE_TYPE = "node_type";
        String OLD_ID = "old_id";
        String NEW_ID = "new_id";
        String ACTION = "action";
        String DEVICE_TYPE = "device_type";
        String DEVICE_CHILD_TYPE = "device_child_type";
        String STATE = "status";
        String TIME = "time";
        String OPERATE_TYPE = "operate_type";/*00是从当前组删除，01是添加到当前组,或者设置情景类型*/
        String SUPERID = "superID";
        String SCENE_ID = "scene_id";/*只有新增和修改场景名字的时候才需要传这参数*/
        String SCENE_NUMBER = "scene_number";/*没有场景之前该参数为0*/
        String SCENE_REMOTER_INDEX = "remoter_index";/*没有场景之前该参数为0*/
        String ACTION_TYPE = "action_type";/*场景类型enable，disable，只有新增和修改场景名字的时候才需要传这参数*/
        String SCENE_TYPE = "scene_type";/*0、无条件1、定时2、传感器*/
        String CONDITION_ID = "condition_id";
        String CONDITION = "condition";
        String ACTION_ID = "action_id";
        String FORCE = "force";
        String CONFIG = "config";
        /**
         * cmd指令
         */
        String CMD = "CMD";
        /**
         * 返回数据的key值
         */
        String IS_SUCCESS = "success";
        String START = "start";
        String START_INDEX = "start_index";
        String ADMIN_NAME = "admin_name";
        String GUEST_NAME = "guest_name";
        String COUNT = "count";
        String GROUPADDR = "groupAddr";
        String ADDR = "addr";
        String BUILDING = "building";
        String ROOM = "room";
        String LOCATION = "location";
        String SERIALID = "serialId";
        String X_AXIS = "x_axis";
        String Y_AXIS = "y_axis";
        String SCENE_STATUS = "scene_status";
        String PHONE = "phone";
        String GROUP_ID = "group_id";
        String GROUP_NAME = "group_name";
        String GROUP_NEW_NAME = "group_new_name";
        String GROUP_STATE = "group_state";
        String GROUP_MEMBER = "group_member";
        String SCENE = "scene";
        String REMOTER = "remoter";
        String REQUEST = "request";
        String DEVICE_SERIAL = "deviceSerial";
        String DIRECTION = "direction";
        String SPEED = "speed";
        String VALIDATE_CODE = "validateCode";
        String FORCE_DELETE = "force_delete";
        String APP_KEY = "appKey";
        String ZONE = "zone";
        String CODE = "code";
        String PIN = "pin";
        String IR_CFGS = "ir_cfgs";
        String DEVICETYPE = "deviceType";
        String BRAND_ID = "brandId";
        String IR_SRC = "src";
        String REMOTE_ID = "rId";
        String INDEX = "index";
        String VERSION = "version";
        String R_MODEL = "rmodel";
        String REMOTE_NAME = "name";
        String REMOTE_TYPE = "t";
        String MESSAGE_ID = "id";
        String DATA = "data";
        String SCENE_NAME = "sceneName";
        String SCENES = "scenes";
        String SENSORS = "sensors";
        String STATUS = "status";
        String MSG_ALTER = "msgAlter";
        String INTERVAL = "interval";
        String REGULATION = "regulation";
        String ID = "id";
        String SCENESTATUS = "sceneStatus";
        /**
         * 安防场景是否设置为按照预设时间段的打开和关闭,00不自动修改，01自动修改
         */
        String AUTO_MODIFY = "autoModify";

        /*以下为单品设备参数*/
        String COMMOND = "command";
        String FUNCTIONID = "functionId";
        String VALUE = "value";
        String TIMER = "timer";
        String TIMERID = "timerId";
        String DEVICEID = "deviceId";
        String CONFIGS = "configs";
        String TIMERS = "timers";
        String DEVICE_NAME = "deviceName";
        String PRODUCT_KEY = "productKey";

        /*以下为irobox使用参数*/
        String APP_CODE = "appCode";
        String IRID = "irId";
        String DEVICE_TYPE_ID = "deviceTypeId";
        String MODEL = "model";
        String MOBILE = "mobile";
        String NICKNAME = "nickName";
        String AUTH_TOKEN = "authToken";
        String START_TIME = "startTime";
        String END_TIME = "endTime";
        String TIMES = "times";
        String OLD_PWD = "oldPwd";
        String NEW_PWD = "newPwd";
        String PUSH_INFO = "pushInfo";
        String ISMAX = "isMax";
        String KEY_OR_NAME = "key_or_name";
        String IR_PROGRAM = "ir_program";
        String KEY_TYPE = "key_type";
        String GRANT_TYPE = "grant_type";
        String MESSAGE = "message";
        String SHARESDKAPPKEY = "shareSdkAppkey";
        String OPEN_ID = "openId";
        String WX_TOKEN = "wxToken";
        /**
         * 标准按键或拓展按键的按键名称key
         */
        String KEY = "key";
        /**
         * 0:标准按键
         * 1:拓展按键
         * 2:手动匹配测试按键
         * 按键类型
         * 对于2仅用于发送手动匹配的测试码库按键
         */
        String KEYTYPE = "keyType";
        /**
         * 匹配的测试码库rid,遥控云码库id
         */
        String REMOTEID = "remoteId";

        /**
         * 光头强通道名字
         */
        String NAMES = "names";
        /**
         * 第三方唯一用户标识
         */
        String UNIQUE_KEY = "uniqueKey";
    }

    /**
     * 请求参数字段具体值
     */
    interface ParameterValue {
        String BILI = "ff";
        String LightDef = "01";
        String NULL = "null";
        String FORCE_TRUE = "true";
        String SIMPLE_SCENE = "0";
        String TIM_SCENE = "1";
        String SENSOR_SCENE = "2";
        String OBOXS = "oboxs";
        String SCENES = "scenes";
        String UPGRADES = "upgrades";
        String INT_SIMPLE_SCENE = "0";
        String INT_TIM_SCENE = "1";
        String INT_SENSOR_SCENE = "2";
        String IS_DEL_MEMBER = "0";
        String IS_ADD_MEMBER = "1";
        int MSC_IS_GROUP = 1;
        int MSC_IS_SINGLE = 0;
        /**
         * 删除情景
         */
        String DELETE_SCENE = "delete";
        /**
         * 执行情景
         */
        String EXECUTE_SCENE = "execute";
        /**
         * 重命名情景
         */
        String RENAME_SCENE = "rename";
        /**
         * 修改情景
         */
        String MODIFY_SCENE = "modify";
        /**
         * 使能情景
         */
        String ENABLE_SCENE = "01";
        /**
         * 不使能情景
         */
        String DISABLE_SCENE = "00";
        /**
         * 添加行为节点到情景内
         */
        String ADD_ACTION = "add";
        /**
         * 修改情景内节点状态,ps 因为本地情景也是不支持情景内节点修改
         */
        String MODIFY_ACTION = "add";
        /**
         * 从情景内删除行为节点
         */
        String DEL_ACTION = "delete";
        String SETTING_ALL_NODE_STATUS = "setting_all_node_status";
        /**
         * 修改条件
         */

        String UPDATE_CONDITION = "update";
        /**
         * 新增加条件
         */
        String CREAT_CONDITION = "add";
        /**
         * 删除条件,条件是传感器这些东
         */
        String DEL_CONDITION = "delete";
        /**
         * 创建情景
         */
        String CREAT_SCENE = "add";

        /**
         * 查询一天
         */
        String ONE_DAY = "00";

        /**
         * 查询时间段
         */
        String SOME_DAY = "01";
        /**
         * 查询时间段
         */
        String WEEKLY_DAY = "02";
        /**
         * 查询时间段
         */
        String MONTHLY_DAY = "03";
        /**
         * 查询时间段
         */
        String YEAR_DAY = "04";
        /**
         * 删除组
         */
        String DELETE_GROUP = "00";
        /**
         * 设置组
         */
        String SETTING_GROUP = "01";
        /**
         * 覆盖成员
         */
        String COVER_MEMBER = "02";
        /**
         * 添加成员
         */
        String ADD_MEMBER = "03";
        /**
         * 删除成员
         */
        String DELETE_MEMBER = "04";
        /**
         * 改名
         */
        String RE_NAME = "05";
        /**
         * 执行
         */
        String EXUTE_GROUP = "06";
    }

    /**
     * cmd的对应值
     */
    interface CmdValue {


        String REGISTER = "register";
        String LOGIN = "login";
        String ADD_OBOX = "add_obox";
        String DELETE_OBOX = "delete_obox";
        String UPDATE_OBOX_PASSWORD = "update_obox_pwd";
        String RESET_OBOXPWD = "reset_obox_pwd";
        String UPDATE_OBOX_NAME = "update_obox_name";
        String RELEASE_ALL_DEVICES = "release_all_devices";
        String DELETE_SINGLE_DEVICE = "del_single_device";
        String QUERY_VERSION = "query_version";
        String UPDATE_NODE_NAME = "update_node_name";/*修改组/节点名字*/
        String SETTING_NODE_STATUS = "setting_node_status";
        String OPERATE_GROUP_MEMBERS = "operate_group_members";
        String SETTING_SC_ID = "setting_sc_id";
        String SETTING_SC_CONDITION = "setting_sc_condition";/*设置场景序号的条件信息*/
        String SETTING_SC_ACTION = "setting_sc_action";
        String QUERY_SCENES = "query_scenes";
        String SETTING_UPGRADE = "setting_upgrade";
        String QUERY_UPGRADES = "query_upgrades";
        String QUERY_OBOX_BIND = "query_obox_bind";
        String QUERY_DEVICE = "query_device";
        String QUERY_LOCATION = "query_location";
        String CREATE_LOCATION = "create_location";
        String QUERY_OBOX = "query_obox";
        String QUERY_OBOX_CONFIG = "query_obox_config";
        String QUERY_DEVICE_STATUS_HISTORY = "query_device_status_history";
        String QUERY_NODE_HISTORY = "query_user_operation_history";
        String QUERY_DEVICE_LOCATION = "query_device_location";
        String SET_DEVICE_LOCATION = "set_device_location";
        String GET_STATUS = "get_status";
        String SEARCH_NEW_DEVICES = "search_new_device";
        String GET_NEW_DEVICES = "getting_new_device";
        String MODIFY_DEVICE = "modify_device";
        String QUERY_GROUP = "query_group";
        String MULTI_RENAME = "rename_device";
        String SET_GROUP = "set_group";
        String SET_PASSWORD = "set_pwd";
        String QUERY_IR_CFG = "query_ir_cfg";
        String SET_AIRCRAFT_STATUS = "set_aircraft_status";
        String SET_AIRCRAFT_GROUP = "set_aircraft_group";

        /**
         * 查询位置绑定的情景
         */
        String QUERY_SCENE_LOCATION = "query_scene_location";
        String SET_SCENE_LOCATION = "set_scene_location";
        String DELETE_SCENE_LOCATION = "delete_scene_location";

        String EXECUTE_SC = "execute_sc";

        String BIND_PHONE = "msg_alter";
        String SETTING_SC_INFO = "setting_sc_info";

        String DETECT_REMOTE = "detect_remoter";
        String QUERY_REMOTER_CHANNEL = "query_remoter_channel";
        String QUERY_REMOTE = "query_remoter";

        String QUERY_CAMERA = "query_camera";//查询摄像头
        String QUERY_CAMERA_LIVE_ADDR = "query_camera_addr";//摄像头直播地址
        String SET_CAMERA_PTZ = "set_camera_ptz";//云台控制
        String SET_CAMERA_CAPTURE = "set_camera_capture";//云台控制

        String QUERY_YS_ACCESS_TOKEN = "query_ys_access_token";//查询access token
        String CREATE_YS_CAMERA = "create_ys_camera";//添加摄像头
        String BIND_YS_USER = "bind_ys_user";//绑定账号
        String QUERY_CAMERA_CAPTURE = "query_camera_capture";//获取摄像头下的图片
        String FINDPWD = "pwd_forget";
        String ADD_FINGERPRINT = "add_fingerprint";
        String QUERY_FINGERPRINT = "query_fingerprint";
        String QUERY_FINGER_HOME = "query_finger_home";
        String QUERY_FINGERPRINT_USER = "query_fingerprint_user";
        String QUERY_FINGERPRINT_LOG = "query_finger_log";
        String ADD_USER_FINGERPRINT = "add_user_finger";
        String MODIFY_USER = "modify_user";
        String QUERY_USER = "query_user";
        String BIND_DEVICE = "bind_device";
        String QUERY_USER_DEVICE = "query_user_device";
        String QUERY_NODE_REAL_STATUS = "query_node_real_status";
        String QUERY_MESSAGE = "query_msg";
        String UPDATE_MESSAGE_STATUS = "update_msg_state";

        String DISABLE_SCENE = "00";
        String ENABLE_SCENE = "01";
        String ACTION_SCENE = "02";
        String DELETE_SCENE = "03";


        /**
         * 查询遥控支持设备
         */
        String QUERY_DEVICE_TYPE = "query_device_type";
        /**
         * 根据设备类型查询品牌
         */
        String QUERY_BRAND = "query_brand";

        /**
         * 根据品牌ID、设备类型一键匹配遥控器列表(仅限空调)
         */
        String QUERY_REMOTE_CONTROL_SRC = "query_remote_control_src";
        /**
         * 获取红外拥有的遥控方案
         */
        String QUERY_BIND_REMOTE_CONTROL = "query_bind_remote_control";
        /**
         * 获取某个遥控器对应的详情码库（遥控云）
         */
        String QUERY_REMOTE_CONTROL_ID = "query_remote_control_id";
        /**
         * 绑定和解绑红外遥控器方案
         */
        String BIND_REMOTE_CONTROL = "bind_remote_control";
        /**
         * 获取遥控器列表、根据品牌id，设备类型
         */
        String QUERY_REMOTE_CONTROL = "query_remote_control";
        String SET_DRONE_STATUS = "set_drone_status";//飞控指令

        /**
         * 安防场景设置
         */
        String SETTING_SECURITY_SCENE = "setting_security_scene";
        /**
         * 查询安防场景
         */
        String QUERY_SECURITY_SCENE = "query_security_scene";
        /**
         * 删除安防场景
         */
        String DELETE_SECURITY_SCENE = "delete_security_scene";
        /**
         * 添加或更新场景定时任务设置
         */
        String ADDORUPDATE_TIMING_TASK = "addOrUpdate_timing_task";
        /**
         * 查询场景定时任务设置
         */
        String QUERY_TIMING_TASK = "query_timing_task";
        /**
         * 删除场景定时任务设置
         */
        String DELETE_TIMING_TASK = "delete_timing_task";

        /*以下为单品设备*/
        String SET_ALI_DEV = "set_ali_dev";
        String READ_ALI_DEV = "read_ali_dev";
        String QUERY_ALI_DEV = "query_ali_dev";
        String SET_TIMER = "set_timer";
        String SET_COUNTDOWN_TIMER = "set_countdown";
        /**
         * 注册阿里设备
         */
        String REGIST_ALIDEV = "regist_aliDev";
        String QUERY_TIMER = "query_timer";
        String QUERY_COUNTDOWN = "query_countdown";
        /**
         * 上传阿里设备配置
         */
        String UPLOAD_CONFIG = "upload_config";


        /*昂宝自己的智能门锁用到的相关接口START*/

        /**
         * 查询OB智能门锁主页信息,返回电量，上下线，状态
         */
        String QUERY_INTELLIGENT_FINGERHOME = "query_intelligent_fingerHome";

        /**
         * 查询OB智能门锁开门记录
         */
        String QUERY_INTELLIGENT_OPENRECORD = "query_intelligent_openRecord";
        /**
         * 查询OB智能门锁警告记录
         */
        String QUERY_INTELLIGENT_WARNINGRECORD = "query_intelligent_warningRecord";
        /**
         * 查询OB智能门锁用户列表
         */
        String QUERY_INTELLIGENT_USERINGRECORD = "query_intelligent_useringRecord";
        /**
         * 编辑OB智能门锁用户
         */
        String EDIT_INTELLIGENT_USER = "edit_intelligent_user";
        /**
         * 发送OB智能门锁胁迫验证码
         */
        String SEND_INTELLIGENT_VALIDATECODE = "send_intelligent_validateCode";
        /**
         * OB智能门锁创建权限密码
         */
        String ADD_INTELLIGENT_AUTHPWD = "add_intelligent_authPwd";
        /**
         * OB智能门锁验证权限密码
         */
        String QUERY_INTELLIGENT_AUTHPWD = "query_intelligent_authPwd";
        /**
         * OB智能门锁远程开锁列表
         */
        String QUERY_INTELLIGENT_REMOTE_UNLOCKING = "query_intelligent_remote_unLocking";
        /**
         * OB智能门锁创建远程授权用户
         */
        String ADD_INTELLIGENT_REMOTE_USER = "add_intelligent_remote_user";
        /**
         * OB智能门锁删除远程授权用户
         */
        String DEL_INTELLIGENT_REMOTE_USER = "del_intelligent_remote_user";
        /**
         * OB智能门锁忘记权限密码
         */
        String FORGET_INTELLIGENT_PWD = "forget_intelligent_pwd";
        /**
         * 智能门锁修改权限密码
         */
        String RESET_INTELLIGENT_PWD = "reset_intelligent_pwd";
        /**
         * 发送密码给临时用户
         */
        String SEND_REMOTE_PWD = "send_remote_pwd";
        /**
         * 智能门锁根据推送重置权限密码
         */
        String RESET_INTELLIGENT_PWD_BY_CODE = "reset_intelligent_pwd_by_code";
        /**
         * 智能门锁修改远程用户
         */
        String MODIFY_INTELLIGENT_REMOTE_USER = "modify_intelligent_remote_user";
        /**
         * 查询推送设置列表
         */
        String QUERY_INTELLIGENT_PUSH_LIST = "query_intelligent_push_list";
        /**
         * 修改推送设置
         */
        String MODIFY_INTELLIGENT_PUSH = "modify_intelligent_push";

        /*昂宝自己的智能门锁用到的相关接口END*/

        /*以下为wifi红外转发器*/
        /**
         * 刷新token
         */
        String REFRESH_TOKEN = "refresh_token";
        /**
         * 注册前的申请
         */
        String REGISTER_BEFORE = "register_before";
        /**
         * 遥控灯对码
         */
        String PAIR_REMOTE_LED = "pair_remote_led";
        /**
         * 遥控灯清码
         */
        String UN_PAIR_REMOTE_LED = "un_pair_remote_led";
        /**
         * 设置遥控灯状态
         */
        String SETTING_REMOTE_LED = "setting_remote_led";

        String SETTING_REMOTE_RGB = "setting_remote_rgb";
        String SETTING_REMOTE_DOUBLE = "setting_remote_double";
        String ON_REMOTE_RGB = "on_remote_rgb";
        String OFF_REMOTE_RGB = "off_remote_rgb";
        String REMOTE_TIMER30S_TOOFF = "remote_timer30s_tooff";
        String REMOTE_NIGHTLIGHT = "remote_nightlight";
        String ADD_REMOTE_LED = "ADD_REMOTE_LED";
        String DELETE_REMOTE_LED = "delete_remote_led";

        /**
         * 微信登陆
         */
        String WECHAT_LOGIN = "wechat_login";
        /**
         * 删除阿里云设备
         */
        String DELETE_ALI_DEV = "delete_ali_dev";
        /**
         * wifi查询遥控器支持的设备类型
         */
        String QUERY_IR_DEVICE_TYPE = "query_ir_device_type";
        /**
         * 获取遥控云品牌类型
         */
        String QUERY_IR_BRAND = "query_ir_brand";
        /**
         * 获取红外遥控方案
         */
        String QUERY_IR_DEVICE = "query_ir_device";
        /**
         * 删除红外遥控方案
         */
        String DELETE_IR_DEVICE = "delete_ir_device";
        /**
         * 重命名红外遥控方案
         */
        String RENAME_IR_DEVICE = "rename_ir_device";
        /**
         * 控制转发命令
         */
        String CONTROL_IR_DEVICE = "control_ir_device";
        /**
         * 删除方案中特定按键
         */
        String DELETE_IR_DEVICE_KEY = "delete_ir_device_key";
        /**
         * 学习遥控方案——进入按键学习模式
         */
        String LEARN_IR_DEVICE_KEY = "learn_ir_device_key";
        /**
         * 学习遥控方案——新建自定义遥控器
         */
        String CREATE_IR_DEVICE = "create_ir_device";
        /**
         * 一键匹配遥控方案——进入空调对码模式
         */
        String PAIR_IR_REMOTECODE = "pair_ir_remotecode";
        /**
         * 手动匹配遥控方案——测试码获取
         */
        String QUERY_IR_TESTCODE = "query_ir_testcode";
        /**
         * 手动匹配/一键匹配遥控方案——绑定码库方案
         */
        String BIND_IR_REMOTECODE = "bind_ir_remotecode";
        /**
         * 查询光头强的按键名称
         */
        String QUERY_REMOTE_LED_NAME = "query_remote_led_name";
        /**
         * 设置光头强的按键名称
         */
        String SETTING_REMOTE_LED_NAME = "setting_remote_led_name";
        /**
         * 下载码库方案至红外转发器
         */
        String LOCAL_IR_DEVICE_DOWNLOAD = "local_ir_device_download";
        /**
         * 本地遥控方案——删除方案
         */
        String LOCAL_IR_DEVICE_DELETE = "local_ir_device_delete";
        /**
         * 删除房间
         */
        String DELETE_LOCATION = "delete_location";
        /**
         * 修改位置
         */
        String UPDATE_LOCATION = "update_location";
        /**
         * 删除房间内的节点
         */
        String DELETE_DEVICE_LOCATION = "delete_device_location";
        /**
         * 第一次校验交换token
         */
        String INIT = "init";
        /**
         * 第二次校验交换token
         */
        String INIT_SECOND = "init_second";
        /**
         * 删除设备
         */
        String DELETE_DEVICE = "delete_device";
    }


    /**
     * 设备类型
     */
    interface NodeType {

        //只使用于云版本
        String IS_GROUP = "80";
        String IS_SINGLE = "00";
        String IS_LAMP = "1";
        String IS_SIMPLE = "01";
        String IS_COOL = "02";
        String IS_COLOUR = "03";
        String IS_SENSOR = "11";
        String IS_ALS_SENSOR = "1";
        String IS_WATER_SENSOR = "2";
        String IS_RADAR_SENSOR = "3";
        String IS_CO_SENSOR = "4";
    }

    /**
     * activity intent传输的key
     * handler message.what值
     */
    interface TransKey {
        /**
         * 传递键，情景选灯后传递到参数设置的页面，单节点
         */
        String CHOOSED_SGLNODE = "choosed_sglnode";

        /**
         * 传递键，情景选灯后传递到参数设置的页面，组节点
         */
        String CHOOSED_GROUPNODE = "choosed_groupnode";

        /**
         * 传递键，情景选灯后传递到参数设置的页面，组内的单节点
         */
        String CHOOSED_SIGNODE = "choosed_signode";


        /**
         * 键，新建情景的时候选好的行为列表
         */
        String ACTION_FC = "scene_fc";
        /**
         * 传递键，用于查看场景详细，与创建场景选完节点后传递action信息
         */
        String CLD_SCENACTS = "CLSenACS";
        /**
         * 传递键，用于开始创建场景时过滤灯类型节点后传递给选灯界面的DCforCtrl列表
         */
        String CLD_SCENDCFCS = "CLSenDCFCS";
        /**
         * 传递键，用于开始创建场景时过滤组类型节点后传递给选灯界面的DCGrouforCtrl列表
         */
        String CLD_SCENDGCFCS = "CLSenDGFCS";
        /**
         * 键，传递SceneFC
         */
        String CLD_SCENEFC = "sceneFC";
        /**
         * 新建位置
         */
        int CREAT_POSITON = 1;
        /**
         * 用于传递
         */
        String CHANGE_IMG = "change_img";

        /**
         * 用于传递position序列化
         */
        String POSITION = "position";
        String DEVEICE_SERID = "deveice_serid";
        /**
         * 传递新增加的位置节点
         */
        String ADD_NODE_POSITION = "add_node_position";
        String TRANS_NODE_POSITION = "trans_node_position";
        String POSITION_SER = "position_ser";
        String TRANS_LAMP_GROUP_ADD = "trans_lamp_group_add";
        String TRANS_LAMP = "trans_lamp";
        String TRANS_LAMP_GROUP = "trans_lamp_group";
        String TRANS_SCENES = "trans_scenes";
        String TRANS_LOCATION = "trans_location";
        String TRANS_LAMP_HISTORY = "trans_lamp_history";
        String TRANS_CURTAIN_POSITION = "trans_curtain_position";
        String TRANS_SMART_LOCK = "trans_smart_lock";
        String TRANS_PIC_URL = "trans_pic_url";
        String TRANS_CAMERA_INFO = "trans_camera_info";
        String LAST_OBOX = "lastObox";
        String OBOX_PSW = "obox_psw";
        String TRANS_FILE_URL = "trans_file_url";
    }

    /**
     * activityforResult传递键
     */
    interface ResultKey {
        /**
         * forresult键，选择节点
         */
        int CHOOSE_ACTION = 0;
        /**
         * forresult键,设置行为状态
         */
        int ACTION_STATUS = 1;
        /**
         * forresult键,设置情景条件   传感器 一般 定时
         */
        int SET_CONDITION = 2;
        /**
         * forresult键,点击情景详情的任何修改
         */
        int EVEN_MODIFY_SCENE = 3;
        int ADD_GROUP_DEVICE = 4;
        int ADD_PERMISSION_DEVICE = 5;
        int ADD_PERMISSION_SCENE = 6;
        String KEY = "json";
    }

}