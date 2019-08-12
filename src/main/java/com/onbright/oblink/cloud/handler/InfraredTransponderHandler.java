package com.onbright.oblink.cloud.handler;


import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.infraredtransponderbean.Brand;
import com.onbright.oblink.cloud.bean.infraredtransponderbean.DeviceType;
import com.onbright.oblink.cloud.bean.infraredtransponderbean.Program;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理红外转发器,
 *
 * @author dky
 * 2019/8/9
 */
public abstract class InfraredTransponderHandler extends WifiDeviceHandler {

    private Gson gson = new Gson();

    /**
     * @param wifiDeviceId wifi设备序列号
     */
    public InfraredTransponderHandler(String wifiDeviceId) throws Exception {
        super(wifiDeviceId);
    }

    /**
     * 查询支持的遥控器类型，查询成功后返回类型例如空调、电视等等
     *
     * @param queryRemoteControlTypeLsn 回调
     */
    public void queryRemoteControlType(QueryRemoteControlTypeLsn queryRemoteControlTypeLsn) {
        mQueryRemoteControlTypeLsn = queryRemoteControlTypeLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_IR_DEVICE_TYPE, GetParameter.onQueryWifiIrDeviceType(),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询支持的遥控器类型回调接口
     */
    public interface QueryRemoteControlTypeLsn {
        /**
         * 查询支持的遥控器类型成功
         *
         * @param deviceTypes 遥控器支持的设备类型列表
         */
        void queryRemoteControlTypeOk(List<DeviceType> deviceTypes);
    }

    private QueryRemoteControlTypeLsn mQueryRemoteControlTypeLsn;

    /**
     * 查询选定设备类型支持的遥控品牌
     *
     * @param deviceType          设备类型
     * @param queryBrandOfTypeLsn 回调
     */
    public void queryBrandOfType(DeviceType deviceType, QueryBrandOfTypeLsn queryBrandOfTypeLsn) {
        mQueryBrandOfTypeLsn = queryBrandOfTypeLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_IR_BRAND,
                GetParameter.onQueryWifiIrBrand(deviceType.getT() + ""),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询选定设备类型支持的遥控品牌回调接口
     */
    public interface QueryBrandOfTypeLsn {
        /**
         * 查询选定设备类型支持的遥控品牌成功
         *
         * @param brands 选定设备类型支持的遥控品牌集合
         */
        void queryBrandOfTypeOk(List<Brand> brands);
    }

    private QueryBrandOfTypeLsn mQueryBrandOfTypeLsn;

    /**
     * 获取已有红外遥控方案
     *
     * @param queryPrograms 回调
     */
    public void queryPrograms(QueryPrograms queryPrograms) {
        mQueryPrograms = queryPrograms;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_IR_DEVICE,
                GetParameter.onQueryWifiIrDevice(wifiDeviceId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 获取已有红外遥控方案回调接口
     */
    public interface QueryPrograms {
        /**
         * 获取已有红外遥控方案成功
         *
         * @param programs 红外遥控方案列表
         */
        void queryProgramsOk(List<Program> programs);
    }

    private QueryPrograms mQueryPrograms;

    /**
     * 删除已有的遥控方案
     *
     * @param program          目标方案
     * @param deleteProgramLsn 回调
     */
    public void deleteProgram(Program program, DeleteProgramLsn deleteProgramLsn) {
        mDeleteProgramLsn = deleteProgramLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_IR_DEVICE,
                GetParameter.onDeleteIrDevice(program.getIndex() + "", wifiDeviceId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 删除已有的遥控方案回调接口
     */
    public interface DeleteProgramLsn {
        /**
         * 删除已有的遥控方案成功
         */
        void deleteProgramOk();
    }

    private DeleteProgramLsn mDeleteProgramLsn;

    @Override
    public void onSuccess(String action, String json) {
        super.onSuccess(action, json);
        switch (action) {
            case CloudConstant.CmdValue.QUERY_IR_DEVICE_TYPE:
                JSONArray typeJA = CloudParseUtil.getJsonArryParm(json, "rs");
                List<DeviceType> deviceTypes = new ArrayList<>();
                for (int i = 0; i < typeJA.length(); i++) {
                    try {
                        DeviceType deviceType = gson.fromJson(typeJA.getString(i), DeviceType.class);
                        deviceTypes.add(deviceType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mQueryRemoteControlTypeLsn != null) {
                    mQueryRemoteControlTypeLsn.queryRemoteControlTypeOk(deviceTypes);
                }
                break;
            case CloudConstant.CmdValue.QUERY_IR_BRAND:
                JSONArray brandJA = CloudParseUtil.getJsonArryParm(json, "rs");
                List<Brand> brands = new ArrayList<>();
                for (int i = 0; i < brandJA.length(); i++) {
                    try {
                        Brand brand = gson.fromJson(brandJA.getString(i), Brand.class);
                        brands.add(brand);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mQueryBrandOfTypeLsn != null) {
                    mQueryBrandOfTypeLsn.queryBrandOfTypeOk(brands);
                }
                break;
            case CloudConstant.CmdValue.QUERY_IR_DEVICE:
                Gson gson = new Gson();
                JSONArray programsJA = CloudParseUtil.getJsonArryParm(json, "rs");
                List<Program> programs = new ArrayList<>();
                for (int i = 0; i < programsJA.length(); i++) {
                    try {
                        String jsonStr = programsJA.getString(i);
                        Program remote = gson.fromJson(jsonStr, Program.class);
                        programs.add(remote);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mQueryPrograms != null) {
                    mQueryPrograms.queryProgramsOk(programs);
                }
                break;
            case CloudConstant.CmdValue.DELETE_IR_DEVICE:
                if (mDeleteProgramLsn != null) {
                    mDeleteProgramLsn.deleteProgramOk();
                }
                break;
        }
    }

}
