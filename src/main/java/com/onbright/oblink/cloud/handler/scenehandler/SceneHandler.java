package com.onbright.oblink.cloud.handler.scenehandler;

import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.bean.Condition;
import com.onbright.oblink.cloud.net.CloudConstant;
import com.onbright.oblink.cloud.net.CloudParseUtil;
import com.onbright.oblink.cloud.net.GetParameter;
import com.onbright.oblink.cloud.net.HttpRequst;
import com.onbright.oblink.cloud.net.HttpRespond;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 场景管理：创建场景、删除场景、编辑场景触发的先决条件、编辑场景触发的设备行为动作。
 *
 * @author dky
 * 2019/7/3
 */
public abstract class SceneHandler implements HttpRespond {

    private GetSceneForPaginationHandler getSceneForPaginationHandler;

    /**
     * 查询场景
     *
     * @param querySceneLsn 回调
     */
    public void queryScene(QuerySceneLsn querySceneLsn) {
        mQuerySceneLsn = querySceneLsn;
        getSceneForPaginationHandler = new GetSceneForPaginationHandler() {
            @Override
            protected void onRequstComplete(ArrayList<CloudScene> cloudScenes) {
                if (mQuerySceneLsn != null) {
                    mQuerySceneLsn.querySceneOk(cloudScenes);
                }
            }
        };
        getSceneForPaginationHandler.startRequstCloudScene();
    }

    /**
     * 查询场景回调接口
     */
    public interface QuerySceneLsn {
        /**
         * 查询场景完成
         *
         * @param cloudScenes 场景集合
         */
        void querySceneOk(ArrayList<CloudScene> cloudScenes);
    }

    private QuerySceneLsn mQuerySceneLsn;

