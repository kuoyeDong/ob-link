package com.onbright.oblink.cloud.handler.scenehandler;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析场景condition语意话内容，本类做逻辑换算，使用者可根据需要复写拆解表达语意
 *
 * @author dky
 * 2019/8/7
 */
public class ParseCondition {

    /**
     * 服务器模式解析condition显示
     *
     * @param condition 服务器模式的condition
     * @return 语意话文字编码和参数，详见各实现方法注释
     */
    public List<Object> getCdtShowMsg(Condition condition) {
        List<Object> expressions = null;
        int conditionType = Integer.parseInt(condition.getCondition_type(), 16);
        switch (conditionType) {
            case Condition.TIMING:
                expressions = onTime(condition);
                break;
            case Condition.SENSOR:
                expressions = onLink(condition);
                break;
            case Condition.REMOTE:
                expressions = onRemote(condition);
                break;
            case Condition.FINGERPRINT_MACHINE:
                if (condition.getDevice_type() != null &&
                        Integer.parseInt(condition.getDevice_type(), 16) == OBConstant.NodeType.SMART_LOCK) {
                    expressions = onLock(condition);
                } else {
                    expressions = onFingerMachine(condition, true);
                }
                break;
        }
        return expressions;
    }

    /**
     * 门锁
     *
     * @param condition 条件
     * @return 集合成员，如是锁用户指纹方式则有成员pin值，临时用户开锁和卡开锁则无pin值
     */
    private List<Object> onLock(Condition condition) {
        List<Object> expressions = new ArrayList<>();
        String conditionVal = condition.getCondition();
        String conditionType = conditionVal.substring(2, 4);
        int conditionTypeInt = Integer.valueOf(conditionType, 16);
        switch (conditionTypeInt) {
            /*指纹开锁*/
            case 0xc3:
                int pinIndex = Integer.valueOf(conditionVal.substring(4, 6), 16);
                switch (pinIndex) {
                    case 0x4a:/*1个字节等于，普通用户*/
                        int pinVal = Integer.valueOf(conditionVal.substring(6, 8), 16);
                        expressions.add(ConditionExpression.LOCK_USER);
                        expressions.add(pinVal);
                        break;
                    case 0x52:/*2个字节等于，普通用户,低字节在前，高字节在后*/
                        pinVal = Integer.valueOf(conditionVal.substring(6, 8), 16) + (Integer.valueOf(conditionVal.substring(8, 10), 16) << 8);
                        expressions.add(ConditionExpression.LOCK_USER);
                        expressions.add(pinVal);
                        break;
                    case 0x51:/*2个字节大于，远程用户*/
                        expressions.add(ConditionExpression.LOCK_TEMP_USER);
                        break;
                }
                break;
            /*卡*/
            case 0xcd:
                expressions.add(ConditionExpression.LOCK_CARD);
                break;
        }
        return expressions;
    }

    /**
     * 指纹机
     *
     * @param condition 条件对象
     * @param isCloud   是否云解析
     * @return 只包含int用户pin值
     */
    private List<Object> onFingerMachine(Condition condition, boolean isCloud) {
        List<Object> expressions = new ArrayList<>();
        expressions.add(isCloud ? Integer.valueOf(condition.getCondition()) : Integer.parseInt(condition.getCondition().substring(4, 6) +
                condition.getCondition().substring(2, 4), 16));
        return expressions;
    }

    /**
     * 解析本地登录模式场景内条件信息
     * 本地模式即将于云模式统一
     *
     * @param sceneCondition 本地场景的条件
     */
    @Deprecated
    public List<Object> getLocalShowMsg(SceneCondition sceneCondition, String key) {
        List<Object> expressions = null;
        int conditionType = sceneCondition.getconditionType();
        Condition condition = new Condition();
        if (sceneCondition.getCondition(key) == null) {
            return null;
        }
        condition.setCondition(Transformation.byteArryToHexString(sceneCondition.getCondition(key)));
        if (sceneCondition instanceof ObNode) {
            ObNode os = (ObNode) sceneCondition;
            condition.setDevice_type(Transformation.byte2HexString((byte) os.getParentType()));
            condition.setDevice_child_type(Transformation.byte2HexString((byte) os.getType()));
            condition.setConditionID(os.getNodeId());
        }
        switch (conditionType) {
            /*定时场景*/
            case SceneCondition.TIMING:
                expressions = onTime(condition);
                break;
            /*联动场景*/
            case SceneCondition.SENSOR:
                expressions = onLink(condition);
                break;
            /*遥控器场景*/
            case SceneCondition.CONTROL:
                expressions = onRemote(condition);
                break;
        }
        return expressions;
    }

