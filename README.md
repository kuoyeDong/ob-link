# ob-link
# 流程图

# 使用方法：（待bintray审核通过后替换为在线模式）
# 将aar文件拷贝到libs目录下

# 在gradle中添加如下代码

	repositories {
    	flatDir {
       		 dirs 'libs'
    	}
	}
    
    dependencies {
    	api(name:'oblink-1.0.3', ext:'aar')
	}




# 主要模块 #
## ObInit(初始化) ##
### 功能：与昂宝云交互获得口令并建立mq连接. ###
    
	obInit = new ObInit("Tencent", "Tencent", "uniqueKey", this) {
            @Override
            public void onInitSuc() {
                //初始化成功，
            }

            @Override
            public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                
            }
        };
		obInit.init();
	在整个程序退出时请使用 obInit.destory();释放资源
## ConnectHandler(连接工具) ##
### 功能：连接obox或wifi设备到云。 ###
	
	ConnectHandler connectHandler = new ConnectHandler(this, "mymm1234", new ConnectHandler.ConnectOboxLsn() {
                    @Override
                    public void error(ConnectHandler.ConnectError connectError) {
                        showMsg("添加obox失败，请参考参数枚举查看失败原因");
                    }

                    @Override
                    public void connectOboxSuc(Obox obox) {
                        showMsg("成功添加obox，obox序列号为：" + obox.getSerialId());
                        putSpString("oboxSerId", obox.getSerialId());
                    }

                    @Override
                    public void connectWifiDeviceSuc(String s) {
                        showMsg("成功添加非obox的wifi设备，设备参数为：" + s);
                    }
                }, true);
                connectHandler.start();
## OboxHandler(删除obox) ##
### 功能：删除obox，但不断开obox与云的连接。 ###

	OboxHandler oboxHandler = new OboxHandler(oboxSerId) {
                    @Override
                    protected void oboxDeleteSuc(String oboxSerId) {
                        showMsg("成功删除obox，序列号为：" + oboxSerId);
                        putSpString("oboxSerId", null);
                    }

                    @Override
                    public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                        onFaildHandle(errorCode, s1);
                    }
                };
                oboxHandler.deleteObox();
## SmartLockHotelHandler(门锁处理者) ##
### 功能：提供一切与门锁相关的操作。 ###
#### 对象的初始化 ####
	smartLockHotelHandler = new SmartLockHotelHandler(getSpString("lockSerId")) {
            @Override
            protected void lockStatusChange(LockStatusEnum lockStatusEnum) {
                showMsg("锁状态发生变化，请查看参数枚举");
            }

            @Override
            protected void batteryValue(int i) {
                showMsg("当前电量值为：" + i);
            }

            @Override
            public void deleteDeviceSuc() {
                showMsg("成功删除设备");
            }

            @Override
            public void searchNewDeviceSuc() {
                showMsg("成功开启扫描，请等待扫描时传入的时间，单位为秒");
            }

            @Override
            protected void onNewDevice(Device device) {
                showMsg("扫描到新设备,设备名称为：" + device.getName()
	                        + "设备序列号为：" + device.getSerialId());
                putSpString("lockSerId", device.getSerialId());
            }

            @Override
            public void onFaild(ErrorCode errorCode, int i, String s, String s1) {
                onFaildHandle(errorCode, s1);
            }
        };
#### 添加门锁 ####
	smartLockHotelHandler.searchNewDevice(oboxSerId, "30");
#### 删除门锁 ####
	smartLockHotelHandler.deleteDevice();
####获取用户列表####
	smartLockHotelHandler.queryUser(new SmartLockHotelHandler.queryUserLsn() {
                    @Override
                    public void userRecordLoad(List<LockUser> list) {
                        showMsg("获取用户列表成功");
                        lockUsers = list;
                    }
                });
####发送验证码到胁迫时目标手机，此方法用于设定短信接受人时，获得接受人许可,(要使用此功能首先要在门锁设置用户胁迫指纹)####
	smartLockHotelHandler.sendValidateCode(lockUser, "18666860862", new SmartLockHotelHandler.SendCodeLsn() {
                    @Override
                    public void sendCodeOk() {
                        showMsg("sendCodeOk");
                    }
                });
####修改门锁用户####
	lockUser.setNickName("newNickName");
                smartLockHotelHandler.modifyUser(lockUser, "收到的短信验证码", new SmartLockHotelHandler.ModifyUserLsn() {
                    @Override
                    public void modifyUserOk() {
                        showMsg("modifyUserOk");
                    }
                });
