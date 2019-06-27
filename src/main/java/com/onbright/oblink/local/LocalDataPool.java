package com.onbright.oblink.local;

import android.os.Handler;
import android.os.Message;

import com.onbright.oblink.local.bean.ObGroup;
import com.onbright.oblink.local.bean.ObNode;
import com.onbright.oblink.local.bean.ObScene;
import com.onbright.oblink.local.bean.SceneAction;
import com.onbright.oblink.local.bean.SceneCondition;
import com.onbright.oblink.local.net.OBConstant;
import com.onbright.oblink.local.net.Respond;
import com.onbright.oblink.local.net.TcpSend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地版本数据池
 * Created by adolf_dong on 2016/7/19.
 */
public class LocalDataPool {


    private static LocalDataPool mLocalDataPool;
    /**
     * 当前操作场景内条件
     */
    private static SceneCondition condition;
    /**
     * 当前操作的情景链表
     */
    private static ArrayList<ObScene> curentScenes;
    /**
     * 当前操作的链式情景action集合
     */
    private static List<SceneAction> curentSceneActions;


    public static void setConditions(List<SceneCondition> conditions) {
        LocalDataPool.conditions = conditions;
    }

    /**
     * 引用指向当前的场景条件条件组内条件列表
     */
    private static List<SceneCondition> conditions;

    private Map<String, TcpSend> tcpSends = new HashMap<>();
    private List<Respond> responds = new ArrayList<>();


    private List<Obox> oboxs = new ArrayList<>();
    private List<String> oboxIps = new ArrayList<>();
    private List<String> oboxSSIDs = new ArrayList<>();


    private Map<String, List<ObNode>> obNodeMap = new HashMap<>();
    private Map<String, List<ObGroup>> obGroupMap = new HashMap<>();
    private Map<String, List<ObScene>> obSceneMap = new HashMap<>();

    private List<ObNode> obNodes = new ArrayList<>();
    private List<ObGroup> obGroups = new ArrayList<>();
    private List<ObScene> obScenes = new ArrayList<>();
    private ObNode obNode;
    private ObGroup obGroup;
    private SceneAction action;
    private List<ObNode> cacheNodes;
    private static ObScene curentScene;

    public static void setCondition(SceneCondition condition) {
        LocalDataPool.condition = condition;
    }

    public static SceneCondition getCondition() {
        return condition;
    }

    public static void setCurentScenes(ArrayList<ObScene> curentScenes) {
        LocalDataPool.curentScenes = curentScenes;
    }

    public static ArrayList<ObScene> getCurentScenes() {
        return curentScenes;
    }

    public static void setCurentSceneActions(List<SceneAction> curentSceneActions) {
        LocalDataPool.curentSceneActions = curentSceneActions;
    }


    public static List<SceneAction> getCurentSceneActions() {
        return curentSceneActions;
    }


    public Obox getObox() {
        return obox;
    }

    private Obox obox;

    public static LocalDataPool newInstance() {
        synchronized (LocalDataPool.class) {
            if (mLocalDataPool == null) {
                mLocalDataPool = new LocalDataPool();
            }
            return mLocalDataPool;
        }
    }

    public SceneAction getAction() {
        return action;
    }

    public Map<String, List<ObNode>> getObNodeMap() {
        return obNodeMap;
    }

    public Map<String, List<ObGroup>> getObGroupMap() {
        return obGroupMap;
    }

    public Map<String, List<ObScene>> getObSceneMap() {
        return obSceneMap;
    }

    public static void setCurentScene(ObScene curentScene) {
        LocalDataPool.curentScene = curentScene;
    }

    public static ObScene getCurentScene() {
        return curentScene;
    }

