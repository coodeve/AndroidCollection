#include <jni.h>
#include <cstdio>
#include "include/md5.h"
#include <cstring>
#include <vector>
#include <list>
#include <iostream>
#include "android/log.h"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native-lib",FORMAT,##__VA_ARGS__)
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native-lib",FORMAT,##__VA_ARGS__)
#define ALOGD(FORMAT, ...) __android_log_print(ANDROID_LOG_DEBUG,"native-lib",FORMAT,##__VA_ARGS__)

using namespace std;

static const char *used_key = "b8ac2daa-f7be-46d7-bec6-1e8ed0632292";

class KeyValue {
public:
    string key;
    string value;

    KeyValue(const string &key, const string &value) : key(key), value(value) {}

    friend ostream &operator<<(ostream &os, const KeyValue &value) {
        os << "key: " << value.key << " value: " << value.value;
        return os;
    }

    bool operator<(const KeyValue &kv2) const {
        return key < kv2.key;
    }

    bool operator>(const KeyValue &kv2) const {
        return key > kv2.key;
    }

};

list<KeyValue> jniGetHashMapInfo(JNIEnv *env, jobject hashMapInfo);


extern "C"
JNIEXPORT jstring JNICALL
Java_com_pico_loginpaysdk_utils_SignUtil_signMap(JNIEnv *env, jclass clazz, jobject map_params) {
    jclass map_class = env->FindClass("java/util/Map");
    jmethodID map_put = env->GetMethodID(map_class, "put",
                                         "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
    jvalue *args = new jvalue[2];
    args[0].l = env->NewStringUTF("md5_key");
    args[1].l = env->NewStringUTF(used_key);
    env->CallObjectMethodA(map_params, map_put, args);

    list<KeyValue>::iterator iterator;
    list<KeyValue> keyValueList = jniGetHashMapInfo(env, map_params);
    for (iterator = keyValueList.begin();
         iterator != keyValueList.end(); iterator++) {
        LOGI("ndk --> %s,%s", iterator->key.c_str(), iterator->value.c_str());
    }

    keyValueList.sort();

    LOGI("after sort ... ");
    int first = 0;
    string urlStr;
    iterator = keyValueList.begin();
    while (iterator != keyValueList.end()) {
        LOGI("ndk --> %s,%s", iterator->key.c_str(), iterator->value.c_str());
        if (first != 0) {
            urlStr.append("&");
        }
        first++;
        urlStr.append(iterator->key);
        urlStr.append("=");
        urlStr.append(iterator->value);
        iterator++;
    }
    LOGI("urlString = %s", urlStr.c_str());
    const string &basicString = MD5(urlStr).toString();
    return env->NewStringUTF(MD5(urlStr).toString().c_str());
}

list<KeyValue> jniGetHashMapInfo(JNIEnv *env, jobject hashMapInfo) {
    list<KeyValue> keyValueSet;

    // 获取HashMap类entrySet()方法ID
    jclass hashmapClass = env->FindClass("java/util/HashMap");
    jmethodID entrySetMID = env->GetMethodID(hashmapClass, "entrySet", "()Ljava/util/Set;");
    // 调用entrySet()方法获取Set对象
    jobject setObj = env->CallObjectMethod(hashMapInfo, entrySetMID);
    // 调用size()方法获取HashMap键值对数量
    //  jmethodID sizeMID = env->GetMethodID(hashmapClass, "size", "()I");
    //  jint size = env->CallIntMethod(hashMapInfo, sizeMID);

    // 获取Set类中iterator()方法ID
    jclass setClass = env->FindClass("java/util/Set");
    jmethodID iteratorMID = env->GetMethodID(setClass, "iterator", "()Ljava/util/Iterator;");
    // 调用iterator()方法获取Iterator对象
    jobject iteratorObj = env->CallObjectMethod(setObj, iteratorMID);

    // 获取Iterator类中hasNext()方法ID
    // 用于while循环判断HashMap中是否还有数据
    jclass iteratorClass = env->FindClass("java/util/Iterator");
    jmethodID hasNextMID = env->GetMethodID(iteratorClass, "hasNext", "()Z");
    // 获取Iterator类中next()方法ID
    // 用于读取HashMap中的每一条数据
    jmethodID nextMID = env->GetMethodID(iteratorClass, "next", "()Ljava/lang/Object;");

    // 获取Map.Entry类中getKey()和getValue()的方法ID
    // 用于读取“课程-分数”键值对，注意：内部类使用$符号表示
    jclass entryClass = env->FindClass("java/util/Map$Entry");
    jmethodID getKeyMID = env->GetMethodID(entryClass, "getKey", "()Ljava/lang/Object;");
    jmethodID getValueMID = env->GetMethodID(entryClass, "getValue", "()Ljava/lang/Object;");

    // HashMap只能存放引用数据类型，不能存放int等基本数据类型
    // 使用Integer类的intValue()方法获取int数据
    jclass integerClass = env->FindClass("java/lang/Integer");
    jmethodID valueMID = env->GetMethodID(integerClass, "intValue", "()I");



    // 循环检测HashMap中是否还有数据
    while (env->CallBooleanMethod(iteratorObj, hasNextMID)) {
        // 读取一条数据
        jobject entryObj = env->CallObjectMethod(iteratorObj, nextMID);

        // 提取数据中key值：String类型课程名字
        jstring courseJS = (jstring) env->CallObjectMethod(entryObj, getKeyMID);
        if (courseJS == NULL)   // HashMap允许null类型
            continue;
        // jstring转C风格字符串
        const char *courseStr = env->GetStringUTFChars(courseJS, JNI_FALSE);

        // 提取数据中value值：Integer类型分数，并转为int类型
        jstring scoreObj = (jstring) env->CallObjectMethod(entryObj, getValueMID);
        if (scoreObj == NULL)
            continue;
        const char *scoreStr = env->GetStringUTFChars(scoreObj, JNI_FALSE);

        keyValueSet.push_back(KeyValue(courseStr, scoreStr));

        // 释放UTF字符串资源
        env->ReleaseStringUTFChars(courseJS, courseStr);
        // 释放JNI局部引用资源
        env->DeleteLocalRef(entryObj);
        env->DeleteLocalRef(courseJS);
        env->DeleteLocalRef(scoreObj);
    }

    // 释放JNI局部引用: jclass jobject
    env->DeleteLocalRef(hashmapClass);
    env->DeleteLocalRef(setObj);
    env->DeleteLocalRef(setClass);
    env->DeleteLocalRef(iteratorObj);
    env->DeleteLocalRef(iteratorClass);
    env->DeleteLocalRef(entryClass);
    env->DeleteLocalRef(integerClass);

    // 生成jstring字符串并返回
    return keyValueSet;
}
