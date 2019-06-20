#include <jni.h>
#include<math.h>
#include <stdint.h>

int sbox[] = {0x13, 0x51, 0x24, 0x67, 0xf1, 0xa9, 0x4b, 0x9c, 0xc8, 0x74, 0x62, 0x3d, 0xd0, 0x81,
              0x7a, 0xe0};

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsOboxSSID(JNIEnv *env, jobject instance,
                                                      jbyteArray array_) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    array[5] = 0x01;
    array[6] = 0x02;
    array[7] = 0x02;
    array[8] = (jbyte) 0xff;
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsChangeOboxRfPsw(JNIEnv *env, jobject instance,
                                                             jbyteArray array_, jbyteArray oldPsw_,
                                                             jbyteArray newPsw_, jint oldLen,
                                                             jint newLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *oldPsw = (*env)->GetByteArrayElements(env, oldPsw_, JNI_FALSE);
    jbyte *newPsw = (*env)->GetByteArrayElements(env, newPsw_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = 0x07;
    array[6] = 0x03;
    for (int i = 0; i < oldLen; ++i) {
        array[7 + i] = oldPsw[i];
    }
    for (int i = 0; i < newLen; ++i) {
        array[7 + oldLen + i] = newPsw[i];
    }
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, oldPsw_, oldPsw, 0);
    (*env)->ReleaseByteArrayElements(env, newPsw_, newPsw, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsOboxToStation(JNIEnv *env, jobject instance,
                                                           jbyteArray array_, jbyteArray ssid_,
                                                           jbyteArray pswdata_, jbyteArray ipByte_,
                                                           jbyteArray codebytes_, jint ssidLen,
                                                           jint pswLen, jint ipLen, jint codeLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *ssid = (*env)->GetByteArrayElements(env, ssid_, JNI_FALSE);
    jbyte *pswdata = (*env)->GetByteArrayElements(env, pswdata_, JNI_FALSE);
    jbyte *ipByte = (*env)->GetByteArrayElements(env, ipByte_, JNI_FALSE);
    jbyte *codebytes = (*env)->GetByteArrayElements(env, codebytes_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = 0x13;
    array[6] = 0x03;//长度
    array[7] = 0x03;
    array[8] = 0x0b;
    array[9] = 0x01;//obox编号
    array[10] = 0x01;//参数编号
    array[11] = 0x01;//参数长度
    array[12] = 0x01;//工作模式1station  2ap
    array[13] = 0x12;
    array[14] = (jbyte) ssidLen;

    for (int i = 0; i < ssidLen; ++i) {
        array[15 + i] = ssid[i];
    }
    array[15 + ssidLen] = 0x13;
    array[16 + ssidLen] = (jbyte) pswLen;

    for (int i = 0; i < pswLen; ++i) {
        array[17 + ssidLen + i] = pswdata[i];
    }
    array[17 + ssidLen + pswLen] = 0x14;
    array[18 + ssidLen + pswLen] = (jbyte) ipLen;
    for (int i = 0; i < ipLen; ++i) {
        array[19 + ssidLen + pswLen + i] = ipByte[i];
    }
    if (codeLen != 0) {
        array[19 + ssidLen + pswLen + ipLen] = 0x16;
        array[20 + ssidLen + pswLen + ipLen] = (jbyte) codeLen;
        for (int i = 0; i < codeLen; ++i) {
            array[21 + ssidLen + pswLen + ipLen + i] = codebytes[i];
        }
    }
    //此处可能要加分隔符
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, ssid_, ssid, 0);
    (*env)->ReleaseByteArrayElements(env, pswdata_, pswdata, 0);
    (*env)->ReleaseByteArrayElements(env, ipByte_, ipByte, 0);
    (*env)->ReleaseByteArrayElements(env, codebytes_, codebytes, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsGetDevice(JNIEnv *env, jobject instance,
                                                       jbyteArray array_, jint index,
                                                       jboolean isGroup) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = 0x13;
    array[7] = 0x03;
    array[8] = (jbyte) (isGroup ? 0x04 : 0x02);
    array[9] = (jbyte) index;
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsGetDeviceState(JNIEnv *env, jobject instance,
                                                            jbyteArray array_, jbyteArray cplAddr_,
                                                            jbyteArray dataMark_, jint cplAddrLen,
                                                            jint dataMarkLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *cplAddr = (*env)->GetByteArrayElements(env, cplAddr_, JNI_FALSE);
    jbyte *dataMark = (*env)->GetByteArrayElements(env, dataMark_, JNI_FALSE);

    array[4] = (jbyte) 0x01;
    for (int i = 0; i < cplAddrLen - 2; ++i) {
        array[7 + i] = cplAddr[i];
    }
    array[7 + cplAddrLen - 1] = cplAddr[6];
    for (int i = 0; i < dataMarkLen; ++i) {
        array[7 + cplAddrLen + i] = dataMark[i];
    }
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, cplAddr_, cplAddr, 0);
    (*env)->ReleaseByteArrayElements(env, dataMark_, dataMark, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsReqOboxMsg(JNIEnv *env, jobject instance,
                                                        jbyteArray array_) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    array[4] = (jbyte) 0x80;
    array[5] = 0x13;
    array[7] = 0x03;
    array[8] = 0x0a;
    array[9] = 0x01;
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsMakeOboxCloudState(JNIEnv *env, jobject instance,
                                                                jbyteArray array_, jboolean isAdd,
                                                                jbyteArray ipByte_,
                                                                jint ipByteLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *ipByte = (*env)->GetByteArrayElements(env, ipByte_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = 0x12;
    if (isAdd) {
        array[7] = 0x01;
        array[8] = 0x20;
        for (int i = 0; i < ipByteLen; ++i) {
            array[9 + i] = ipByte[i];
        }
    }
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, ipByte_, ipByte, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsRfCmd(JNIEnv *env, jobject instance, jbyteArray array_,
                                                   jint mode, jbyteArray startId_, jint time,
                                                   jint startIdLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *startId = (*env)->GetByteArrayElements(env, startId_, JNI_FALSE);

    array[5] = 0x03;
    array[7] = (jbyte) mode;
    if (mode != 0) {
        for (int i = 0; i < startIdLen; ++i) {
            array[8 + i] = startId[i];
        }
    }
    array[11] = (jbyte) time;
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, startId_, startId, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsRelease(JNIEnv *env, jobject instance,
                                                     jbyteArray array_) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    array[4] = (jbyte) 0x80;
    array[5] = 0x0a;
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsSetOboxTime(JNIEnv *env, jobject instance,
                                                         jbyteArray array_, jint year, jint month,
                                                         jint montyDay, jint weekDay, jint hour,
                                                         jint minute,
                                                         jint second) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    array[4] = (jbyte) 0x80;
    array[5] = 0x0d;
    array[7] = 0x08;//时区
    array[8] = (jbyte) (year % 100);
    array[9] = (jbyte) (month + 1);
    array[10] = (jbyte) montyDay;
    if (weekDay == 7) {
        array[11] = 1;
    } else {
        array[11] = (jbyte) (weekDay + 1);
    }
    array[12] = (jbyte) hour;
    array[13] = (jbyte) minute;
    array[14] = (jbyte) second;
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsReqScene(JNIEnv *env, jobject instance,
                                                      jbyteArray array_, jint factorsOfScene,
                                                      jint serNum, jint num, jint actionIndex) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);

    array[5] = 0x0e;
    array[7] = (jbyte) factorsOfScene;
    array[8] = (jbyte) (factorsOfScene == 1 ? 0 : serNum);
    array[9] = (jbyte) num;
    if (factorsOfScene == 3) {
        array[9] = (jbyte) actionIndex;
    }
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsSetNodeState(JNIEnv *env, jobject instance,
                                                          jbyteArray array_, jbyteArray cplAddr_,
                                                          jbyteArray status_, jboolean isGroup,
                                                          jint cplAddrLen, jint statusLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *cplAddr = (*env)->GetByteArrayElements(env, cplAddr_, JNI_FALSE);
    jbyte *status = (*env)->GetByteArrayElements(env, status_, JNI_FALSE);

    array[4] = (jbyte) 0x81;
    for (int i = 0; i < cplAddrLen; ++i) {
        array[7 + i] = cplAddr[i];
    }

    if (isGroup) {
        array[8 + 5] = (jbyte) 0xff;
    } else {
        array[8 + 4] = 0;
    }
    for (int i = 0; i < statusLen; ++i) {
        array[14 + i] = status[i];
    }
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, cplAddr_, cplAddr, 0);
    (*env)->ReleaseByteArrayElements(env, status_, status, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsEditNodeOrGroup(JNIEnv *env, jobject instance,
                                                             jbyteArray array_, jint opreType,
                                                             jint nodeAddr, jint groupAddr,
                                                             jbyteArray id_, jboolean isGroup,
                                                             jint idLen, jbyteArray rfAddr_,
                                                             jint rfAddrLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *id = (*env)->GetByteArrayElements(env, id_, JNI_FALSE);
    jbyte *rfAddr = (*env)->GetByteArrayElements(env, rfAddr_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = (jbyte) 0x04;
    array[7] = (jbyte) opreType;
    for (int i = 0; i < rfAddrLen; ++i) {
        array[8 + i] = rfAddr[i];
    }
    switch (opreType) {
        case 0:
            /*删除*/
            array[8 + rfAddrLen] = (jbyte) (isGroup ? groupAddr : 0);
            array[8 + rfAddrLen + 1] = (jbyte) (isGroup ? 0 : nodeAddr);
            break;
        case 1:
            /*新增组*/
            for (int i = 0; i < idLen; ++i) {
                array[8 + rfAddrLen + 2 + i] = id[i];
            }
            break;
        case 2:
            /*重命名*/
            array[8 + rfAddrLen] = (jbyte) (isGroup ? groupAddr : 0);
            array[8 + rfAddrLen + 1] = (jbyte) (isGroup ? 0 : nodeAddr);
            for (int i = 0; i < idLen; ++i) {
                array[8 + rfAddrLen + 2 + i] = id[i];
            }
            break;
        default:
            break;
    }
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, id_, id, 0);
    (*env)->ReleaseByteArrayElements(env, rfAddr_, rfAddr, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsOrganizGroup(JNIEnv *env, jobject instance,
                                                          jbyteArray array_, jbyte groupAddr,
                                                          jbyte nodeAddr, jboolean isAdd,
                                                          jbyteArray rfAddr_, jint rfAddrLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *rfAddr = (*env)->GetByteArrayElements(env, rfAddr_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = (jbyte) 0x06;
    for (int i = 0; i < rfAddrLen; ++i) {
        array[7 + i] = rfAddr[i];
    }
    if (isAdd) {
        array[7 + rfAddrLen] = groupAddr;
    }
    array[7 + rfAddrLen + 1] = nodeAddr;
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, rfAddr_, rfAddr, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsEditSceneId(JNIEnv *env, jobject instance,
                                                         jbyteArray array_, jint vailable,
                                                         jint operaType,
                                                         jint sceneSer, jbyteArray id_, jint idLen,
                                                         jint sceneGroup) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *id = (*env)->GetByteArrayElements(env, id_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = (jbyte) 0x0e;
    array[7] = 1;
    array[8] = (jbyte) ((vailable << 4) + operaType);
    array[9] = (jbyte) (sceneSer);
    for (int i = 0; i < idLen; ++i) {
        array[10 + i] = id[i];
    }
    array[10 + 16] = (jbyte) (sceneGroup);
    array[61] = 0x55;

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, id_, id, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsEditSceneCondition(JNIEnv *env, jobject instance,
                                                                jbyteArray array_, jint sceneSer,
                                                                jint conditionGroupNum,
                                                                jint sceneConditionsLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = (jbyte) 0x0e;
    array[7] = 2;
    array[8] = (jbyte) (pow(2, sceneConditionsLen) - 1);
    array[9] = (jbyte) (sceneSer);
    array[10] = (jbyte) (conditionGroupNum);
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsEditSceneConditionDetial(JNIEnv *env, jobject instance,
                                                                      jbyteArray array_,
                                                                      jbyteArray conditionAddr_,
                                                                      jbyteArray condition_,
                                                                      jint conditionAddrLen,
                                                                      jint conditionLen,
                                                                      jint circleIndex) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *conditionAddr = (*env)->GetByteArrayElements(env, conditionAddr_, JNI_FALSE);
    jbyte *condition = (*env)->GetByteArrayElements(env, condition_, JNI_FALSE);

    for (int i = 0; i < conditionAddrLen; ++i) {
        array[12 + circleIndex * 15 + i] = conditionAddr[i];
    }

    for (int i = 0; i < conditionLen; ++i) {
        array[12 + 7 + circleIndex * 15 + i] = condition[i];
    }

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, conditionAddr_, conditionAddr, 0);
    (*env)->ReleaseByteArrayElements(env, condition_, condition, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsEditSceneConditionEnd(JNIEnv *env, jobject instance,
                                                                   jbyteArray array_,
                                                                   jint conditionType) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);

    array[11] = (jbyte) (conditionType);


    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsSetWifiConfig(JNIEnv *env, jobject instance,
                                                           jbyteArray array_, jint config) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);

    array[4] = (jbyte) 0x80;
    array[5] = (jbyte) 0x08;
    array[14] = (jbyte) config;
    array[61] = 0x55;
    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsEditSceneAction(JNIEnv *env, jobject instance,
                                                             jbyteArray array_,
                                                             jbyteArray actionAddr_,
                                                             jbyteArray action_, jint actionAddrLen,
                                                             jint actionLen, jboolean isGroup,
                                                             jint circleIndex) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);
    jbyte *actionAddr = (*env)->GetByteArrayElements(env, actionAddr_, JNI_FALSE);
    jbyte *action = (*env)->GetByteArrayElements(env, action_, JNI_FALSE);

    for (int i = 0; i < actionAddrLen; ++i) {
        array[10 + circleIndex * 15 + i] = actionAddr[i];
    }
    if (isGroup) {
        array[10 + circleIndex * 15 + 6] = (jbyte) 0xff;
    } else {
        array[10 + circleIndex * 15 + 5] = 0;
    }

    for (int i = 0; i < actionLen; ++i) {
        array[10 + circleIndex * 15 + 7 + i] = action[i];
    }

    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
    (*env)->ReleaseByteArrayElements(env, actionAddr_, actionAddr, 0);
    (*env)->ReleaseByteArrayElements(env, action_, action, 0);
}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_TcpSend_tsEditSceneActionEnd(JNIEnv *env, jobject instance,
                                                                jbyteArray array_, jint sceneSer,
                                                                jint option, jboolean isAllDelete,
                                                                jint actionsLen) {
    jbyte *array = (*env)->GetByteArrayElements(env, array_, JNI_FALSE);

    for (int i = 0; i < 3 - actionsLen; i++) {
        option += (3 << (4 - 2 * i));
    }
    array[4] = (jbyte) 0x80;
    array[5] = (jbyte) 0x0e;
    array[7] = 3;
    array[9] = (jbyte) (sceneSer);

    array[8] = (jbyte) (isAllDelete ? 0x3f : option);
    array[61] = 0x55;


    (*env)->ReleaseByteArrayElements(env, array_, array, 0);
}

JNIEXPORT jint JNICALL
Java_com_onbright_oblink_local_net_Sbox_tsSum(JNIEnv *env, jobject instance, jbyteArray pBuffer_,
                                              jint length) {
    jbyte *pBuffer = (*env)->GetByteArrayElements(env, pBuffer_, JNI_FALSE);

    int wCRC16 = 0;
    if ((pBuffer == NULL) || (length == 0)) {
        return 0;
    }
    for (int i = 0; i < length; i++) {
        int crc = wCRC16;
        crc += (((char) pBuffer[i] & 0x00ff) + ((char) pBuffer[i] >> 8 & 0xff00));
        wCRC16 = crc & 0xffff;

    }
    (*env)->ReleaseByteArrayElements(env, pBuffer_, pBuffer, 0);
    return wCRC16;

}

JNIEXPORT void JNICALL
Java_com_onbright_oblink_local_net_Sbox_ts(JNIEnv *env, jobject instance, jbyteArray result_,
                                           jbyteArray key_) {
    jbyte *result = (*env)->GetByteArrayElements(env, result_, JNI_FALSE);
    jbyte *key = (*env)->GetByteArrayElements(env, key_, JNI_FALSE);
    for (int j = 0; j < 4; j++) {
        for (int i = 0; i < 8; i++) {
            int value = key[i] & 0xff;
            int high = (value >> 4);
            int low = value & 0x0f;
            int highGoal = sbox[high] ^result[2 * i + 16 * j];
            int lowGoal = sbox[low] ^result[2 * i + 16 * j + 1];
            result[2 * i + 16 * j] = (jbyte) highGoal;
            result[2 * i + 16 * j + 1] = (jbyte) lowGoal;
        }
    }

    (*env)->ReleaseByteArrayElements(env, result_, result, 0);
    (*env)->ReleaseByteArrayElements(env, key_, key, 0);
}