    /**
     * 获取特定的类型设备
     *
     * @param parentType  设备类型
     * @param childType   设备子类型 传0则不区分子类型
     * @param justOneObox 是否取特定obox
     * @param obox        当取特定obox时特定obox的名称
     * @return 目标列表
     */
    public List<ObNode> getNodesForType(int parentType, int childType, boolean justOneObox, Obox obox) {
        List<ObNode> nodes = new ArrayList<>();
        if (justOneObox) {
            obNodes = obNodeMap.get(obox.getObox_serial_id());
        } else {
            obNodes = new ArrayList<>();
            for (List<ObNode> cacheNodes : obNodeMap.values()) {
//                nodes.addAll(cacheNodes);
                for (ObNode obNode :
                        cacheNodes) {
                    obNodes.add(obNode);
                }
            }
        }
        for (ObNode on :
                obNodes) {
            if ((on.getParentType() == parentType || parentType == 0) && (on.getType() == childType || childType == 0)) {
                nodes.add(on);
            }
        }
        return nodes;
    }

    /**
     * 获取单个obox的所有设备,返回的是实例对象
     *
     * @param obox 所取的obox
     */
    public List<ObNode> getObnodesForOneObox(Obox obox) {
        return obNodeMap.get(obox.getObox_serial_id());
    }

    /**
     * 获取单个obox的所有设备,返回的是实例对象
     *
     * @param oboxStr obox序列号
     * @return 目标obox的数据容器
     */
    public List<ObNode> getObnodesForOneObox(String oboxStr) {
        return obNodeMap.get(oboxStr);
    }

    /**
     * 获取特定obox的组数据，返回的是实例对象
     */
    public List<ObGroup> getObGroupsForOneObox(Obox obox) {
        return obGroupMap.get(obox.getObox_serial_id());
    }

    /**
     * 获取指定obox序列号的数据 数据，返回的是实例
     *
     * @param oboxSer 要获取的目标obox序列号
     */
    public List<ObGroup> getObGroupsForOneObox(String oboxSer) {
        return obGroupMap.get(oboxSer);
    }

    /**
     * 获取特定obox的场景，返回的是实例对象
     */
    public List<ObScene> getObScenesForOneObox(Obox obox) {
        return obSceneMap.get(obox.getObox_serial_id());
    }

    /**
     * 获取特定obox的场景，返回的是实例对象
     */
    public List<ObScene> getObScenesForOneObox(String oboxSer) {
        return obSceneMap.get(oboxSer);
    }

    /**
     * 获取特定类型的组列表
     *
     * @param parentType  设备类型
     * @param childType   设备子类型 0表示不过滤子节点设备类型
     * @param justOneObox 是否获取特定obox
     * @param obox        如获取特定obox则传该obox
     */
    public List<ObGroup> getObGroupsForType(int parentType, int childType, boolean justOneObox, Obox obox) {
        List<ObGroup> groups = new ArrayList<>();
        if (justOneObox) {
            obGroups = obGroupMap.get(obox.getObox_serial_id());
        } else {
            obGroups = new ArrayList<>();
            for (List<ObGroup> cacheGroups : obGroupMap.values()) {
//                obGroups.addAll(cacheGroups);
                for (ObGroup obGroup : cacheGroups) {
                    obGroups.add(obGroup);
                }
            }
        }
        for (ObGroup on : obGroups) {
            if ((on.getGroupPType() == 0 || on.getGroupPType() == parentType) && (on.getGroupType() == childType || childType == 0)) {
                groups.add(on);
            }
        }
        return groups;
    }

    /**
     * 用于编辑组节点关系操作
     */
    public boolean editNode(String key, ObNode obNode, boolean isAdd) {
        if (isAdd) {
            return obNodeMap.get(key).add(obNode);
        } else {
            return obNodeMap.get(key).remove(obNode);
        }
    }

    /**
     * 用于编辑组节点关系操作
     */
    public boolean editGroup(String key, ObGroup obGroup, boolean isAdd) {
        if (isAdd) {
            return obGroupMap.get(key).add(obGroup);
        } else {
            return obGroupMap.get(key).remove(obGroup);
        }
    }

    /**
     * 用于编辑场景操作
     */
    public boolean editScene(String key, ObScene obScene, boolean isAdd) {
        if (isAdd) {
            return obSceneMap.get(key).add(obScene);
        } else {
            return obSceneMap.get(key).remove(obScene);
        }
    }

