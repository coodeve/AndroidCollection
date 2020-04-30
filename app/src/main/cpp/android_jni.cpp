//
// Created by patrick.ding on 2020/4/30.
//
#include <jni.h>
#include "Logger.h"


#ifdef __cplusplus
extern "C" {
#endif

// 测试动态注册
void jniutil_hello(JNIEnv *jniEnv, jobject jobj) {
    LOGI("%s", "hello");
}

//需要动态动态注册的类
static const char *JNIUtil_Class_Name = "com/picovr/androidcollection/Utils/common/JNIUtil";
// Native method table
// 动态注册函数表
static JNINativeMethod methods[] = {
        //{"Method name", "Signature", FunctionPointer}
        // 分别是，函数名称，函数签名，native对应函数实现
        {"hello", "()V", (void *) jniutil_hello}
};

/**
 * 进行注册
 * @param jniEnv
 * @param class_name
 * @param methods
 * @param num_methods
 * @return
 */
jint registerNativeMethods(JNIEnv *jniEnv, const char *class_name, JNINativeMethod *methods,
                           int num_methods) {
    jclass targetClass = jniEnv->FindClass(class_name);
    if (NULL != targetClass) {
        return jniEnv->RegisterNatives(targetClass, methods, num_methods);
    }

    return JNI_ERR;
}

JNIEnv *jniEnv;

// jni初始化
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("%s", "vm");

    if (JNI_OK != vm->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_4)) {
        return JNI_ERR;
    }

    if (JNI_OK != registerNativeMethods(jniEnv, JNIUtil_Class_Name, methods,
                                        sizeof(methods) / sizeof(methods[0]))) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_4;
}


/*
 * Class:     com_picovr_androidcollection_Utils_common_JNIUtil
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_picovr_androidcollection_Utils_common_JNIUtil_init
        (JNIEnv *jniEnv, jclass jcls) {
    LOGI("%s", "init");
}

#ifdef __cplusplus
}
#endif