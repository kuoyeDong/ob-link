package com.onbright.oblink;

import android.content.Context;
import android.support.annotation.NonNull;

import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.local.bean.ObSensor;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Transformation;

/**
 * 解析服务器场景condition内容主要用于展示
 * Created by adolf_dong on 2016/11/11.
 */
public class ParseCondition {

    public String getCdtShowMsg(Context context, Condition condition) {
        String space = ": ";
        String cdtdetial = null;
        int conditionType = Integer.parseInt(condition.getCondition_type(), 16);
        switch (conditionType) {
            /*定时场景*/
            case 0:
                cdtdetial = onTime(context, condition, space);
                break;
            /*联动场景*/
            case 1:
                cdtdetial = onLink(context, condition, space);
                break;
            /*遥控器场景*/
            case 2:
                cdtdetial = onRemote(context, condition, space);
                break;
        }
        return cdtdetial;
    }

    /**
     * 解析本地登录模式场景内条件信息
     *
     * @param context        上下文
     * @param sceneCondition 本地场景的条件
     */
    public String getLocalShowMsg(Context context, SceneCondition sceneCondition, String key) {
        String space = ": ";
        String cdtdetial = null;
        int conditionType = sceneCondition.getconditionType();
        Condition condition = new Condition();
        if (sceneCondition.getCondition(key) == null) {
            return "";
        }
        condition.setCondition(Transformation.byteArryToHexString(sceneCondition.getCondition(key)));
        if (sceneCondition instanceof ObSensor) {
            condition.setDevice_type(Transformation.byte2HexString((byte) ((ObSensor) sceneCondition).getParentType()));
            condition.setDevice_child_type(Transformation.byte2HexString((byte) ((ObSensor) sceneCondition).getType()));
        }
        switch (conditionType) {
           /*定时场景*/
            case SceneCondition.TIMING:
                cdtdetial = onTime(context, condition, space);
                break;
            /*联动场景*/
            case SceneCondition.SENSOR:
                cdtdetial = onLink(context, condition, space);
                break;
            /*遥控器场景*/
            case SceneCondition.CONTROL:
                cdtdetial = onRemote(context, condition, space);
                break;
        }
        return cdtdetial;
    }