    /**
     * 获取特定obox的场景，或者所有
     *
     * @param justOneObox 是否需要获取特定obox的场景
     * @param obox        特定obox
     */
    public List<ObScene> getObScenesForObox(boolean justOneObox, Obox obox) {
        if (justOneObox) {
            obScenes = obSceneMap.get(obox.getObox_serial_id());
        } else {
            obScenes = new ArrayList<>();
            for (List<ObScene> cacheScenes : obSceneMap.values()) {
                for (ObScene obScene : cacheScenes) {
                    obScenes.add(obScene);
                }
            }
        }
        return obScenes;
    }

    /**
     * 获取特定obox的场景，或者所有
     *
     * @param justOneObox 是否需要获取特定obox的场景
     * @param oboxstr     特定obox序列号
     */
    public List<ObScene> getObScenesForOboxStr(boolean justOneObox, String oboxstr) {
        if (justOneObox) {
            obScenes = obSceneMap.get(oboxstr);
        } else {
            obScenes = new ArrayList<>();
            for (List<ObScene> cacheScenes : obSceneMap.values()) {
                for (ObScene obScene : cacheScenes) {
                    obScenes.add(obScene);
                }
            }
        }
        return obScenes;
    }

    /**
     * 获取当前的obox列表
     */
    public List<Obox> getOboxs() {
        return oboxs;
    }


    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (Respond respond : responds) {
                respond.onReceive(msg);
            }
        }
    };

    public Map<String, TcpSend> getTcpSends() {
        return tcpSends;
    }


    public TcpSend getTcpSend(String key) {
        return tcpSends.get(key);
    }


    public void clearTcpSends() {
        tcpSends.clear();
    }


    /**
     * 给实现HandlerListner接口的页面设置监听
     *
     * @param respond HandlerListner接口的引用
     */
    public void regist(Respond respond) {
        if (!responds.contains(respond)) {
            responds.add(respond);
        }
    }

    /**
     * 给实现HandlerListner接口的页面移除监听
     *
     * @param respond HandlerListner接口的引用
     */
    public void unRegist(Respond respond) {
        responds.remove(respond);
    }

    public Handler getHandler() {
        return handler;
    }

    public List<String> getOboxIps() {
        return oboxIps;
    }

    public List<String> getOboxSSIDs() {
        return oboxSSIDs;
    }

    public void setObNode(ObNode obNode) {
        this.obNode = obNode;
    }

    public ObNode getObNode() {
        return obNode;
    }

    public void setObGroup(ObGroup obGroup) {
        this.obGroup = obGroup;
    }

    public ObGroup getObGroup() {
        return obGroup;
    }

    public void setAction(SceneAction action) {
        this.action = action;
    }


    public void setObox(Obox obox) {
        this.obox = obox;
    }

    /**
     * 通过obox的序列号重置obox引用指向的对象
     *
     * @param rfAddr obox的序列号也就是rf地址
     * @return 是否重置成功
     */
    public boolean setOboxForRfAddr(String rfAddr) {
        for (int i = 0; i < oboxs.size(); i++) {
            Obox obox = oboxs.get(i);
            if (obox.getObox_serial_id().equals(rfAddr)) {
                this.obox = obox;
                return true;
            }
        }
        return false;
    }

    public void setCacheNode(List<ObNode> cacheNode) {
        this.cacheNodes = cacheNode;
    }

    public List<ObNode> getCacheNodes() {
        return cacheNodes;
    }

    /**
     * @return 当前指向的本地条件组内详情
     */
    public static List<SceneCondition> getCurrentConditions() {
        return conditions;
    }

    /**
     * 判断是否可以添加场景
     *
     * @param oboxSerString obox序列号
     * @param len           新增加的场景长度
     * @return 可以则返回true
     */
    public boolean canAddScene(String oboxSerString, int len) {
        List<ObScene> obScenes = getObSceneMap().get(oboxSerString);
        if (obScenes != null) {
            int curLen = obScenes.size();
            return curLen + len < OBConstant.MAX_SCENE_LIMIT;
        } else {
            return true;
        }
    }
}
