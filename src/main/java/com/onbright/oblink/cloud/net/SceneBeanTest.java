package com.onbright.oblink.cloud.net;

import com.onbright.oblink.cloud.bean.Action;
import com.onbright.oblink.cloud.bean.Condition;

import java.io.Serializable;
import java.util.List;

/**
 * Created by shifan_xiao on 2017/9/15.
 */

public class SceneBeanTest implements Serializable {

    private List<String> CMD;
    private List<SceneBean> scene;
    private List<String> access_token;

    public List<String> getCMD() {
        return CMD;
    }

    public void setCMD(List<String> CMD) {
        this.CMD = CMD;
    }

    public List<SceneBean> getScene() {
        return scene;
    }

    public void setScene(List<SceneBean> scene) {
        this.scene = scene;
    }

    public List<String> getAccess_token() {
        return access_token;
    }

    public void setAccess_token(List<String> access_token) {
        this.access_token = access_token;
    }

    public static class SceneBean {
        /**
         * actions : [{"action":"bc000000000000","actionName":"Lamp4","addr":"04","device_child_type":"02","device_type":"01","node_type":"00","obox_serial_id":"3c82000000","serialId":"5225010000"},{"action":"bc000000000000","actionName":"Lamp5","addr":"05","device_child_type":"02","device_type":"01","node_type":"00","obox_serial_id":"3c82000000","serialId":"5325010000"}]
         * conditions : [[{"condition":"8008000000093a00","condition_type":"00"}]]
         * scene_type : 00
         * msg_alter : 0
         * scene_group : 00
         * scene_name : Tyu
         * scene_number : 0
         * scene_status : 01
         */

        private String scene_type;
        private String msg_alter;
        private String scene_group;
        private String scene_name;
        private String scene_number;
        private String scene_status;
        private List<Action> actions;
        private List<List<Condition>> conditions;

        public String getScene_type() {
            return scene_type;
        }

        public void setScene_type(String scene_type) {
            this.scene_type = scene_type;
        }

        public String getMsg_alter() {
            return msg_alter;
        }

        public void setMsg_alter(String msg_alter) {
            this.msg_alter = msg_alter;
        }

        public String getScene_group() {
            return scene_group;
        }

        public void setScene_group(String scene_group) {
            this.scene_group = scene_group;
        }

        public String getScene_name() {
            return scene_name;
        }

        public void setScene_name(String scene_name) {
            this.scene_name = scene_name;
        }

        public String getScene_number() {
            return scene_number;
        }

        public void setScene_number(String scene_number) {
            this.scene_number = scene_number;
        }

        public String getScene_status() {
            return scene_status;
        }

        public void setScene_status(String scene_status) {
            this.scene_status = scene_status;
        }

        public List<Action> getActions() {
            return actions;
        }

        public void setActions(List<Action> actions) {
            this.actions = actions;
        }

        public List<List<Condition>> getConditions() {
            return conditions;
        }

        public void setConditions(List<List<Condition>> conditions) {
            this.conditions = conditions;
        }

        public static class ActionsBean {
            /**
             * action : bc000000000000
             * actionName : Lamp4
             * addr : 04
             * device_child_type : 02
             * device_type : 01
             * node_type : 00
             * obox_serial_id : 3c82000000
             * serialId : 5225010000
             */

            private String action;
            private String actionName;
            private String addr;
            private String device_child_type;
            private String device_type;
            private String node_type;
            private String obox_serial_id;
            private String serialId;

            public String getAction() {
                return action;
            }

            public void setAction(String action) {
                this.action = action;
            }

            public String getActionName() {
                return actionName;
            }

            public void setActionName(String actionName) {
                this.actionName = actionName;
            }

            public String getAddr() {
                return addr;
            }

            public void setAddr(String addr) {
                this.addr = addr;
            }

            public String getDevice_child_type() {
                return device_child_type;
            }

            public void setDevice_child_type(String device_child_type) {
                this.device_child_type = device_child_type;
            }

            public String getDevice_type() {
                return device_type;
            }

            public void setDevice_type(String device_type) {
                this.device_type = device_type;
            }

            public String getNode_type() {
                return node_type;
            }

            public void setNode_type(String node_type) {
                this.node_type = node_type;
            }

            public String getObox_serial_id() {
                return obox_serial_id;
            }

            public void setObox_serial_id(String obox_serial_id) {
                this.obox_serial_id = obox_serial_id;
            }

            public String getSerialId() {
                return serialId;
            }

            public void setSerialId(String serialId) {
                this.serialId = serialId;
            }
        }

        public static class ConditionsBean {
            /**
             * condition : 8008000000093a00
             * condition_type : 00
             */

            private String condition;
            private String condition_type;

            public String getCondition() {
                return condition;
            }

            public void setCondition(String condition) {
                this.condition = condition;
            }

            public String getCondition_type() {
                return condition_type;
            }

            public void setCondition_type(String condition_type) {
                this.condition_type = condition_type;
            }
        }
    }
}
