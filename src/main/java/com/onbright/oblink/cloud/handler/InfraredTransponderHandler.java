package com.onbright.oblink.cloud.handler;


import com.google.gson.Gson;
import com.onbright.oblink.EventMsg;
import com.onbright.oblink.cloud.bean.infraredtransponderbean.Brand;
import com.onbright.oblink.cloud.bean.infraredtransponderbean.DeviceType;
import com.onbright.oblink.cloud.bean.infraredtransponderbean.KeyTypeEnum;
import com.onbright.oblink.cloud.bean.infraredtransponderbean.Program;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.local.net.OBConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理红外转发器
 *
 * @author dky
 * 2019/8/9
 */
public abstract class InfraredTransponderHandler extends WifiDeviceHandler {

    private Gson gson = new Gson();

    /**
     * @param wifiDeviceId wifi设备序列号,wifi设备特殊，所以必须要求有序列号，
     *                     请自行保存已有wifi设备序列号，或使用{@link ConnectHandler#start()}添加，序列号会在{@link ConnectHandler.ConnectLsn#connectWifiDeviceSuc(String)}返回
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
     * @param queryAlreadyHaveProgramsLsn 回调
     */
    public void queryAlreadyHavePrograms(QueryAlreadyHaveProgramsLsn queryAlreadyHaveProgramsLsn) {
        mQueryAlreadyHaveProgramsLsn = queryAlreadyHaveProgramsLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_IR_DEVICE,
                GetParameter.onQueryWifiIrDevice(wifiDeviceId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 获取已有红外遥控方案回调接口
     */
    public interface QueryAlreadyHaveProgramsLsn {
        /**
         * 获取已有红外遥控方案成功
         *
         * @param programs 红外遥控方案列表
         */
        void queryAlreadyHaveProgramsOk(List<Program> programs);
    }

    private QueryAlreadyHaveProgramsLsn mQueryAlreadyHaveProgramsLsn;

    /**
     * 删除已有的遥控方案
     *
     * @param program          目标方案
     * @param deleteProgramLsn 回调
     */
    public void deleteProgram(Program program, DeleteProgramLsn deleteProgramLsn) {
        mDeleteProgramLsn = deleteProgramLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_IR_DEVICE,
                GetParameter.onDeleteIrDevice(program.getIndex(), wifiDeviceId),
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

    /**
     * 重命名红外遥控方案
     *
     * @param program          目标遥控方案
     * @param renameProgramLsn 回调
     */
    public void renameProgram(Program program, RenameProgramLsn renameProgramLsn) {
        mRenameProgramLsn = renameProgramLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.RENAME_IR_DEVICE,
                GetParameter.renameIrDevice(wifiDeviceId, program.getIndex(), program.getName()),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 重命名红外遥控方案回调接口
     */
    public interface RenameProgramLsn {
        /**
         * 重命名红外遥控方案成功
         */
        void renameProgramOk();
    }

    private RenameProgramLsn mRenameProgramLsn;

    /**
     * 删除指定的拓展(自定义)按键
     *
     * @param program                目标遥控方案
     * @param key                    按键索引
     * @param keyTypeEnum            按键类型枚举
     * @param deleteOneExtendsKeyLsn 回调
     */
    public void deleteOneExtendsKey(Program program, String key, KeyTypeEnum keyTypeEnum, DeleteOneExtendsKeyLsn deleteOneExtendsKeyLsn) {
        mDeleteOneExtendsKeyLsn = deleteOneExtendsKeyLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_IR_DEVICE_KEY,
                GetParameter.deleteIrDeviceKey(wifiDeviceId, program.getIndex(), keyTypeEnum.getKeyType(), key),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 删除指定的拓展(自定义)按键回调接口
     */
    public interface DeleteOneExtendsKeyLsn {
        /**
         * 删除指定的拓展(自定义)按键成功
         */
        void deleteOneExtendsKeyOk();
    }

    private DeleteOneExtendsKeyLsn mDeleteOneExtendsKeyLsn;

    /**
     * 命令红外转发器发送特定遥控方案的特定按键控制数据
     *
     * @param program            遥控方案
     * @param key                按键索引
     * @param keyTypeEnum        按键类型枚举
     * @param transmitProgramLsn 回调
     */
    public void transmitProgram(Program program, String key, KeyTypeEnum keyTypeEnum, TransmitProgramLsn transmitProgramLsn) {
        mTransmitProgramLsn = transmitProgramLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.CONTROL_IR_DEVICE,
                GetParameter.controlWifiIrDevice(wifiDeviceId, program.getIndex(), key, keyTypeEnum.getKeyType()),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 发送控制数据回调接口
     */
    public interface TransmitProgramLsn {
        /**
         * 发送控制数据成功
         */
        void transmitProgramOK();
    }

    private TransmitProgramLsn mTransmitProgramLsn;

    /**
     * 创建遥控器方案，这个方法是从学习方式来创建，建议只在码库方案未找到合适的情况下使用，因为要丰富一个自定义码库还需要用到后续的学习按键补充
     *
     * @param program          新创建遥控器方案，必须设置设备类型{@link Program#setDeviceType(int)}和名称{@link Program#setName(String)}
     * @param createProgramLsn 回调
     */
    public void createProgram(Program program, CreateProgramLsn createProgramLsn) {
        mCreateProgramLsn = createProgramLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.CREATE_IR_DEVICE,
                GetParameter.createIrDevice(wifiDeviceId, program.getDeviceType(), program.getBrandId(), program.getName()),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 创建遥控器方案回调接口
     */
    public interface CreateProgramLsn {
        /**
         * 创建遥控器方案成功
         *
         * @param program 新创建的遥控器方案
         */
        void createProgramOk(Program program);
    }

    private CreateProgramLsn mCreateProgramLsn;

    /**
     * 使红外转发器进入学习按键的状态,请在进入学习状态成功后超时时间内对着红外转发器按下遥控器对应按钮
     *
     * @param program            要学习的红外转发器方案
     * @param keyTypeEnum        按键类型枚举
     * @param key                要学习的按键索引
     * @param timeOut            超时时间，单位S，超出此时间未成功，则key销毁
     * @param learnProgramKeyLsn 回调
     */
    public void learnProgramKey(Program program, KeyTypeEnum keyTypeEnum, String key, int timeOut, LearnProgramKeyLsn learnProgramKeyLsn) {
        mLearnProgramKeyLsn = learnProgramKeyLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.LEARN_IR_DEVICE_KEY,
                GetParameter.learnIrDeviceKey(wifiDeviceId, program.getIndex(), keyTypeEnum.getKeyType(), key, timeOut),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 学习遥控器方案回调接口
     */
    public interface LearnProgramKeyLsn {
        /**
         * 进入学习状态成功，等待用户对红外转发器按下对应按钮
         */
        void waitPressKey();

        /**
         * 成功学习到遥控器按键
         *
         * @param program 学习按键后的完整遥控方案
         */
        void learnProgramKeyOk(Program program);
    }

    private LearnProgramKeyLsn mLearnProgramKeyLsn;

    /**
     * 一键匹配空调遥控方案(只支持空调),进入对码模式,调用成功后请在超时时间内按下空调遥控器按钮
     *
     * @param timeOut 超时时间，单位S，红外转发器只会在该时间内处于接收遥控器信号状态
     * @param brandId 要学习的空调品牌，请使用{@link #queryBrandOfType(DeviceType, QueryBrandOfTypeLsn)}获取该品牌Id{@link Brand#getBid()}
     */
    public void toPairAirConProgram(int timeOut, int brandId, ToPairAirConProgramLsn toPairAirConProgramLsn) {
        mToPairAirConProgramLsn = toPairAirConProgramLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.PAIR_IR_REMOTECODE,
                GetParameter.pairIrRemoteCode(wifiDeviceId, timeOut, brandId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 匹配空调遥控方案回调接口
     */
    public interface ToPairAirConProgramLsn {
        /**
         * 进入接收状态成功，请按下空调遥控器任意按钮
         */
        void waitPressKey();

        /**
         * 查询到匹配待选遥控方案
         *
         * @param pairPrograms 匹配到待选空调遥控方案集合，当未有合适待选方案时，长度为0
         */
        void toPairAirConProgramOk(List<Program> pairPrograms);
    }

    private ToPairAirConProgramLsn mToPairAirConProgramLsn;

    /**
     * 查询某设备类型指定品牌的所有候选遥控方案
     *
     * @param type                      遥控设备类型
     * @param brandId                   设备品牌Id
     * @param queryCandidateProgramsLsn 回调
     */
    public void queryCandidatePrograms(int type, int brandId, QueryCandidateProgramsLsn queryCandidateProgramsLsn) {
        mQueryCandidateProgramsLsn = queryCandidateProgramsLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.QUERY_IR_TESTCODE,
                GetParameter.queryIrTestCode(type, brandId, wifiDeviceId),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 查询某设备类型指定品牌的所有候选遥控方案回调接口
     */
    public interface QueryCandidateProgramsLsn {
        /**
         * 查询某设备类型指定品牌的所有候选遥控方案成功
         *
         * @param candidatePrograms 备选遥控方案列表
         */
        void queryCandidateProgramsOk(List<Program> candidatePrograms);
    }

    private QueryCandidateProgramsLsn mQueryCandidateProgramsLsn;

    /**
     * 在候选遥控方案中选取按键发送，测试该候选遥控方案是否合适
     *
     * @param program                  候选遥控方案中选取的其中之一
     * @param key                      选取遥控方案包含的按键之一
     * @param transmitProgramOnTestLsn 回调
     */
    public void transmitProgramOnTest(Program program, String key, TransmitProgramOnTestLsn transmitProgramOnTestLsn) {
        mTransmitProgramOnTestLsn = transmitProgramOnTestLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.TEST_PROGRAM,
                GetParameter.controlWifiIrDevice(wifiDeviceId, program.getIndex(), key, KeyTypeEnum.TEST_CODE_TYPE.getKeyType()),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 测试候选遥控方案时发送遥控数据回调接口
     */
    public interface TransmitProgramOnTestLsn {
        /**
         * 测试候选遥控方案时发送遥控数据成功
         */
        void transmitProgramOnTestOk();
    }

    private TransmitProgramOnTestLsn mTransmitProgramOnTestLsn;

    /**
     * 添加遥控方案
     *
     * @param addedProgram  添加的遥控方案
     * @param addProgramLsn 回调
     */
    public void addProgram(Program addedProgram, AddProgramLsn addProgramLsn) {
        mAddProgramLsn = addProgramLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.BIND_IR_REMOTECODE,
                GetParameter.bindIrRemotecode(wifiDeviceId, addedProgram.getDeviceType(), addedProgram.getBrandId(),
                        addedProgram.getRid(), addedProgram.getName()),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 添加遥控方案回调接口
     */
    public interface AddProgramLsn {
        /**
         * 添加遥控方案成功
         */
        void addProgramOk();
    }

    private AddProgramLsn mAddProgramLsn;

    /**
     * 下载遥控方案到红外转发器内，此功能为高级功能版本(支持此功能的红外转发设备有USB输出口)
     *
     * @param downloadProgram                  下载到红外转发器的遥控方案
     * @param timeOut                          超时时间，单位S
     * @param downLoadToInfraredTransponderLsn 回调
     */
    public void downLoadToInfraredTransponder(Program downloadProgram, int timeOut, DownLoadToInfraredTransponderLsn downLoadToInfraredTransponderLsn) {
        mDownLoadToInfraredTransponderLsn = downLoadToInfraredTransponderLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.LOCAL_IR_DEVICE_DOWNLOAD,
                GetParameter.localIrDeviceDownload(wifiDeviceId, downloadProgram.getIndex(), timeOut),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 下载到红外转发器回调接口
     */
    public interface DownLoadToInfraredTransponderLsn {
        /**
         * 下载中
         */
        void downLoading();

        /**
         * 下载到红外转发器成功
         *
         * @param index 对应下载遥控方案的{@link Program#getIndex()}
         */
        void downLoadOk(int index);

        /**
         * 下载到红外转发器失败，可能是网络超时或红外转发器内部存储已满
         *
         * @param index 对应下载遥控方案的{@link Program#getIndex()}
         */
        void downLoadNotOk(int index);
    }

    private DownLoadToInfraredTransponderLsn mDownLoadToInfraredTransponderLsn;

    /**
     * 从红外转发器内删除已经下载的遥控方案，此功能为高级功能版本(支持此功能的红外转发设备有USB输出口)
     *
     * @param deleteProgram                  从红外转发器删除的遥控方案
     * @param deleteOnInfraredTransponderLsn 回调
     */
    public void deleteOnInfraredTransponder(Program deleteProgram, DeleteOnInfraredTransponderLsn deleteOnInfraredTransponderLsn) {
        mDeleteOnInfraredTransponderLsn = deleteOnInfraredTransponderLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.LOCAL_IR_DEVICE_DELETE,
                GetParameter.localIrDeviceDelete(wifiDeviceId, deleteProgram.getIndex()),
                CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 从红外转发器内删除已经下载的遥控方案回调接口
     */
    public interface DeleteOnInfraredTransponderLsn {
        /**
         * 从红外转发器内删除已经下载的遥控方案成功
         */
        void deleteOnInfraredTransponderOk();
    }

    private DeleteOnInfraredTransponderLsn mDeleteOnInfraredTransponderLsn;

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
                List<Program> alreadyHavePrograms = new ArrayList<>();
                addProgramsFromJson(json, alreadyHavePrograms);
                if (mQueryAlreadyHaveProgramsLsn != null) {
                    mQueryAlreadyHaveProgramsLsn.queryAlreadyHaveProgramsOk(alreadyHavePrograms);
                }
                break;
            case CloudConstant.CmdValue.DELETE_IR_DEVICE:
                if (mDeleteProgramLsn != null) {
                    mDeleteProgramLsn.deleteProgramOk();
                }
                break;
            case CloudConstant.CmdValue.RENAME_IR_DEVICE:
                if (mRenameProgramLsn != null) {
                    mRenameProgramLsn.renameProgramOk();
                }
                break;
            case CloudConstant.CmdValue.DELETE_IR_DEVICE_KEY:
                if (mDeleteOneExtendsKeyLsn != null) {
                    mDeleteOneExtendsKeyLsn.deleteOneExtendsKeyOk();
                }
                break;
            case CloudConstant.CmdValue.CONTROL_IR_DEVICE:
                if (mTransmitProgramLsn != null) {
                    mTransmitProgramLsn.transmitProgramOK();
                }
                break;
            case CloudConstant.CmdValue.CREATE_IR_DEVICE:
                String remoteStr = CloudParseUtil.getJsonParm(json, "remote");
                Program program = gson.fromJson(remoteStr, Program.class);
                if (mCreateProgramLsn != null) {
                    mCreateProgramLsn.createProgramOk(program);
                }
                break;
            case CloudConstant.CmdValue.LEARN_IR_DEVICE_KEY:
                if (mLearnProgramKeyLsn != null) {
                    mLearnProgramKeyLsn.waitPressKey();
                }
                break;
            case CloudConstant.CmdValue.PAIR_IR_REMOTECODE:
                if (mToPairAirConProgramLsn != null) {
                    mToPairAirConProgramLsn.waitPressKey();
                }
                break;
            case CloudConstant.CmdValue.QUERY_IR_TESTCODE:
                List<Program> candidatePrograms = new ArrayList<>();
                addProgramsFromJson(json, candidatePrograms);
                if (mQueryCandidateProgramsLsn != null) {
                    mQueryCandidateProgramsLsn.queryCandidateProgramsOk(candidatePrograms);
                }
                break;
            case CloudConstant.CmdValue.TEST_PROGRAM:
                if (mTransmitProgramOnTestLsn != null) {
                    mTransmitProgramOnTestLsn.transmitProgramOnTestOk();
                }
                break;
            case CloudConstant.CmdValue.BIND_IR_REMOTECODE:
                if (mAddProgramLsn != null) {
                    mAddProgramLsn.addProgramOk();
                }
                break;
            case CloudConstant.CmdValue.LOCAL_IR_DEVICE_DOWNLOAD:
                if (mDownLoadToInfraredTransponderLsn != null) {
                    mDownLoadToInfraredTransponderLsn.downLoading();
                }
                break;
            case CloudConstant.CmdValue.LOCAL_IR_DEVICE_DELETE:
                if (mDeleteOnInfraredTransponderLsn != null) {
                    mDeleteOnInfraredTransponderLsn.deleteOnInfraredTransponderOk();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onMessage(EventMsg eventMsg) {
        super.onMessage(eventMsg);
        switch (eventMsg.getAction()) {
            case OBConstant.StringKey.LEARN_REMOTE:
                String learnMqStr = (String) eventMsg.getExtra(OBConstant.StringKey.LEARN_REMOTE);
                if (learnMqStr != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(learnMqStr);
                        String serialId = jsonObject.getString("serialId");
                        if (wifiDeviceId.equals(serialId)) {
                            Program program = gson.fromJson(jsonObject.getString("remote"), Program.class);
                            if (mLearnProgramKeyLsn != null) {
                                mLearnProgramKeyLsn.learnProgramKeyOk(program);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case OBConstant.StringKey.PAIR_IR_REMOTE:
                String pairProgramStr = (String) eventMsg.getExtra(OBConstant.StringKey.PAIR_IR_REMOTE);
                List<Program> pairPrograms = new ArrayList<>();
                if (pairProgramStr != null) {
                    String serialId = CloudParseUtil.getJsonParm(pairProgramStr, "serialId");
                    if (wifiDeviceId.equals(serialId)) {
                        addProgramsFromJson(pairProgramStr, pairPrograms);
                    }
                }
                if (mToPairAirConProgramLsn != null) {
                    mToPairAirConProgramLsn.toPairAirConProgramOk(pairPrograms);
                }
                break;
            case OBConstant.StringKey.DOWN_IR_REMOTE:
                String jsonStr = (String) eventMsg.getExtra(OBConstant.StringKey.DOWN_IR_REMOTE);
                try {
                    JSONObject downResultObject = new JSONObject(jsonStr);
                    String serialId = downResultObject.getString("serialId");
                    if (wifiDeviceId.equals(serialId)) {
                        int index = downResultObject.getInt("index");
                        boolean isSuc = downResultObject.getBoolean("success");
                        if (mDownLoadToInfraredTransponderLsn != null) {
                            if (isSuc) {
                                mDownLoadToInfraredTransponderLsn.downLoadOk(index);
                            } else {
                                mDownLoadToInfraredTransponderLsn.downLoadNotOk(index);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }

    /**
     * 处理遥控方案列表添加操作
     *
     * @param json                json数据
     * @param alreadyHavePrograms 遥控方案列表
     */
    private void addProgramsFromJson(String json, List<Program> alreadyHavePrograms) {
        JSONArray alreadyHaveProgramsJA = CloudParseUtil.getJsonArryParm(json, "rs");
        for (int i = 0; i < alreadyHaveProgramsJA.length(); i++) {
            try {
                String jsonStr = alreadyHaveProgramsJA.getString(i);
                Program remote = gson.fromJson(jsonStr, Program.class);
                alreadyHavePrograms.add(remote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