    @NonNull
    private String onRemote(Context context, Condition condition, String space) {
        String item;
        String cdtdetial;
        item = context.getString(R.string.remote_contidion);
        String contStr = condition.getCondition();
        String remoteSer = condition.getCondition().substring(0, 10);
        if (contStr.length() == 16) {
            int remortBtnCode = Integer.parseInt(condition.getCondition().substring(14), 16);
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
    private String onLink(Context context, Condition condition, String space) {
        String item;
        String cdtdetial;
        item = context.getString(R.string.link_condition);
        String cdtTypeName = null;
        String cdtName = condition.getConditionID();
        int cdtStateCode = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
        String cdtStateStr = null;
        int type = Integer.parseInt(condition.getDevice_child_type(), 16);
        switch (type) {
            case OBConstant.NodeType.ALS:
                cdtTypeName = context.getString(R.string.sensor_als);
                break;
            case OBConstant.NodeType.FLOOD:
                cdtTypeName = context.getString(R.string.sensor_water);
                if (cdtStateCode == 0) {
                    cdtStateStr = context.getString(R.string.flood_lev_0);
                } else {
                    cdtStateStr = context.getString(R.string.flood_lev_1);
                }
                break;
            case OBConstant.NodeType.RADAR:
                cdtTypeName = context.getString(R.string.sensor_radar);
                if (cdtStateCode == 0) {
                    cdtStateStr = context.getString(R.string.radar_lev_0);
                } else {
                    cdtStateStr = context.getString(R.string.radar_lev_1);
                }
                break;
            case OBConstant.NodeType.CO:
                cdtTypeName = context.getString(R.string.sensor_co);

                break;
            case OBConstant.NodeType.ENVIRONMENTAL:
                cdtTypeName = context.getString(R.string.sensor_env);

                break;
            case OBConstant.NodeType.BODY:
                cdtTypeName = context.getString(R.string.sensor_infra);
                if (cdtStateCode == 0) {
                    cdtStateStr = context.getString(R.string.radar_lev_0);
                } else {
                    cdtStateStr = context.getString(R.string.radar_lev_1);
                }
                break;
            case OBConstant.NodeType.PM2_5:
                cdtTypeName = context.getString(R.string.sensor_pm);

                break;
            case OBConstant.NodeType.POWER_CHECK:
                cdtTypeName = context.getString(R.string.sensor_pwck);

                break;
            case OBConstant.NodeType.VR_RADAR:
                cdtTypeName = context.getString(R.string.sensor_radar);
                if (cdtStateCode == 0) {
                    cdtStateStr = context.getString(R.string.radar_lev_0);
                } else {
                    cdtStateStr = context.getString(R.string.radar_lev_1);
                }
                break;
            case OBConstant.NodeType.LIGHT_SENSOR:
                cdtTypeName = context.getString(R.string.sensor_light);
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
                cdtTypeName = context.getString(R.string.sensor_temp_humid);
                String tempMathStr = math2Str(context, condition.getCondition().substring(0, 2));
                int tempInt = Integer.parseInt(condition.getCondition().substring(2, 4), 16);
                if (tempInt != 255) {
                    tempInt -= 50;
                }
                String humiMathStr = math2Str(context, condition.getCondition().substring(4, 6));
                int humiInt = Integer.parseInt(condition.getCondition().substring(6, 8), 16);
                if (tempInt == 255 && humiInt != 255) {
                    cdtStateStr = context.getString(R.string.hum) + humiMathStr + humiInt;
                } else if (tempInt != 255 && humiInt == 255) {
                    cdtStateStr = context.getString(R.string.temp) + tempMathStr + tempInt;
                } else if (tempInt != 255) {
                    cdtStateStr = context.getString(R.string.temp) + tempMathStr + tempInt + context.getString(R.string.hum) + humiMathStr + humiInt;
                } else {
                    cdtStateStr = context.getString(R.string.not_set_temp_humi);
                }
                break;
            case OBConstant.NodeType.SMOKE_SENSOR:
                cdtTypeName = context.getString(R.string.sensor_smoke);
                if (cdtStateCode == 0) {
                    cdtStateStr = context.getString(R.string.smoke_lev_0);
                } else {
                    cdtStateStr = context.getString(R.string.smoke_lev_1);
                }
                break;
        }
        cdtdetial = item + space + cdtTypeName + cdtName + cdtStateStr;
        return cdtdetial;
    }

    @NonNull
    private String onTime(Context context, Condition condition, String space) {
        String item;
        String cdtdetial;
        item = context.getString(R.string.time_condition);
        String time = condition.getCondition();
                /*一般时间*/
        if (time.substring(0, 2).equals("00")) {
            cdtdetial = onSimpleTime(item, space, time);
        }
        /*循环时间*/
        else {
            cdtdetial = onCircleTime(context, item, space, time);
        }
        return cdtdetial;
    }

    private String math2Str(Context context, String cdtStr) {
        String mathStr = null;
        switch (Integer.parseInt(cdtStr, 16)) {
            case 76:
                mathStr = context.getString(R.string.greater_than);
                break;
            case 73:
                mathStr = context.getString(R.string.less_than);
                break;
            case 74:
                mathStr = context.getString(R.string.equal_to);
                break;
            case 78:
                mathStr = context.getString(R.string.greater_than_or_equal_to);
                break;
            case 75:
                mathStr = context.getString(R.string.less_than_or_equal_to);
                break;
        }
        return mathStr;
    }

    @NonNull
    private String onCircleTime(Context context, String item, String space, String time) {
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
    private String onSimpleTime(String item, String space, String time) {
        String cdtdetial;
        String year = "20" + apendzero(String.valueOf(Integer.parseInt(time.substring(4, 6), 16)));
        String month = apendzero(String.valueOf(Integer.parseInt(time.substring(6, 8), 16)));
        String day = apendzero(String.valueOf(Integer.parseInt(time.substring(8, 10), 16)));
        String hour = apendzero(String.valueOf(Integer.parseInt(time.substring(10, 12), 16)));
        String min = apendzero(String.valueOf(Integer.parseInt(time.substring(12, 14), 16)));
        cdtdetial = item + space + year + "-" + month + "-" + day + "  " + hour + ":" + min;
        return cdtdetial;
    }

    private String apendzero(String time) {
        if (time.length() < 2) {
            time = "0" + time;
        }
        return time;
    }


}