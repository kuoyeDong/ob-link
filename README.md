# ob-link

# 流程图，请详细参阅，大部分不合规操作都将不被执行，并回调提示函数，如遇此情况，重新查阅此流程图。

![](https://raw.githubusercontent.com/kuoyeDong/ob-link/master/lockStep.png)
# 使用方法：（待bintray审核通过后替换为在线模式）

# 将aar文件拷贝到libs目录下

# 在gradle中添加如下代码

	repositories {
    	flatDir {
       		 dirs 'libs'
    	}
	}
    
    dependencies {
    	api(name:'oblink-1.0.0', ext:'aar')
	    api 'com.google.code.gson:gson:2.8.2'
	    api 'com.squareup.okhttp3:okhttp:3.14.2'
	    api 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
	    api 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
	    api 'com.orhanobut:logger:2.2.0'
	    api 'org.greenrobot:eventbus:3.1.1'
	}

# 主要模块

## 1.ObInit(初始化)

### 功能：与昂宝云交互获得口令并建立mq连接。

	//昂宝分配的key，与key对应的secret，下级用户的唯一标识(可为自行管理用户账户的唯一标识)，上下文
	obInit = new ObInit("Tencent", "Tencent", "uniqueKey", this) {

        @Override
        public void onInitSuc() {
            //初始化成功，已获取token，此token未公开，会在后续的操作中自动使用

        }

        @Override
        public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
            //初始化失败，若网络连接正常，请检查参数是否正确，否则请检查网络连接

        }

    };
	obInit.init();

	在整个程序退出时请使用 obInit.destory()释放mq连接占用的资源

## 2.ConnectHandler(连接工具)

### 功能：连接obox或wifi设备到云。
	
	//上下文，手机连接的wifi密码，回调实例
	ConnectHandler connectHandler = new ConnectHandler(this, "mymm1234", new ConnectHandler.ConnectOboxLsn() {

        @Override
        public void error(ConnectHandler.ConnectError connectError) {
            //添加obox失败，请参考参数枚举查看失败原因

        }

        @Override
        public void connectOboxSuc(Obox obox) {
            //成功添加obox，请自行保存回调中的obox实例，添加设备需要用到obox的序列号

        }

        @Override
        public void connectWifiDeviceSuc(String s) {
            //成功添加非obox的wifi设备，此wifi设备并非门锁，而是典型的有wifi热点信号的设备，设备参数在回调中，可自取

        }

    }, true);

    connectHandler.start();

## 3.OboxHandler(删除obox)

### 功能：删除obox，若OBOX此时处于在线状态，将断开obox与云的连接，若OBOX此时处于离线状态，仅删除OBOX在云端数据。

	//OBOX序列号
	OboxHandler oboxHandler = new OboxHandler(oboxSerId) {

		@Override
        public void noSerialId() {
			//检测到在没序列号的情况下调用了必需序列号的操作，此时目标操作不会被执行，请确认有合法序列号

        }

        @Override
        protected void oboxDeleteSuc(String oboxSerId) {
            //成功删除OBOX，回调参数为该OBOX序列号

        }

        @Override
        public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
            //删除OBOX失败，若网络连接正常，请检查参数是否正确，否则请检查网络连接

        }

    };

    oboxHandler.deleteObox();

## 4.SmartLockHotelHandler(门锁处理者)

### 功能：提供一切与门锁相关的操作。

#### 4.1.对象的初始化

	//锁序列号,若此序列号为空时执行必需序列号的操作，将直接回调noSerialId而不进行目标操作
	smartLockHotelHandler = new SmartLockHotelHandler(lockSerId) {

		@Override
        public void noSerialId() {
            //检测到在没序列号的情况下调用了必需序列号的操作，此时目标操作不会被执行，请确认有合法序列号

        }

        @Override
        protected void lockStatusChange(LockStatusEnum lockStatusEnum) {
            //锁状态发生变化，请查看参数枚举

        }

        @Override
        protected void batteryValue(int i) {
            //当前电量值发生变化，回调参数为电量百分比

        }

        @Override
        public void deleteDeviceSuc() {
            //成功删除设备

        }

        @Override
        public void searchNewDeviceSuc() {
            //成功开启扫描，请等待扫描时传入的时间，单位为秒

        }

        @Override
        protected void onNewDevice(Device device) {
			//扫描到新设备回调，回调参数为扫描到的新设备实例
            
        }

        @Override
        public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
            //操作失败，若网络连接正常，请检查参数是否正确，否则请检查网络连接

        }

    };

