package com.onbright.oblink.cloud.handler;

import android.support.annotation.Nullable;

import com.onbright.oblink.DeviceEnum;
import com.onbright.oblink.cloud.handler.basehandler.ControllableRfDeviceHandler;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.local.net.Transformation;

/**
 * 处理窗帘,打开、关闭、停止、测量长度、以及测量后的分阶功能
 *
 * @author dky
 * 2019/8/8
 */
public abstract class CurtainHandler extends ControllableRfDeviceHandler {

    /**
     * 是否被测量过,说明：只有测量过的窗帘才能按阶开合，默认没测量过，调用请求设备状态接口{@link #queryDeviceStatus()}以确认此状态
     */
    private boolean isMeasured;

    /**
     * 设置模式
     */
    private static final int MODEINDEX = 0;

    /**
     * 读取和设置状态返回时，标识是否有高级功能
     */
    private static final int MEASURE_TAG = 4;

    /**
     * 关闭
     */
    private static final int CLOSE = 0;
    /**
     * 停止
     */
    private static final int STOP = 1;
    /**
     * 打开
     */
    private static final int OPEN = 2;
    /**
     * 档位可设置数值0-11{@link #SEEK_LEVEL}模式时即为档位
     */
    private static final int LEVEL_INDEX = 1;
    /**
     * 滑动方式控制、{@link #LEVEL_INDEX}
     */
    private static final int SEEK_LEVEL = 4;

    /**
     * 记录档位
     */
    private static final int SAVE_LEVEL = 6;

    /**
     * 滑行道记录档位
     */
    private static final int LOAD_LEVEL = 7;
    /**
     * 测量
     */
    private static final int MEASURE_LEVEL = 8;

    /**
     * 滑动的间隔基数
     */
    private static final int NUM = 10;

    /**
     * @param deviceSerId 操作rf设备的序列号，为null只能进行{@link #searchNewDevice(String, String, SearchNewDeviceLsn)}操作
     */
    protected CurtainHandler(@Nullable String deviceSerId) {
        super(deviceSerId);
    }

    /**
     * 打开窗帘
     */
    public void open() {
        change(OPEN, 0);
    }

    /**
     * 关闭窗帘
     */
    public void close() {
        change(CLOSE, 0);
    }

    /**
     * 停止正在进行的动作
     */
    public void stop() {
        change(STOP, 0);
    }

    /**
     * 测量窗帘的行程，调用此方法后，其他方法将被暂停使用，以防干扰，经过一次完整的测量，{@link #isMeasured}为true，按阶段设置开合状态功能被打开{@link #levelTo(int)}
     */
    public void toMeasure() {
        change(MEASURE_LEVEL, 0);
    }

    /**
     * 滑动到档位,若不确定此具备此功能，则回调{@link #notFeatures()}
     *
     * @param level 档位，取值范围1-10
     */
    public void levelTo(int level) {
        if (checkIsMeasured()) {
            change(SEEK_LEVEL, level * NUM);
        }
    }

    /**
     * 检查是否有测量行程
     *
     * @return 有返回true
     */
    private boolean checkIsMeasured() {
        if (isMeasured) {
            return true;
        } else {
            notFeatures();
            return false;
        }
    }

    /**
     * 未确认具备滑动档位功能，请调用{@link #queryDeviceStatus()},或者有存储状态可使用{@link #setStatus(String)}确认此功能，
     * 若经过确认还是没有，请调用{@link #toMeasure()}进行测量，并等待{@link #measureComplete()}回调，期间请不要进行其他操作以免干扰
     */
    protected abstract void notFeatures();

    /**
     * 记录当前档位
     */
    public void saveLevel() {
        if (checkIsMeasured()) {
            change(SAVE_LEVEL, 0);
        }
    }

    /**
     * 滑动到记录档位
     */
    public void loadLevel() {
        if (checkIsMeasured()) {
            change(LOAD_LEVEL, 0);
        }
    }

    @Override
    protected void onStatusChange(String status) {
        handleStatus(status);
    }

    /**
     * 处理状态
     *
     * @param status 状态
     */
    private void handleStatus(String status) {
        byte[] statusBytes = Transformation.hexString2Bytes(status);
        if (statusBytes[3] == 1) {
            isMeasured = true;
            measureComplete();
            return;
        } else if (statusBytes[MEASURE_TAG] == 1) {
            if (!isMeasured) {
                isMeasured = true;
                isMeasured();
            }
        }
        if (statusBytes[MODEINDEX] == CLOSE) {
            onClosed();
        } else if (statusBytes[MODEINDEX] == OPEN) {
            onOpen();
        } else if (statusBytes[MODEINDEX] == STOP) {
            onStop();
        } else if (isMeasured) {
            if (statusBytes[MODEINDEX] == SEEK_LEVEL || statusBytes[MODEINDEX] == LOAD_LEVEL) {
                onLevel(statusBytes[LEVEL_INDEX]);
            }
        }
    }

    /**
     * 状态为滑动到档位
     *
     * @param level 档位值
     */
    protected abstract void onLevel(byte level);

    /**
     * 状态为停止，但无法知道停止位置
     */
    protected abstract void onStop();

    /**
     * 状态为打开
     */
    protected abstract void onOpen();

    /**
     * 状态为关闭
     */
    protected abstract void onClosed();

    /**
     * 确认窗帘进行过测量行程，具备滑动档位功能，出于性能考虑，此方法最多只会被回调一次
     */
    protected abstract void isMeasured();

    /**
     * 完成测量行程时回调，请注意此回调在测量完成的时候回调，并非通过查询状态确认有测量过行程时
     */
    protected abstract void measureComplete();

    @Override
    protected DeviceEnum getDeviceEnum() {
        return DeviceEnum.THE_CURTAINS;
    }

    /**
     * 发起设置请求
     *
     * @param mode  设置模式
     * @param level 档位设置时的档位
     */
    private void change(int mode, int level) {
        byte[] statusCache = new byte[7];
        statusCache[MODEINDEX] = (byte) mode;
        statusCache[LEVEL_INDEX] = (byte) level;
        sendStatus = Transformation.byteArryToHexString(statusCache);
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.SETTING_NODE_STATUS,
                GetParameter.onSetNodeState(deviceSerId, sendStatus), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }


}