####验证门锁权限密码(要操作门锁临时用户，必须验证权限密码，如没有在权限密码则此方法不会执行任何操作，请使用创建权限密码方法createAdminPwd(String, CreatAuthPwdLsn)####
	smartLockHotelHandler.validateAdminPwd("123456", new SmartLockHotelHandler.ValidateAdminPwdLsn() {
                    @Override
                    public void validateAdminPwdOk() {
                        showMsg("validateAdminPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                    @Override
                    public void areadyHasAdminPwd() {
                        showMsg("areadyHasAdminPwd");
                    }
                });
####门锁创建权限密码####
	smartLockHotelHandler.createAdminPwd("123456", new SmartLockHotelHandler.CreatAuthPwdLsn() {
                    @Override
                    public void CreatAuthPwdOk() {
                        showMsg("CreatAuthPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                    @Override
                    public void areadyHasAdminPwd() {
                        showMsg("areadyHasAdminPwd");
                    }
                });
####智能门锁忘记权限密码####
	smartLockHotelHandler.forgetAdminPwd(new SmartLockHotelHandler.ForgetPwdLsn() {
                    @Override
                    public void forgetPwdOk() {
                        showMsg("forgetPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                    @Override
                    public void areadyHasAdminPwd() {
                        showMsg("areadyHasAdminPwd");
                    }
                });
####智能门锁根据推送重置权限密码####
	smartLockHotelHandler.resetAdminPwdByCode("123456", new SmartLockHotelHandler.ResetPwdLsn() {
                    @Override
                    public void waitLockReset() {
                        showMsg("waitLockReset");
                    }

                    @Override
                    public void resetPwdOk() {
                        showMsg("resetPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                    @Override
                    public void areadyHasAdminPwd() {
                        showMsg("areadyHasAdminPwd");
                    }
                });
####修改门锁权限密码####
	smartLockHotelHandler.modifyAdminPwd("123456", "123456", new SmartLockHotelHandler.ModifyPwdLsn() {
                    @Override
                    public void modifyPwdOk() {
                        showMsg("modifyPwdOk");
                    }

                    @Override
                    public void noAdminPwd() {
                        showMsg("noAdminPwd");
                    }

                    @Override
                    public void areadyHasAdminPwd() {
                        showMsg("areadyHasAdminPwd");
                    }
                });
####查询门锁临时用户####
	smartLockHotelHandler.queryTemporaryUser(new SmartLockHotelHandler.QueryTemporaryUserLsn() {
                    @Override
                    public void queryTemporaryUserOk(List<LockTempUser> list) {
                        lockTempUsers = list;
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
####添加门锁临时用户####
	smartLockHotelHandler.addTemporaryUser("nickName", "2019-07-22 00:00:00", "2019-07-22 23:00:00", "3", new SmartLockHotelHandler.AddTemporaryUserLsn() {
                    @Override
                    public void addTemporaryUserOk(LockTempUser lockTempUser) {
                        showMsg("addTemporaryUserOk");
                        if (lockTempUsers == null) {
                            lockTempUsers = new ArrayList<>();
                        }
                        lockTempUsers.add(lockTempUser);
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
####删除门锁临时用户####
	smartLockHotelHandler.deleteTemporaryUser(lockTempUsers.get(0), new SmartLockHotelHandler.DeleteTemporaryUserLsn() {
                    @Override
                    public void deleteTemporaryUserOk() {
                        showMsg("deleteTemporaryUserOk");
                        lockTempUsers.remove(0);
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
####修改门锁临时用户####
				final LockTempUser modifyLockTempUser = lockTempUsers.get(0);
                modifyLockTempUser.setNickName("newNickName");
                smartLockHotelHandler.modifyTemporaryUser(modifyLockTempUser, new SmartLockHotelHandler.ModifyTemporaryUserLsn() {
                    @Override
                    public void modifyTemporaryUserOk(LockTempUser lockTempUser) {
                        showMsg("modifyTemporaryUserOk");
                        lockTempUsers.remove(modifyLockTempUser);
                        lockTempUsers.add(0, lockTempUser);
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
####发送密码给临时用户####
				LockTempUser sendPwdTempUser = lockTempUsers.get(0);
                smartLockHotelHandler.sendTemporaryUserPwd(sendPwdTempUser, new SmartLockHotelHandler.SendTemporaryUserPwdLsn() {
                    @Override
                    public void sendTemporaryUserPwdOk() {
                        showMsg("sendTemporaryUserPwdOk");
                    }

                    @Override
                    public void noAuthToken() {
                        showMsg("noAuthToken");
                    }
                });
####查询推送设置列表####
	smartLockHotelHandler.queryPush(new SmartLockHotelHandler.QueryPushLsn() {
                    @Override
                    public void queryPushOk(String s, List<LockPush> list) {
                        showMsg("queryPushOk,电话号码：" + s);
                        lockPushes = list;
                    }
                });
####修改推送设置####
				if (lockPushes == null) {
                    lockPushes = new ArrayList<>();
                }
                lockPushes.clear();
                LockPush lockPush = new LockPush();
                /*创建一条反锁警报*/
                lockPush.setValue(5);
                lockPush.setEnable(1);
                lockPushes.add(lockPush);
                smartLockHotelHandler.modifyPush("18666860862", lockPushes, new SmartLockHotelHandler.ModifyPushLsn() {
                    @Override
                    public void modifyPushOk() {
                        showMsg("modifyPushOk");
                    }
                });
####释放资源####
	@Override
    protected void onDestroy() {
        super.onDestroy();
        obInit.destory();
        smartLockHotelHandler.unRegist();
    }