    /**
     * 解析遥控器情景对象
     *
     * @param condition 条件对象
     * @return 集合只会包含一个按钮
     */
    private List<Object> onRemote(Condition condition) {
        List<Object> expressions = new ArrayList<>();
        String contStr = condition.getCondition();
        String remoteSer = condition.getCondition().substring(0, 10);
        expressions.add(remoteSer);
        if (contStr.length() == 16) {
            int remortBtnCode = Integer.parseInt(contStr.substring(14), 16);
            switch (remortBtnCode) {
                case 1:
                    expressions.add(ConditionExpression.REMOTE_BTN_1);
                    break;
                case 2:
                    expressions.add(ConditionExpression.REMOTE_BTN_2);
                    break;
                case 4:
                    expressions.add(ConditionExpression.REMOTE_BTN_3);
                    break;
                case 8:
                    expressions.add(ConditionExpression.REMOTE_BTN_4);
                    break;
            }
        } else {
            expressions.add(ConditionExpression.REMOTE_BTN_UNKNOW);
        }
        return expressions;
    }

    /**
     * 解析传感器条件对象
     *
     * @param condition 条件对象
     * @return 参见各下级实现
     */
    private List<Object> onLink(Condition condition) {
        int ptype = Integer.parseInt(condition.getDevice_type(), 16);
        int type = Integer.parseInt(condition.getDevice_child_type(), 16);
        switch (ptype) {
            case OBConstant.NodeType.IS_SENSOR:
                switch (type) {
                    case OBConstant.NodeType.ALS:
                        break;
                    case OBConstant.NodeType.FLOOD:
                        return parseFlood(condition);
                    case OBConstant.NodeType.RADAR:
                    case OBConstant.NodeType.HOTEL_RADAR:
                    case OBConstant.NodeType.VR_RADAR:
                    case OBConstant.NodeType.BODY:
                    case OBConstant.NodeType.RED_SENSOR:
                    case OBConstant.NodeType.DC_RED_SENSOR:
                        return parseBody(condition);
                    case OBConstant.NodeType.CO:
                        break;
                    case OBConstant.NodeType.ENVIRONMENTAL:
                        break;
                    case OBConstant.NodeType.PM2_5:
                        break;
                    case OBConstant.NodeType.POWER_CHECK:
                        break;
                    case OBConstant.NodeType.LIGHT_SENSOR:
                        return parseAls(condition);
                    case OBConstant.NodeType.TEMP_HUMID_SENSOR:
                        return parseTempHumid(condition);
                    case OBConstant.NodeType.SMOKE_SENSOR:
                        return parseSmoke(condition);
                    case OBConstant.NodeType.DOOR_WINDOW_MAGNET:
                        return parseDoorWindowMagnet(condition);
                    case OBConstant.NodeType.ELECTRIC_CARD:
                        return parseElectricCard(condition);
                    case OBConstant.NodeType.DC_BODY_ALS:
                    case OBConstant.NodeType.AC_BODY_ALS:
                        return parseBodyAls(condition);
                }
                break;
            case OBConstant.NodeType.IS_OBSOCKET:
                switch (type) {
                    case OBConstant.NodeType.SINGLE_SCENE_PANEL:
                    case OBConstant.NodeType.DOUBLE_SCENE_PANEL:
                    case OBConstant.NodeType.THREE_SCENE_PANEL:
                    case OBConstant.NodeType.SINGLE_SWITCH_SCENE_PANEL:
                    case OBConstant.NodeType.DOUBLE_SWITCH_SCENE_PANEL:
                    case OBConstant.NodeType.THREE_SWITCH_SCENE_PANEL:
                    case OBConstant.NodeType.THREE_SWITCH_RED_SCENE_PANEL:
                    case OBConstant.NodeType.TWO_SWITCH_TWO_SCENE_PANEL:
                        return parseScenePanelLessThanFour(condition);
                    case OBConstant.NodeType.FOUR_SCENE_PANEL:
                    case OBConstant.NodeType.SIX_SCENE_PANEL:
                    case OBConstant.NodeType.SIX_SCENE_RED_PANEL:
                        return parseScenePanelGreaterThanThree(condition);
                    case OBConstant.NodeType.CURTIAN_PANEL:
                    case OBConstant.NodeType.DOUBLE_CURTIAN_PANEL:
                        return parseCurtianPanel(condition);
                    case OBConstant.NodeType.SOCKET:
//                        expressions.add(cdtStateCode * 50);
                        break;
                }
                break;
            case OBConstant.NodeType.SMART_FINGER:
                return onFingerMachine(condition, false);
            case OBConstant.NodeType.SMART_LOCK:
                return onLock(condition);
        }
        return null;
    }

