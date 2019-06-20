package com.onbright.oblink.cloud.net;

/**
 * 网络请求参数
 * Created by adolf_dong on 2016/1/7.
 */
public interface CloudConstant {
    interface Source {
        String Common = "https://BDCloud.on-bright.com/common";
    }

    /**
            * 返回状态，正常，失败，异常
    */
    interface ResponState {
        int State_OK = 0;
        int State_F = 1;
        int State_E = 2;
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
        String ROOTNAME = "root_name";
        String ADMINNAME = "admin_name";
        String ACCESS_TOKEN = "access_token";
        String PASS_WORD = "pwd";
        String MODIFY_TYPE = "modify_type";
        String LICENSE = "license";
        String OBOX = "obox";
        String WEIGHT = "weight";
        String YS_OPTIVATE = "ys_optivate";
        String OBOX_SERIAL_ID = "obox_serial_id";
        String DEVICE_SERIAL_ID = "serialId";
        String TYPE = "type";
        String COVER = "cover";
        String OPERATION = "operation";
        String GROUP_STYLE = "group_style";
        String FROM_DATE = "from_date";
        String TO_DATE = "to_date";
        String FROM_DATA= "from_data";
        String TO_DATA = "to_data";
        String OBOX_OLD_PWD = "obox_old_pwd";
        String OBOX_NEW_PWD = "obox_new_pwd";
        String OBOX_NEW_NAME = "obox_name";
        String NODES = "nodes";
        String STATES = "state";
        String TIME_OUT = "timeout";
        String NAME = "name";
        String DEVICES = "devices";
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
        String SCENE_STATUS= "scene_status";
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
        String VALIDATE_CODE ="validateCode";
        String FORCE_DELETE = "force_delete";
        String APP_KEY = "appKey";
        String ZONE = "zone";
        String CODE = "code";
        String PIN = "pin";
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
        /**
         * 查询位置绑定的情景
         */
        String QUERY_SCENE_LOCATION = "query_scene_location";
        String SET_SCENE_LOCATION = "set_scene_location";

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

        String DISABLE_SCENE = "00";
        String ENABLE_SCENE = "01";
        String ACTION_SCENE = "02";
        String DELETE_SCENE = "03";
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
         * 传递健用于传递创建position之后传回给positionFragment
         */
        String CREAT_POSITION = "creat_position";
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
        String ADD_NODE_POSITION ="add_node_position" ;
        String TRANS_NODE_POSITION ="trans_node_position" ;
        String POSITION_SER ="position_ser" ;
        String TRANS_LAMP_POSITION ="trans_lamp_position" ;
        String TRANS_LAMP_GROUP_ADD ="trans_lamp_group_add" ;
        String TRANS_LAMP="trans_lamp" ;
        String TRANS_FINGER_USER="trans_finger_user" ;
        String TRANS_LAMP_GROUP="trans_lamp_group" ;
        String TRANS_LAMP_HISTORY="trans_lamp_history" ;
        String TRANS_CURTAIN_POSITION ="trans_curtain_position" ;
        String TRANS_SMART_LOCK ="trans_smart_lock" ;
        String TRANS_PIC_URL="trans_pic_url" ;
        String TRANS_CAMERA_INFO="trans_camera_info" ;
        String LAST_OBOX = "lastObox";
        String OBOX_PSW = "obox_psw";
        String TRANS_FILE_URL="trans_file_url" ;
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
        String KEY = "json";
    }
    /**
     * {@link #ADMIN}与的主要区别在于其可以创建楼层概念，楼宇概念可以用来区分智能楼宇和智能家居
     * 服务器登录后详细的用户类型
     */
    interface CloudDitalMode {
        /**
         * 超级管理员，拥有所有权限
         */
        String SUPERROOT = "00";
        /**
         * 总管理员，拥有创建楼宇管理员权限，次于超级管理员的所有权限，当有授权码的时候就可以创建ADMINOFBD
         */
        String ROOT = "01";
        /**
         * 楼宇管理员，权限:创建guest，创建楼层，创建房间，增加位置信息，修改组节点关系，创建情景,设备升级权限，
         */
        String ADMIN = "02";

        /**
         * 访客，拥有节点控制，组控制，情景使能，位置信息读取权限
         */
        String GUEST = "03";
    }
}