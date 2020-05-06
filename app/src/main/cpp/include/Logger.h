//
// Created by patrick.ding on 2020/4/30.
//
#ifndef ANDROIDCOLLECTION_LOGGER_H
#define ANDROIDCOLLECTION_LOGGER_H
#include "android/log.h"

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"native-lib",FORMAT,##__VA_ARGS__)
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"native-lib",FORMAT,##__VA_ARGS__)

extern JavaVM* gJavaVM;

#endif //ANDROIDCOLLECTION_LOGGER_H