    /**
     * 解析窗帘面板条件节点
     *
     * @param condition 条件节点
     * @return 只包含其中一个开停关按钮
     */
    private List<Object> parseCurtianPanel(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 1) {
            expressions.add(ConditionExpression.OPEN_1);
        } else if (cdtStateCode == 2) {
            expressions.add(ConditionExpression.STOP_1);
        } else if (cdtStateCode == 4) {
            expressions.add(ConditionExpression.CLOSE_1);
        } else if (cdtStateCode == 8) {
            expressions.add(ConditionExpression.OPEN_2);
        } else if (cdtStateCode == 16) {
            expressions.add(ConditionExpression.STOP_2);
        } else if (cdtStateCode == 32) {
            expressions.add(ConditionExpression.CLOSE_2);
        }
        return expressions;
    }

    /**
     * 解析超过三个情景按钮的面板条件
     *
     * @param condition 条件对象
     * @return 只会包含其中一个情景按钮
     */
    private List<Object> parseScenePanelGreaterThanThree(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 1) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_1);
        } else if (cdtStateCode == 2) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_2);
        } else if (cdtStateCode == 4) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_3);
        } else if (cdtStateCode == 8) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_4);
        } else if (cdtStateCode == 16) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_5);
        } else if (cdtStateCode == 32) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_6);
        } else if (cdtStateCode == 64) {
            expressions.add(ConditionExpression.SCENE_PANEL_RED);
        }
        return expressions;
    }

    /**
     * 解析小于四个情景按钮的面板条件
     *
     * @param condition 条件对象
     * @return int 只会包含一个按钮
     */
    private List<Object> parseScenePanelLessThanFour(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 1) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_1);
        } else if (cdtStateCode == 2) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_2);
        } else if (cdtStateCode == 4) {
            expressions.add(ConditionExpression.SCENE_PANEL_BTN_3);
        } else if (cdtStateCode == 8) {
            expressions.add(ConditionExpression.SCENE_PANEL_RED);
        }
        return expressions;
    }

    /**
     * 解析人体感应光感二合一条件
     *
     * @param condition 条件对象
     * @return int 依次为(人体感应有人无人状态或未设置表述)，(光感未设置或比较级+比较数据)
     */
    private List<Object> parseBodyAls(Condition condition) {
        List<Object> expressions = new ArrayList<>();
        byte[] cdtBytes;
        cdtBytes = Transformation.hexString2Bytes(condition.getCondition());
        byte bodyRule = cdtBytes[0];
        int bodyVal = MathUtil.validByte(cdtBytes[1]);
        byte lightRule = cdtBytes[2];
        int lightVal = MathUtil.validByte(cdtBytes[3]);
        if (bodyVal != 0xff) {
            expressions.add(bodyRule == 0 ? ConditionExpression.NO_BODY : ConditionExpression.SOME_BODY);
        } else {
            expressions.add(ConditionExpression.NOT_SET);
        }
        if (!(lightRule == 0 && lightVal == 0)) {
            expressions.add(compareExpression(MathUtil.validByte(lightRule)));
            expressions.add(lightVal);
        } else {
            expressions.add(ConditionExpression.NOT_SET);
        }
        return expressions;
    }

    /**
     * 解析插卡取电条件节点
     *
     * @param condition 条件对象
     * @return int 只会包含一种状态，插卡、取卡、取卡后断电
     */
    private List<Object> parseElectricCard(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        switch (cdtStateCode) {
            case 0:
                expressions.add(ConditionExpression.CARD_INSERTED);
                break;
            case 1:
                expressions.add(ConditionExpression.CARD_REMOVED);
                break;
            case 2:
                expressions.add(ConditionExpression.POWER_OFF);
                break;
        }
        return expressions;
    }

    /**
     * 解析门窗磁条件节点
     *
     * @param condition 条件对象
     * @return int 只会包含一种状态，门窗磁打开或关闭
     */
    private List<Object> parseDoorWindowMagnet(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 0) {
            expressions.add(ConditionExpression.DOOR_WINDOW_CLOSE);
        } else {
            expressions.add(ConditionExpression.DOOR_WINDOW_OPEN);
        }
        return expressions;
    }

    /**
     * 解析烟雾传感器条件节点
     *
     * @param condition 条件对象
     * @return int 只会包含一种状态，有烟雾、无烟雾
     */
    private List<Object> parseSmoke(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 0) {
            expressions.add(ConditionExpression.NO_SMOKE);
        } else {
            expressions.add(ConditionExpression.HAVE_SMOKE);
        }
        return expressions;
    }

    /**
     * 解析温度湿度传感器条件
     *
     * @param condition 条件对象
     * @return 温度相关(1 - 2字节)(未设置或比较级 、 比较数据)、湿度相关(1-2字节)(未设置或比较级、比较数据)
     */
    private List<Object> parseTempHumid(Condition condition) {
        List<Object> expressions = new ArrayList<>();
        byte[] cdtBytes = Transformation.hexString2Bytes(condition.getCondition());
        byte tempRule = cdtBytes[0];
        int tempVal = MathUtil.validByte(cdtBytes[1]);
        byte humidRule = cdtBytes[2];
        int humidVal = MathUtil.validByte(cdtBytes[3]);
        if (tempVal != 0xff) {
            expressions.add(compareExpression(MathUtil.validByte(tempRule)));
            expressions.add(tempVal - 30);
        } else {
            expressions.add(ConditionExpression.NOT_SET);
        }
        if (!(humidRule == 0 && humidVal == 0)) {
            expressions.add(MathUtil.validByte(humidRule));
            expressions.add(humidVal);
        } else {
            expressions.add(ConditionExpression.NOT_SET);
        }
        return expressions;
    }

    /**
     * 解析光感条件
     *
     * @param condition 条件对象
     * @return int 只会包含对应的光感等级
     */
    private List<Object> parseAls(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 1) {
            expressions.add(ConditionExpression.LIGHT_LEV_1);
        } else if (cdtStateCode == 2) {
            expressions.add(ConditionExpression.LIGHT_LEV_2);
        } else if (cdtStateCode == 3) {
            expressions.add(ConditionExpression.LIGHT_LEV_3);
        } else if (cdtStateCode == 4) {
            expressions.add(ConditionExpression.LIGHT_LEV_4);
        }
        return expressions;
    }

    /**
     * 解析人体感应相关的条件
     *
     * @param condition 条件对象
     * @return int 只会包含有人、无人
     */
    private List<Object> parseBody(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 0) {
            expressions.add(ConditionExpression.NO_BODY);
        } else {
            expressions.add(ConditionExpression.SOME_BODY);
        }
        return expressions;
    }

    /**
     * 解析水浸条件
     *
     * @param condition 条件对象
     * @return int 只会包含湿或者干
     */
    private List<Object> parseFlood(Condition condition) {
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        List<Object> expressions = new ArrayList<>();
        if (cdtStateCode == 0) {
            expressions.add(ConditionExpression.DRY);
        } else {
            expressions.add(ConditionExpression.WET);
        }
        return expressions;
    }

    /**
     * 解析时间条件
     *
     * @param condition 时间条件对象
     * @return 参见下级实现
     */
    private List<Object> onTime(Condition condition) {
        String time = condition.getCondition();
        /*一般时间*/
        if (time.substring(0, 2).equals("00")) {
            return onSimpleTime(time);
        }
        /*循环时间*/
        else {
            return onCircleTime(time);
        }
    }

    /**
     * 读取判断条件
     *
     * @param rule 判断条件数据
     * @return 语义
     */
    private Integer compareExpression(int rule) {
        switch (rule) {
            case 0x49:
                return ConditionExpression.GREATER_THAN;
            case 0x4c:
                return ConditionExpression.LESS_THAN;
            case 0x4a:
                return ConditionExpression.EQUAL_TO;
            case 0x4b:
                return ConditionExpression.GREATER_THAN_OR_EQUAL_TO;
            case 0x4e:
                return ConditionExpression.LESS_THAN_OR_EQUAL_TO;
            default:
                return ConditionExpression.EQUAL_TO;
        }
    }

    /**
     * @param time 时间条件
     * @return 集合顺序，前7个boolean值星期几是否执行（7,1,2,3,4,5,6），后两个int时、分
     */
    private List<Object> onCircleTime(String time) {
        List<Object> expressions = new ArrayList<>();
        byte circle = (byte) Integer.parseInt(time.substring(0, 2), 16);
        if (MathUtil.byteIndexValid(circle, 7) != 0) {
            expressions.add(true);
            expressions.add(true);
            expressions.add(true);
            expressions.add(true);
            expressions.add(true);
            expressions.add(true);
            expressions.add(true);
        } else {
            for (int i = 0; i < 7; i++) {
                if (MathUtil.byteIndexValid(circle, i) == 1) {
                    expressions.add(true);
                } else {
                    expressions.add(false);
                }
            }
        }
        int hour = Integer.parseInt(time.substring(10, 12), 16);
        int min = Integer.parseInt(time.substring(12, 14), 16);
        expressions.add(hour);
        expressions.add(min);
        return expressions;
    }

    /**
     * 解析一般时间条件对象
     *
     * @param time 时间条件对象
     * @return int   年月日时分
     */
    private List<Object> onSimpleTime(String time) {
        List<Object> expressions = new ArrayList<>();
        int year = Integer.parseInt(time.substring(4, 6), 16);
        int month = Integer.parseInt(time.substring(6, 8), 16);
        int day = Integer.parseInt(time.substring(8, 10), 16);
        int hour = Integer.parseInt(time.substring(10, 12), 16);
        int min = Integer.parseInt(time.substring(12, 14), 16);
        expressions.add(year);
        expressions.add(month);
        expressions.add(day);
        expressions.add(hour);
        expressions.add(min);
        return expressions;
    }

    /**
     * 场景语意话参考
     */
    public interface ConditionExpression {
        /**
         * 门锁用户
         */
        int LOCK_USER = 0;
        /**
         * 门锁临时用户
         */
        int LOCK_TEMP_USER = 1;
        /**
         * 门锁刷卡
         */
        int LOCK_CARD = 2;
        /**
         * 遥控器按钮1
         */
        int REMOTE_BTN_1 = 3;

        /**
         * 遥控器按钮2
         */
        int REMOTE_BTN_2 = 4;
        /**
         * 遥控器按钮3
         */
        int REMOTE_BTN_3 = 5;
        /**
         * 遥控器按钮4
         */
        int REMOTE_BTN_4 = 6;
        /**
         * 遥控器按钮未知
         */
        int REMOTE_BTN_UNKNOW = 7;
        int DRY = 8;
        int WET = 9;
        int NO_BODY = 10;
        int SOME_BODY = 11;
        int LIGHT_LEV_1 = 12;
        int LIGHT_LEV_2 = 13;
        int LIGHT_LEV_3 = 14;
        int LIGHT_LEV_4 = 15;
        int NOT_SET = 16;
        int NO_SMOKE = 17;
        int HAVE_SMOKE = 18;
        int DOOR_WINDOW_CLOSE = 19;
        int DOOR_WINDOW_OPEN = 20;
        int CARD_INSERTED = 21;
        int CARD_REMOVED = 22;
        int POWER_OFF = 23;
        int SCENE_PANEL_BTN_1 = 24;
        int SCENE_PANEL_BTN_2 = 25;
        int SCENE_PANEL_BTN_3 = 26;
        int SCENE_PANEL_BTN_4 = 27;
        int SCENE_PANEL_BTN_5 = 28;
        int SCENE_PANEL_BTN_6 = 29;
        int SCENE_PANEL_RED = 30;
        int OPEN_1 = 31;
        int STOP_1 = 32;
        int CLOSE_1 = 33;
        int OPEN_2 = 34;
        int STOP_2 = 35;
        int CLOSE_2 = 36;
        int GREATER_THAN = 37;
        int LESS_THAN = 38;
        int EQUAL_TO = 39;
        int GREATER_THAN_OR_EQUAL_TO = 40;
        int LESS_THAN_OR_EQUAL_TO = 41;
    }
}