package com.onbright.oblink.cloud.handler;

import com.google.gson.Gson;
import com.onbright.oblink.cloud.bean.CloudScene;
import com.onbright.oblink.cloud.net.CloudConstant;
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
public class SceneHandler implements HttpRespond {

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

//    /**
//     * 添加场景
//     *
//     * @param addSceneLsn 回调
//     */
//    public void addScene(CloudScene cloudScene, AddSceneLsn addSceneLsn) {
//        mAddSceneLsn = addSceneLsn;
//
//    }
//
//
//    /**
//     * 修改场景
//     */
//    public void modifyScene(CloudScene cloudScene, ModifySceneLsn modifySceneLsn) {
//
//    }
//
//
//    /**
//     * 删除场景
//     *
//     * @param cloudScene
//     * @param deleteSceneLsn
//     */
//    public void deleteScene(CloudScene cloudScene, DeleteSceneLsn deleteSceneLsn) {
//
//    }
//
//    /**
//     * 执行场景(忽略条件直接触发场景行为)
//     *
//     * @param cloudScene
//     * @param excuteSceneLsn
//     */
//    public void excuteScene(CloudScene cloudScene, ExcuteSceneLsn excuteSceneLsn) {
//
//    }


    @Override
    public void onSuccess(String action, String json) {
        switch (action) {
            case CloudConstant.CmdValue.QUERY_SCENES:
                if (getSceneForPaginationHandler != null) {
                    getSceneForPaginationHandler.onGetSceneData(json);
                }
                break;


        }
    }

    @Override
    public void onFaild(ErrorCode errorCode, int responseNotOkCode, String operationFailedReason, String action) {

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
     * 检查条件
     *
     * @param cloudScene 目标检测场景
     * @return 符合返回true
     */
    private boolean checkCondition(CloudScene cloudScene) {
        if (CloudScene.LOCAL.equals(cloudScene.getScene_type())) {
            cloudScene.getConditions();
        }
        return true;
    }

    /**
     * 检查行为
     *
     * @param cloudScene 目标检测
     * @return 符合返回true
     */
    private boolean checkeAction(CloudScene cloudScene) {
        if (CloudScene.LOCAL.equals(cloudScene.getScene_type())) {

        }
        return true;
    }

    /**
     * 场景条件、行为不符合规范接口
     */
    public interface NotMatch {
        /**
         * 条件不符合
         */
        void conditionNotMatch();

        /**
         * 行为不符合
         */
        void actionNotMatch();
    }
}
