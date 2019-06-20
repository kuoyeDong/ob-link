package com.onbright.oblink.cloud.bean;

/**
 * 登陆者
 * Created by adolf_dong on 2016/8/12.
 */
public class User {
    private static User user;
    private String name;
    private String weight;

    /**获取账户权重，超级管理员，管理员，访客降序
     * @see com.onbright.oblink.cloud.net.CloudConstant.CloudDitalMode#ROOT
     * @see com.onbright.oblink.cloud.net.CloudConstant.CloudDitalMode#ADMIN
     * @see com.onbright.oblink.cloud.net.CloudConstant.CloudDitalMode#GUEST
     */
    public String getWeight() {
        return weight;
    }

    private User() {

    }
    /**设置账户权重
     * @param weight     /**获取账户权重，超级管理员，管理员，访客降序
     * @see com.onbright.oblink.cloud.net.CloudConstant.CloudDitalMode#ROOT
     * @see com.onbright.oblink.cloud.net.CloudConstant.CloudDitalMode#ADMIN
     * @see com.onbright.oblink.cloud.net.CloudConstant.CloudDitalMode#GUEST
     */
    public void setWeight(String weight) {
        this.weight = weight;
    }

    /**返回用户、单例，服务器模式登陆后实例化
     * @return 返回当前登陆用户
     */
    public static User getUser() {
        synchronized (User.class) {
            if (user == null) {
                user = new User();
            }
            return user;
        }
    }

    /**获取名称
     */
    public String getName() {
        return name;
    }

    /**设置名称
     */
    public void setName(String name) {
        this.name = name;
    }

}