#### 4.2.添加门锁

	//OBOX序列号(门锁为OBOX下级设备)，扫描时间(十进制),开启扫描成功回调searchNewDeviceSuc，扫描到门锁回调onNewDevice
	smartLockHotelHandler.searchNewDevice(oboxSerId, "30");

#### 4.3.删除门锁

	//删除门锁，成功回调deleteDeviceSuc
	smartLockHotelHandler.deleteDevice();

#### 4.4.获取用户列表

	//回调实例
	smartLockHotelHandler.queryUser(new SmartLockHotelHandler.queryUserLsn() {

        @Override
        public void userRecordLoad(List<LockUser> list) {
            //获取用户列表成功,回调参数为用户列表，请自行保存

        }
    });

#### 4.5.发送验证码到胁迫时目标手机，此方法用于设定短信接受人时，获得接受人许可,要使用此功能首先要在门锁设置用户胁迫指纹

	//用户实例，接受胁迫信息的手机号码，回调实例
	smartLockHotelHandler.sendValidateCode(lockUser, "18666860862", new SmartLockHotelHandler.SendCodeLsn() {

        @Override
        public void sendCodeOk() {
            //发送验证码成功

        }
    });

#### 4.6.修改门锁用户

	//门锁用户实例，验证码，回调实例
    smartLockHotelHandler.modifyUser(lockUser, "收到的短信验证码", new SmartLockHotelHandler.ModifyUserLsn() {

        @Override
        public void modifyUserOk() {
			//修改用户成功
          
        }
    });

#### 4.7.验证门锁权限密码(要操作门锁临时用户，必须验证权限密码，如没有在权限密码则此方法不会执行任何操作，请使用创建权限密码方法createAdminPwd(String, CreatAuthPwdLsn)

	//权限密码，回调实例
	smartLockHotelHandler.validateAdminPwd("123456", new SmartLockHotelHandler.ValidateAdminPwdLsn() {

        @Override
        public void validateAdminPwdOk() {
            //权限密码验证成功

        }

        @Override
        public void noAdminPwd() {
            //无权限密码，请先创建权限密码

        }

    });

#### 4.8.门锁创建权限密码

	//权限密码，回调实例
	smartLockHotelHandler.createAdminPwd("123456", new SmartLockHotelHandler.CreatAuthPwdLsn() {

        @Override
        public void creatAdminPwdOk() {
            //创建权限密码成功，请自行保存此密码

        }

        @Override
        public void areadyHasAdminPwd() {
            //已经有权限密码，不能重复创建

        }

    });

#### 4.9.智能门锁忘记权限密码

	//回调实例
	smartLockHotelHandler.forgetAdminPwd(new SmartLockHotelHandler.ForgetPwdLsn() {

        @Override
        public void forgetPwdOk() {
            //忘记权限密码成功，可调用resetAdminPwdByCode根据推送重置权限密码

        }

        @Override
        public void noAdminPwd() {
            //无权限密码，请先创建权限密码

        }

    });

#### 4.10.智能门锁根据推送重置权限密码

	//新的权限密码，回调实例
	smartLockHotelHandler.resetAdminPwdByCode("123456", new SmartLockHotelHandler.ResetPwdLsn() {

        @Override
        public void waitLockReset() {
			//进入等待门锁操作状态，请操作门锁
            
        }

        @Override
        public void resetPwdOk() {
            //重置密码成功

        }

        @Override
        public void noAdminPwd() {
            //无权限密码，请先创建权限密码

        }

    });

#### 4.11.修改门锁权限密码

	//旧密码，新密码，回调实例
	smartLockHotelHandler.modifyAdminPwd("123456", "123456", new SmartLockHotelHandler.ModifyPwdLsn() {

        @Override
        public void modifyPwdOk() {
            //修改权限密码成功

        }

        @Override
        public void noAdminPwd() {
            //无权限密码，请先创建权限密码

        }

    });

