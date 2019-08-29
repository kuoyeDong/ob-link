package com.onbright.oblink.cloud.handler.scenehandler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.onbright.oblink.MathUtil;
import com.onbright.oblink.R;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.local.bean.ObSensor;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

import java.util.List;

/**
 * 解析场景condition语意话内容主要用于展示
 *
 * @author dky
 * 2019/8/7
 */
public class ParseCondition {

    /**
     * 服务器模式解析condition显示
     *
     * @param condition 服务器模式的condition
     * @return 语意话文字
     */
    public List<Integer> getCdtShowMsg(Condition condition) {
        String cdtdetial = null;
        int conditionType = Integer.parseInt(condition.getCondition_type(), 16);
        switch (conditionType) {
            /*定时场景*/
            case Condition.TIMING:
                cdtdetial = onTime(condition);
                break;
            /*联动场景*/
            case Condition.SENSOR:
                cdtdetial = onLink(condition);
                break;
            /*遥控器场景*/
            case Condition.REMOTE:
                cdtdetial = onRemote(condition);
                break;
            /*门锁，指纹机*/
            case Condition.FINGERPRINT_MACHINE:
                if (condition.getDevice_type() != null && Integer.parseInt(condition.getDevice_type(), 16) == OBConstant.NodeType.SMART_LOCK) {
                    cdtdetial = onLock(condition);
                } else {
                    cdtdetial = onFingerMachine(condition, true);
                }
                break;
        }
        return cdtdetial;
    }

    /**
     * 门锁
     *
     * @param context   un
     * @param condition 条件
     * @param space     un
     * @return un
     */
    private List<Integer> onLock(Context context, Condition condition, String space) {
        String conditionStr = null;
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
                        conditionStr = context.getString(R.string.lock_user) + pinVal;
                        break;
                    case 0x52:/*2个字节等于，普通用户,低字节在前，高字节在后*/
                        pinVal = Integer.valueOf(conditionVal.substring(6, 8), 16) + (Integer.valueOf(conditionVal.substring(8, 10), 16) << 8);
                        conditionStr = context.getString(R.string.lock_user) + pinVal;
                        break;
                    case 0x51:/*2个字节大于，远程用户*/
                        conditionStr = context.getString(R.string.remote_open_lock);
                        break;
                }
                break;
            /*卡*/
            case 0xcd:
                conditionStr = context.getString(R.string.card);
                break;
        }
        return context.getString(R.string.smart_lock) + space + condition.getConditionID() + conditionStr;
    }

    /**
     * 指纹机
     */
    private List<Integer> onFingerMachine(Context context, Condition condition, String space, boolean isCloud) {
        return context.getString(R.string.finger_print_machine) +
                space + condition.getConditionID() + context.getString(R.string.pin) +
                (isCloud ? condition.getCondition() : Integer.parseInt(condition.getCondition().substring(4, 6) +
                        condition.getCondition().substring(2, 4), 16))
                + context.getString(R.string.pressed);
    }