    /**
     * 添加场景
     *
     * @param cloudScene  要添加的场景，必须设置{@link CloudScene#conditions},{@link CloudScene#actions}
     * @param addSceneLsn 回调
     */
    public void addScene(CloudScene cloudScene, AddSceneLsn addSceneLsn) {
        mAddSceneLsn = addSceneLsn;
        int conditionResult = checkCondition(cloudScene);
        switch (conditionResult) {
            case CONDITION_LIMIT:
                if (mAddSceneLsn != null) {
                    mAddSceneLsn.conditionNumNotMatch();
                }
                return;
            case CONDITION_NOT_ALONG_OBOX:
                if (mAddSceneLsn != null) {
                    mAddSceneLsn.conditionNotOneObox();
                }
                return;
        }
        if (actionNotMatch(cloudScene)) {
            if (mAddSceneLsn != null) {
                mAddSceneLsn.actionNotMatch();
            }
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ADD_SC_INFO,
                GetParameter.onSetScInfo(cloudScene), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 添加场景回调接口
     */
    public interface AddSceneLsn extends NotMatch {
        /**
         * 添加场景成功
         *
         * @param sceneNumber     场景序号
         * @param storeLocal      是否存储到本地
         * @param oboxSceneNumber 存储到本地时候的场景序号,如为null则非存储到本地
         */
        void addSceneOk(String sceneNumber, boolean storeLocal, String oboxSceneNumber);
    }

    private AddSceneLsn mAddSceneLsn;

    /**
     * 修改场景
     */
    public void modifyScene(CloudScene cloudScene, ModifySceneLsn modifySceneLsn) {
        mModifySceneLsn = modifySceneLsn;
        int conditionResult = checkCondition(cloudScene);
        switch (conditionResult) {
            case CONDITION_LIMIT:
                if (mAddSceneLsn != null) {
                    mAddSceneLsn.conditionNumNotMatch();
                }
                return;
            case CONDITION_NOT_ALONG_OBOX:
                if (mAddSceneLsn != null) {
                    mAddSceneLsn.conditionNotOneObox();
                }
                return;
        }
        if (actionNotMatch(cloudScene)) {
            if (mModifySceneLsn != null) {
                mModifySceneLsn.actionNotMatch();
            }
        }
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.MODIFY_SC_INFO,
                GetParameter.onSetScInfo(cloudScene), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 修改场景回调接口
     */
    public interface ModifySceneLsn extends NotMatch {
        /**
         * 修改场景成功
         */
        void modifySceneOk();
    }

    private ModifySceneLsn mModifySceneLsn;

    /**
     * 删除场景
     *
     * @param cloudScene     场景
     * @param deleteSceneLsn 回调
     */
    public void deleteScene(CloudScene cloudScene, DeleteSceneLsn deleteSceneLsn) {
        mDeleteSceneLsn = deleteSceneLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DELETE_SCENE,
                GetParameter.onDeleteScene(cloudScene), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 删除场景回调接口
     */
    public interface DeleteSceneLsn {
        /**
         * 删除场景成功
         */
        void deleteSceneOk();
    }

    private DeleteSceneLsn mDeleteSceneLsn;

    /**
     * 启用场景(启用后，条件满足时触发行为)
     *
     * @param cloudScene     场景
     * @param enableSceneLsn 回调
     */
    public void enAbleScene(CloudScene cloudScene, EnableSceneLsn enableSceneLsn) {
        mEnableSceneLsn = enableSceneLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ENABLE_SCENE,
                GetParameter.onEnableScene(cloudScene), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 启用场景回调接口
     */
    public interface EnableSceneLsn {
        /**
         * 启用场景成功
         */
        void enableSceneOk();
    }

    private EnableSceneLsn mEnableSceneLsn;

    /**
     * 启用场景(启用后，条件满足时触发行为)
     *
     * @param cloudScene      场景
     * @param disableSceneLsn 回调
     */
    public void disAbleScene(CloudScene cloudScene, DisableSceneLsn disableSceneLsn) {
        mDisableSceneLsn = disableSceneLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.DISABLE_SCENE,
                GetParameter.onDisableScene(cloudScene), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 禁用场景回调接口
     */
    public interface DisableSceneLsn {
        /**
         * 禁用场景成功
         */
        void disbleSceneOk();
    }

    private DisableSceneLsn mDisableSceneLsn;

    /**
     * 执行场景(忽略条件直接触发场景行为)
     *
     * @param cloudScene     场景
     * @param actionSceneLsn 回调
     */
    public void actionScene(CloudScene cloudScene, ActionSceneLsn actionSceneLsn) {
        mActionSceneLsn = actionSceneLsn;
        HttpRequst.getHttpRequst().request(this, CloudConstant.CmdValue.ACTION_SCENE,
                GetParameter.onActionScene(cloudScene), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
    }

    /**
     * 执行场景回调接口
     */
    public interface ActionSceneLsn {
        /**
         * 执行场景成功
         */
        void actionSceneOk();
    }

    private ActionSceneLsn mActionSceneLsn;

    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.QUERY_SCENES:
                if (getSceneForPaginationHandler != null) {
                    getSceneForPaginationHandler.onGetSceneData(json);
                }
                break;
            case CloudConstant.CmdValue.ADD_SC_INFO:
                String sceneType = CloudParseUtil.getJsonParm(json, "scene_type");
                String sceneNumber = CloudParseUtil.getJsonParm(json, "scene_number");
                String oboxSceneNumber = null;
                if (CloudScene.LOCAL.equals(sceneType)) {
                    oboxSceneNumber = CloudParseUtil.getJsonParm(json, "obox_scene_number");
                }
                if (mAddSceneLsn != null) {
                    mAddSceneLsn.addSceneOk(sceneNumber, oboxSceneNumber != null, oboxSceneNumber);
                }
                break;
            case CloudConstant.CmdValue.MODIFY_SC_INFO:
                if (mModifySceneLsn != null) {
                    mModifySceneLsn.modifySceneOk();
                }
                break;
            case CloudConstant.CmdValue.DELETE_SCENE:
                if (mDeleteSceneLsn != null) {
                    mDeleteSceneLsn.deleteSceneOk();
                }
                break;
            case CloudConstant.CmdValue.ENABLE_SCENE:
                if (mEnableSceneLsn != null) {
                    mEnableSceneLsn.enableSceneOk();
                }
                break;
            case CloudConstant.CmdValue.DISABLE_SCENE:
                if (mDisableSceneLsn != null) {
                    mDisableSceneLsn.disbleSceneOk();
                }
                break;
            case CloudConstant.CmdValue.ACTION_SCENE:
                if (mActionSceneLsn != null) {
                    mActionSceneLsn.actionSceneOk();
                }
                break;
        }
    }

    /**
     * 处理分页请求服务器情景
     */
    public abstract class GetSceneForPaginationHandler {

        private ArrayList<CloudScene> cloudScenes = new ArrayList<>();
        /**
         * 起始查询
         */
        private int start;
        /**
         * 查询长度,经过实验，此数据可保证数据不超长的同时用时较少
         */
        private static final int COUNT = 300;

        /**
         * 开始擦还训场景
         */
        private void startRequstCloudScene() {
            start = 0;
            cloudScenes.clear();
            HttpRequst.getHttpRequst().request(SceneHandler.this, CloudConstant.CmdValue.QUERY_SCENES,
                    GetParameter.onQueryScenes(true, start, COUNT), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
        }

        /**
         * 处理查询数据
         *
         * @param json 数据json
         */
        private void onGetSceneData(String json) {
            Gson gson = new Gson();
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (!jsonObject.has("scenes")) {
                    jsonObject.put("scenes", new JSONArray());
                }
                JSONArray jsonArray = jsonObject.getJSONArray("scenes");
                for (int i = 0; i < jsonArray.length(); i++) {
                    CloudScene scenes = gson.fromJson(jsonArray.getString(i), CloudScene.class);
                    if (!scenes.getScene_type().equals("04")) {
                        cloudScenes.add(scenes);
                    }
                }
                boolean isEnd = false;
                if (jsonArray.length() < COUNT) {
                    isEnd = true;
                }
                if (isEnd) {
                    onRequstComplete(cloudScenes);
                } else {
                    start += COUNT;
                    HttpRequst.getHttpRequst().request(SceneHandler.this, CloudConstant.CmdValue.QUERY_SCENES,
                            GetParameter.onQueryScenes(true, start, COUNT), CloudConstant.Source.CONSUMER_OPEN, HttpRequst.POST);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * 查询结束回调
         *
         * @param cloudScenes 场景列表
         */
        protected abstract void onRequstComplete(ArrayList<CloudScene> cloudScenes);

    }

    /**
     * 检查条件是否合规,下发到本地的场景，条件节点必须属于同一obox
     *
     * @param cloudScene 目标检测场景
     * @return 符合返回true
     */
    private int checkCondition(CloudScene cloudScene) {
        List<List<Condition>> conditionss = cloudScene.getConditions();
        if (conditionss.size() > CONDITION_LIMIT) {
            return CONDITION_LIMIT;
        }
        for (List<Condition> conditions : conditionss) {
            if (conditions.size() > CONDITION_LIMIT) {
                return CONDITION_LIMIT;
            }
        }
        if (CloudScene.LOCAL.equals(cloudScene.getScene_type())) {
            for (List<Condition> conditions : conditionss) {
                for (Condition condition : conditions) {
                    if (!cloudScene.getObox_serial_id().equals(condition.getObox_serial_id())) {
                        return CONDITION_NOT_ALONG_OBOX;
                    }
                }
            }
        }
        return CONDITION_OK;
    }

    /**
     * 本地场景时，条件不同属OBOX
     */
    private static final int CONDITION_NOT_ALONG_OBOX = 1;
    /**
     * 条件超出3*3限制
     */
    private static final int CONDITION_LIMIT = 3;
    /**
     * 条件合规
     */
    private static final int CONDITION_OK = 2;

    /**
     * 检查行为是否不符合规范，下发到本地的场景，行为节点必须属于同一obox
     *
     * @param cloudScene 目标检测场景
     * @return 不符合返回true
     */
    private boolean actionNotMatch(CloudScene cloudScene) {
        if (CloudScene.LOCAL.equals(cloudScene.getScene_type())) {
            List<Action> actions = cloudScene.getActions();
            for (Action action : actions) {
                if (!cloudScene.getObox_serial_id().equals(action.getObox_serial_id())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 场景条件、行为不符合规范接口
     */
    private interface NotMatch {
        /**
         * 条件数量不符合
         */
        void conditionNumNotMatch();

        /**
         * 条件不同属Obox
         */
        void conditionNotOneObox();

        /**
         * 本地场景时，条件节点不在同一Obox
         */
        void actionNotMatch();
    }

}