#### 4.12.查询门锁临时用户

	//回调实例
	smartLockHotelHandler.queryTemporaryUser(new SmartLockHotelHandler.QueryTemporaryUserLsn() {

        @Override
        public void queryTemporaryUserOk(List<LockTempUser> list) {
            //查询临时用户成功，请自取回调实例处理

        }

        @Override
        public void noAuthToken() {
            //尚未验证权限密码，请先验证权限密码

        }

    });

#### 4.13.添加临时用户

	//名称，有效期的起始时间，有效期的终止时间，可执行开门次数(十进制)，回调实例	
	smartLockHotelHandler.addTemporaryUser("nickName", "2019-07-22 00:00:00", "2019-07-22 23:00:00", "3", new SmartLockHotelHandler.AddTemporaryUserLsn() {

        @Override
        public void addTemporaryUserOk(LockTempUser lockTempUser) {
			//添加临时用户成功，请自取回调实例处理
            
        }

        @Override
        public void noAuthToken() {
            //尚未验证权限密码，请先验证权限密码

        }

    });

#### 4.14.删除临时用户

	//删除的临时用户实例，回调实例
	smartLockHotelHandler.deleteTemporaryUser(lockTempUser, new SmartLockHotelHandler.DeleteTemporaryUserLsn() {

        @Override
        public void deleteTemporaryUserOk() {
			//删除临时用户成功
            
        }

        @Override
        public void noAuthToken() {
            //尚未验证权限密码，请先验证权限密码

        }

    });

#### 4.15.修改门锁临时用户

	//修改的临时用户实例，回调实例
    smartLockHotelHandler.modifyTemporaryUser(modifyLockTempUser, new SmartLockHotelHandler.ModifyTemporaryUserLsn() {

        @Override
        public void modifyTemporaryUserOk(LockTempUser lockTempUser) {
            //修改临时用户成功，请自取回调实例处理
            
        }

		@Override
        public void temporaryUserExpire() {
			//临时用户已过有效实现，不能被修改，请执行删除临时用户操作
            
        }

        @Override
        public void noAuthToken() {
            //尚未验证权限密码，请先验证权限密码

        }

    });

#### 4.16.发送密码给临时用户

	//临时用户实例，回调实例
    smartLockHotelHandler.sendTemporaryUserPwd(sendPwdTempUser, new SmartLockHotelHandler.SendTemporaryUserPwdLsn() {

        @Override
        public void sendTemporaryUserPwdOk() {
            //发送密码成功

        }

        @Override
        public void noAuthToken() {
            //尚未验证权限密码，请先验证权限密码

        }

    });

#### 4.17.查询推送设置列表

	//回调实例
	smartLockHotelHandler.queryPush(new SmartLockHotelHandler.QueryPushLsn() {

        @Override
        public void queryPushOk(String s, List<LockPush> list) {
            //查询推送配置成功，请自取回调中的电话号码，以及推送配置列表，详细的配置含义请查看源码文档
            
        }

    });

#### 4.18.修改推送设置

	//电话号码，推送配置列表，回调实例
    smartLockHotelHandler.modifyPush("18666860862", lockPushes, new SmartLockHotelHandler.ModifyPushLsn() {

        @Override
        public void modifyPushOk() {
            //修改推送配置成功

        }

    });

#### 4.19.查询开门记录
	
	//回调实例
	smartLockHotelHandler.queryLockOpenRecord(new SmartLockHotelHandler.OpenRecordLsn() {
        @Override
        public void openRecordLoad(List<LockHistory> list) {
            //查询开锁记录成功，请自取回调列表实例处理
        }
    });

#### 4.20.查询警报记录

	//回调实例
	smartLockHotelHandler.queryLockWarnRecord(new SmartLockHotelHandler.WarnRecordLsn() {
        @Override
        public void warnRecordLoad(List<LockAlarm> list) {
			//查询警报记录成功，请自取回调列表实例处理
        }
    });

## 释放资源

	//建议在销毁阶段统一调用，避免遗漏
	@Override
    protected void onDestroy() {
        super.onDestroy();
        obInit.destory();
        smartLockHotelHandler.unRegist();
    }