//    /**
//     * 解析本地登录模式场景内条件信息
//     *
//     * @param context        上下文
//     * @param sceneCondition 本地场景的条件
//     */
//    public List<Integer> getLocalShowMsg(Context context, SceneCondition sceneCondition, String key) {
//        String space = ": ";
//        String cdtdetial = null;
//        int conditionType = sceneCondition.getconditionType();
//        Condition condition = new Condition();
//        if (sceneCondition.getCondition(key) == null) {
//            return "";
//        }
//        condition.setCondition(Transformation.byteArryToHexString(sceneCondition.getCondition(key)));
//        if (sceneCondition instanceof ObNode) {
//            ObNode os = (ObNode) sceneCondition;
//            condition.setDevice_type(Transformation.byte2HexString((byte) os.getParentType()));
//            condition.setDevice_child_type(Transformation.byte2HexString((byte) os.getType()));
//            condition.setConditionID(os.getNodeId());
//        }
//        switch (conditionType) {
//            /*定时场景*/
//            case SceneCondition.TIMING:
//                cdtdetial = onTime(context, condition, space);
//                break;
//            /*联动场景*/
//            case SceneCondition.SENSOR:
//                cdtdetial = onLink(context, condition, space);
//                break;
//            /*遥控器场景*/
//            case SceneCondition.CONTROL:
//                cdtdetial = onRemote(context, condition, space);
//                break;
//        }
//        return cdtdetial;
//    }

    @NonNull
    private List<Integer> onRemote(Context context, Condition condition, String space) {
        String item;
        String cdtdetial;
        item = context.getString(R.string.remote_contidion);
        String contStr = condition.getCondition();
        String remoteSer = condition.getCondition().substring(0, 10);
        if (contStr.length() == 16) {
            int remortBtnCode = Integer.parseInt(contStr.substring(14), 16);
            String remotBtnStr = null;
            switch (remortBtnCode) {
                case 1:
                    remotBtnStr = context.getString(R.string.remote_btn_1);
                    break;
                case 2:
                    remotBtnStr = context.getString(R.string.remote_btn_2);
                    break;
                case 4:
                    remotBtnStr = context.getString(R.string.remote_btn_3);
                    break;
                case 8:
                    remotBtnStr = context.getString(R.string.remote_btn_4);
                    break;
            }
            cdtdetial = item + space + context.getString(R.string.remote_contidion) + remoteSer + remotBtnStr;
        } else {
            cdtdetial = item + space + context.getString(R.string.remote_contidion) + remoteSer + context.getString(R.string.get_remote_crash);
        }
        return cdtdetial;
    }

    @NonNull
    private List<Integer> onLink(Context context, Condition condition, String space) {
        String item;
        String cdtdetial;
        item = context.getString(R.string.link_condition);
        String cdtName = condition.getConditionID();
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        String cdtStateStr = null;
        int ptype = Integer.parseInt(condition.getDevice_type(), 16);
        int type = Integer.parseInt(condition.getDevice_child_type(), 16);
        String cdtTypeName = new GetIconFromType().getTypeName(context, ptype, type);
        switch (ptype) {
            case OBConstant.NodeType.IS_SENSOR:
                switch (type) {
                    case OBConstant.NodeType.ALS:
                        break;
                    case OBConstant.NodeType.FLOOD:
                        if (cdtStateCode == 0) {
                            cdtStateStr = context.getString(R.string.flood_lev_0);
                        } else {
                            cdtStateStr = context.getString(R.string.flood_lev_1);
                        }
                        break;
                    case OBConstant.NodeType.RADAR:
                    case OBConstant.NodeType.HOTEL_RADAR:
                    case OBConstant.NodeType.VR_RADAR:
                        if (cdtStateCode == 0) {
                            cdtStateStr = context.getString(R.string.radar_lev_0);
                        } else {
                            cdtStateStr = context.getString(R.string.radar_lev_1);
                        }
                        break;
                    case OBConstant.NodeType.CO:
                        break;
                    case OBConstant.NodeType.ENVIRONMENTAL:
                        break;
                    case OBConstant.NodeType.BODY:
                    case OBConstant.NodeType.RED_SENSOR:
                    case OBConstant.NodeType.DC_RED_SENSOR:
                        if (cdtStateCode == 0) {
                            cdtStateStr = context.getString(R.string.radar_lev_0);
                        } else {
                            cdtStateStr = context.getString(R.string.radar_lev_1);
                        }
                        break;
                    case OBConstant.NodeType.PM2_5:
                        break;
                    case OBConstant.NodeType.POWER_CHECK:
                        break;

                    case OBConstant.NodeType.LIGHT_SENSOR:
                        if (cdtStateCode == 1) {
                            cdtStateStr = context.getString(R.string.light_lev_1);
                        } else if (cdtStateCode == 2) {
                            cdtStateStr = context.getString(R.string.light_lev_2);
                        } else if (cdtStateCode == 3) {
                            cdtStateStr = context.getString(R.string.light_lev_3);
                        } else if (cdtStateCode == 4) {
                            cdtStateStr = context.getString(R.string.light_lev_4);
                        }
                        break;
                    case OBConstant.NodeType.TEMP_HUMID_SENSOR:
                        byte[] cdtBytes = Transformation.hexString2Bytes(condition.getCondition());
                        byte tempRule = cdtBytes[0];
                        int tempVal = MathUtil.validByte(cdtBytes[1]);
                        byte humidRule = cdtBytes[2];
                        int humidVal = MathUtil.validByte(cdtBytes[3]);
                        StringBuilder sb = new StringBuilder();
                        if (tempVal != 0xff) {
                            sb.append(context.getString(R.string.temp)).append(math2Str(context, MathUtil.validByte(tempRule))).append(tempVal - 30);
                        }
                        if (!(humidRule == 0 && humidVal == 0)) {
                            sb.append(context.getString(R.string.hum)).append(math2Str(context, MathUtil.validByte(humidRule))).append(humidVal);
                        }
                        if (sb.length() == 0) {
                            sb.append(context.getString(R.string.not_set));
                        }
                        cdtStateStr = sb.toString();
                        break;
                    case OBConstant.NodeType.SMOKE_SENSOR:
                        if (cdtStateCode == 0) {
                            cdtStateStr = context.getString(R.string.smoke_lev_0);
                        } else {
                            cdtStateStr = context.getString(R.string.smoke_lev_1);
                        }
                        break;
                    case OBConstant.NodeType.DOOR_WINDOW_MAGNET:
                        if (cdtStateCode == 0) {
                            cdtStateStr = context.getString(R.string.door_magnet_lev_0);
                        } else {
                            cdtStateStr = context.getString(R.string.door_magnet_lev_1);
                        }
                        break;
                    case OBConstant.NodeType.ELECTRIC_CARD:
                        switch (cdtStateCode) {
                            case 0:
                                cdtStateStr = context.getString(R.string.card_inserted);
                                break;
                            case 1:
                                cdtStateStr = context.getString(R.string.card_removed);
                                break;
                            case 2:
                                cdtStateStr = context.getString(R.string.power_off);
                                break;
                        }
                        break;
                    case OBConstant.NodeType.DC_BODY_ALS:
                    case OBConstant.NodeType.AC_BODY_ALS:
                        cdtBytes = Transformation.hexString2Bytes(condition.getCondition());
                        byte bodyRule = cdtBytes[0];
                        int bodyVal = MathUtil.validByte(cdtBytes[1]);
                        byte lightRule = cdtBytes[2];
                        int lightVal = MathUtil.validByte(cdtBytes[3]);
                        sb = new StringBuilder();
                        if (bodyVal != 0xff) {
                            sb.append(context.getString(bodyRule == 0 ? R.string.radar_lev_0 : R.string.radar_lev_1));
                        }
                        if (!(lightRule == 0 && lightVal == 0)) {
                            sb.append(math2Str(context, MathUtil.validByte(lightRule))).append(lightVal * 20).append("%");
                        }
                        if (sb.length() == 0) {
                            sb.append(context.getString(R.string.not_set));
                        }
                        break;
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
                        if (cdtStateCode == 1) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_1);
                        } else if (cdtStateCode == 2) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_2);
                        } else if (cdtStateCode == 4) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_3);
                        } else if (cdtStateCode == 8) {
                            cdtStateStr = context.getString(R.string.scene_panel_red);
                        }
                        cdtStateStr += context.getString(R.string.bing_press);
                        break;
                    case OBConstant.NodeType.FOUR_SCENE_PANEL:
                    case OBConstant.NodeType.SIX_SCENE_PANEL:
                    case OBConstant.NodeType.SIX_SCENE_RED_PANEL:
                        if (cdtStateCode == 1) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_1);
                        } else if (cdtStateCode == 2) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_2);
                        } else if (cdtStateCode == 4) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_3);
                        } else if (cdtStateCode == 8) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_4);
                        } else if (cdtStateCode == 16) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_5);
                        } else if (cdtStateCode == 32) {
                            cdtStateStr = context.getString(R.string.scene_panel_btn_6);
                        } else if (cdtStateCode == 64) {
                            cdtStateStr = context.getString(R.string.scene_panel_red);
                        }
                        cdtStateStr += context.getString(R.string.bing_press);
                        break;
                    case OBConstant.NodeType.CURTIAN_PANEL:
                    case OBConstant.NodeType.DOUBLE_CURTIAN_PANEL:
                        if (cdtStateCode == 1) {
                            cdtStateStr = context.getString(R.string.open_1);
                        } else if (cdtStateCode == 2) {
                            cdtStateStr = context.getString(R.string.stop_1);
                        } else if (cdtStateCode == 4) {
                            cdtStateStr = context.getString(R.string.close_1);
                        } else if (cdtStateCode == 8) {
                            cdtStateStr = context.getString(R.string.open_2);
                        } else if (cdtStateCode == 16) {
                            cdtStateStr = context.getString(R.string.stop_2);
                        } else if (cdtStateCode == 32) {
                            cdtStateStr = context.getString(R.string.close_2);
                        }
                        cdtStateStr += context.getString(R.string.bing_press);
                        break;
                    case OBConstant.NodeType.SOCKET:
                        cdtStateStr = context.getString(R.string.exceed) + cdtStateCode * 50 + context.getString(R.string.watt);
                        break;
                }
                break;
            case OBConstant.NodeType.SMART_FINGER:
                cdtdetial = onFingerMachine(context, condition, space, false);
                return cdtdetial;
            case OBConstant.NodeType.SMART_LOCK:
                return onLock(context, condition, space);
        }
        cdtdetial = item + space + cdtTypeName + cdtName + cdtStateStr;
        return cdtdetial;
    }

    @NonNull
    private List<Integer> onTime(Context context, Condition condition, String space) {
        String item;
        item = context.getString(R.string.time_condition);
        String time = condition.getCondition();
        /*一般时间*/
        if (time.substring(0, 2).equals("00")) {
            return onSimpleTime(item, space, time);
        }
        /*循环时间*/
        else {
            return onCircleTime(context, item, space, time);
        }
    }

    /**
     * 读取判断条件
     *
     * @param context un
     * @param rule    判断条件数据
     * @return 语义
     */
    private Integer math2Str(Context context, int rule) {
        String mathStr = null;
        switch (rule) {
            case 0x49:
                mathStr = context.getString(R.string.greater_than);
                break;
            case 0x4c:
                mathStr = context.getString(R.string.less_than);
                break;
            case 0x4a:
                mathStr = context.getString(R.string.equal_to);
                break;
            case 0x4b:
                mathStr = context.getString(R.string.greater_than_or_equal_to);
                break;
            case 0x4e:
                mathStr = context.getString(R.string.less_than_or_equal_to);
                break;
        }
        return mathStr;
    }

    @NonNull
    private List<Integer> onCircleTime(Context context, String item, String space, String time) {
        String cdtdetial;
        byte circle = (byte) Integer.parseInt(time.substring(0, 2), 16);
        StringBuilder sb = new StringBuilder();
        if (MathUtil.byteIndexValid(circle, 7) != 0) {
            sb.append(context.getString(R.string.everday));
        } else {
            mathWeek(context, circle, sb);
        }
        String hour = apendzero(String.valueOf(Integer.parseInt(time.substring(10, 12), 16)));
        String min = apendzero(String.valueOf(Integer.parseInt(time.substring(12, 14), 16)));
        sb.append(hour).append(":").append(min);
        cdtdetial = item + space + sb.toString();
        return cdtdetial;
    }

    private void mathWeek(Context context, byte circle, StringBuilder sb) {
        boolean is0 = false, is1 = false, is2 = false, is3 = false, is4 = false, is5 = false, is6 = false;
        for (int i = 0; i < 7; i++) {
            if (MathUtil.byteIndexValid(circle, i) != 0) {
                switch (i) {
                    case 6:
                        is6 = true;
                        sb.append(context.getString(R.string.sat));
                        break;
                    case 5:
                        is5 = true;
                        sb.append(context.getString(R.string.fri));
                        break;
                    case 4:
                        is4 = true;
                        sb.append(context.getString(R.string.thu));
                        break;
                    case 3:
                        is3 = true;
                        sb.append(context.getString(R.string.wed));
                        break;
                    case 2:
                        is2 = true;
                        sb.append(context.getString(R.string.tue));
                        break;
                    case 1:
                        is1 = true;
                        sb.append(context.getString(R.string.mon));
                        break;
                    case 0:
                        is0 = true;
                        sb.append(context.getString(R.string.sun));
                        break;
                }
            }
        }
        if ((is0 && is6) && (!(is1 || is2 || is3 || is4 || is5))) {
            sb.delete(0, sb.length());
            sb.append(context.getString(R.string.weekend));
        } else if ((is1 && is2 && is3 && is4 && is5) && (!(is0 || is6))) {
            sb.delete(0, sb.length());
            sb.append(context.getString(R.string.workday));
        }
    }

    @NonNull
    private List<Integer> onSimpleTime(String item, String space, String time) {
        String cdtdetial;
        String year = "20" + apendzero(String.valueOf(Integer.parseInt(time.substring(4, 6), 16)));
        String month = apendzero(String.valueOf(Integer.parseInt(time.substring(6, 8), 16)));
        String day = apendzero(String.valueOf(Integer.parseInt(time.substring(8, 10), 16)));
        String hour = apendzero(String.valueOf(Integer.parseInt(time.substring(10, 12), 16)));
        String min = apendzero(String.valueOf(Integer.parseInt(time.substring(12, 14), 16)));
        cdtdetial = item + space + year + "-" + month + "-" + day + "  " + hour + ":" + min;
        return cdtdetial;
    }
}