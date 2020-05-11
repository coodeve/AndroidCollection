//
// Created by patrick.ding on 2020/5/6.
//
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <signal.h>
#include <unistd.h>
#include <jni.h>
#include <pthread.h>
#include <dlfcn.h>
#include "Logger.h"


/**
 * 信号处理函数
 * @param code
 * @param info
 * @param p
 */
void my_handler(int code, struct siginfo *pSiginfo, void *sc) {

//    pSiginfo->si_signo; /* Signal number 信号量 */
//    pSiginfo->si_code; /* Signal code 错误码 */
//    pSiginfo->si_errno;/* An errno value */
//    sc 是 uc_mcontext的结构体

/**
 * pc值是程序加载到内存中的绝对地址，我们需要拿到奔溃代码相对于共享库的相对偏移地址，
 * 才能使用addr2line分析出是哪一行代码。通过dladdr()可以获得共享库加载到内存的起始地址，
 * 和pc值相减就可以获得相对偏移地址，并且可以获得共享库的名字。
 */
    Dl_info dl_info;
    char *addr;
    if (dladdr(addr, &dl_info) != 0 && dl_info.dli_fname != NULL) {
        void *const nearest = dl_info.dli_saddr;
        //相对偏移地址
        const uintptr_t addr_relative =
                ((uintptr_t) addr - (uintptr_t) dl_info.dli_fbase);

    }

    /* Ensure we do not deadlock. Default of ALRM is to die.
    * (signal() and alarm() are signal-safe) */
    signal(code, SIG_DFL);
    signal(SIGALRM, SIG_DFL);

    /* Ensure we do not deadlock. Default of ALRM is to die.
      * (signal() and alarm() are signal-safe) */
    (void) alarm(8);
}

void *DumpThreadEntry(void *argv) {
    JNIEnv *env = NULL;
    JavaVM *gJavaVM;
    int estatus = 1;
    if (gJavaVM->AttachCurrentThread(&env, NULL) != JNI_OK) {
        LOGE("AttachCurrentThread() failed");
        estatus = 0;
        return &estatus;
    }

    while (true) {
        //等待信号处理函数唤醒
        //waitForSignal();

        //回调native异常堆栈给java层
        //throw_exception(env);

        //告诉信号处理函数已经处理完了
        //notifyThrowException();
    }

    if (gJavaVM->DetachCurrentThread() != JNI_OK) {
        LOGE("DetachCurrentThread() failed");
        estatus = 0;
        return &estatus;
    }

    return &estatus;
}

/**
 * 初始化，创建子线程
 * @param env
 * @param javaClass
 * @param packageNameStr
 * @param tombstoneFilePathStr
 * @param obj
 */
void nativeInit(JNIEnv *env, jclass javaClass, jstring packageNameStr, jstring tombstoneFilePathStr,
                jobject obj) {
    pthread_t thd;
    int ret = pthread_create(&thd, NULL, reinterpret_cast<void *(*)(void *)>(DumpThreadEntry),
                             NULL);
    if (ret) {
        LOGI("%s", "pthread_create error");
    }
}


// sigaction 监听
void initSigaction() {
    struct sigaction sa;
    struct sigaction sa_old;
    memset(&sa, 0, sizeof(sa));
    sigemptyset(&sa.sa_mask);
    sa.sa_sigaction = my_handler;
    sa.sa_flags = SA_SIGINFO;
    if (sigaction(SIGABRT, &sa, &sa_old) == 0) {

    }
}

// 设置格外栈空间
void initStack() {
    stack_t stack;
    memset(&stack, 0, sizeof(stack));
    stack.ss_size = SIGSTKSZ;
    stack.ss_sp = malloc(stack.ss_size);
    stack.ss_flags = 0;
    if (stack.ss_sp != NULL && sigaltstack(&stack, NULL) == 0) {

    }
}
