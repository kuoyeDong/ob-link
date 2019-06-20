package com.onbright.oblink.cloud.helper;

import android.content.Context;

import com.onbright.oblink.R;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;

/**
 * 解析操作失败代码,可在与服务器交互返回失败时调用
 * {@link SControlStatusHelper#onOperationFailed(String)} 中调用
 * exp.ParseFailedCodeHelper ph = new ParseFailedCodeHelper(context);
 * ph.getWrongContent(json);即可获取失败原因
 */

public class ParseFailedCodeHelper {

    private Context context;

    public ParseFailedCodeHelper(Context context) {
        this.context = context;
    }

    /**在操作失败的时候通过此方法解析失败原因
     * @param json 失败时返回的json
     * @return 代码代表的失败原因
     */
    public String getWrongContent(String json) {
        String msgType = CloudParseUtil.getJsonParm(json, CloudConstant.ParameterKey.MSG);
        String content = "";
        try {
            switch (Integer.parseInt(msgType)) {
                case 1001:
                    content = context.getResources().getString(R.string.error_cdoe_1001);
                    break;
                case 1002:
                    content = context.getResources().getString(R.string.error_cdoe_1002);
                    break;
                case 1003:
                    content = context.getResources().getString(R.string.error_cdoe_1003);
                    break;
                case 1004:
                    content = context.getResources().getString(R.string.error_cdoe_1004);
                    break;
                case 1005:
                    content = context.getResources().getString(R.string.error_cdoe_1005);
                    break;
                case 1006:
                    content = context.getResources().getString(R.string.error_cdoe_1006);
                    break;
                case 2001:
                    content = context.getResources().getString(R.string.error_cdoe_2001);
                    break;
                case 2002:
                    content = context.getResources().getString(R.string.error_cdoe_2002);
                    break;
                case 2003:
                    content = context.getResources().getString(R.string.error_cdoe_2003);
                    break;
                case 2004:
                    content = context.getResources().getString(R.string.error_cdoe_2004);
                    break;
                case 2005:
                    content = context.getResources().getString(R.string.error_cdoe_2005);
                    break;
                case 2006:
                    content = context.getResources().getString(R.string.error_cdoe_2006);
                    break;
                case 2007:
                    content = context.getResources().getString(R.string.error_cdoe_2007);
                    break;
                case 2008:
                    content = context.getResources().getString(R.string.error_cdoe_2008);
                    break;
                case 2009:
                    content = context.getResources().getString(R.string.error_cdoe_2009);
                    break;
                case 2010:
                    content = context.getResources().getString(R.string.error_cdoe_2010);
                    break;
                case 3001:
                    content = context.getResources().getString(R.string.error_cdoe_3001);
                    break;
                case 3002:
                    content = context.getResources().getString(R.string.error_cdoe_3002);
                    break;
                case 3003:
                    content = context.getResources().getString(R.string.error_cdoe_3003);
                    break;
                case 3004:
                    content = context.getResources().getString(R.string.error_cdoe_3004);
                    break;
                case 4001:
                    content = context.getResources().getString(R.string.verify_code_error);
                    break;
                default:
                    content = msgType;
                    break;
            }
        } catch (NumberFormatException nf) {
            nf.printStackTrace();
        }
        return content;
    }
}
