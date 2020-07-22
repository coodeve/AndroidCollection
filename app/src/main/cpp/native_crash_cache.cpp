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
#include <unistd.h>
#include "Logger.h"


void get_native_thread() {
    void *handle = dlopen("libart.so", RTLD_LAZY);
    void (*foreach)();
    dlsym(handle, "ForEach");
}


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


/**
 * 获取线程名称
 * 然后把线程名称传给Java层
 * @param tid
 * @return
 */
char *getThreadName(pid_t tid) {
    if (tid <= 1) {
        return NULL;
    }
    size_t THREAD_NAME_LENGTH = 100;

    char *path = (char *) calloc(1, 80);
    char *line = (char *) calloc(1, THREAD_NAME_LENGTH);

    snprintf(path, PATH_MAX, "proc/%d/comm", tid);
    FILE *commFile = NULL;
    if (commFile = fopen(path, "r")) {
        fgets(line, THREAD_NAME_LENGTH, commFile);
        fclose(commFile);
    }
    free(path);
    if (line) {
        int length = strlen(line);
        if (line[length - 1] == '\n') {
            line[length - 1] = '\0';
        }
    }
    return line;
}

char *print_sig(int sign, int code) {
    switch (sign) {
        case SIGFPE:
            switch (code) {
                case FPE_INTDIV:
                    return "Integer divide by zero";
                case FPE_INTOVF:
                    return "Integer overflow";
                case FPE_FLTDIV:
                    return "Floating-point divide by zero";
                case FPE_FLTOVF:
                    return "Floating-point overflow";
                case FPE_FLTUND:
                    return "Floating-point underflow";
                case FPE_FLTRES:
                    return "Floating-point inexact result";
                case FPE_FLTINV:
                    return "Invalid floating-point operation";
                case FPE_FLTSUB:
                    return "Subscript out of range";
                default:
                    return "Floating-point";
            }
            break;
        case SIGSEGV:
            switch (code) {
                case SEGV_MAPERR:
                    return "Address not mapped to object";
                case SEGV_ACCERR:
                    return "Invalid permissions for mapped object";
                default:
                    return "Segmentation violation";
            }
            break;
    }

    return "None";
}

// 用于保存旧监听
struct sigaction sa_old;

/**
 * 型号监听函数
 *
 * 此函数方案：
 * 1. 新栈中
 * 2. 新线程中
 * 3. 新进程中（父子进程可以使用管道通信）
 *
 * @param code
 * @param si
 * @param sc
 */

static void my_handler(const int code, siginfo_t *const si, void *const sc) {
    LOGE("sign is %d,%s", code, print_sig(si->si_signo, si->si_code));

    // 设置旧的监听
    sigaction(SIGSEGV, &sa_old, NULL);
    // 防止循环
    signal(code, SIG_DFL);
    signal(SIGALRM, SIG_DFL);
    (void) alarm(8);
}

/**
 * 设置额外堆栈
 */
void init_stack() {
    stack_t stack;
    stack.ss_size = SIGSTKSZ;
    stack.ss_sp = malloc(stack.ss_size);
    stack.ss_flags = 0;
    if (stack.ss_sp != NULL && sigaltstack(&stack, NULL) == 0) {
        LOGI("%s", "stack success");
    } else {
        LOGE("%s", "stack failure");
    }
}

/**
 * 信号处理监听
 */
void init_sigaction() {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sigemptyset(&sa.sa_mask);
    sa.sa_sigaction = static_cast<void (*)(int, siginfo *, void *)>(my_handler);
    sa.sa_flags = SA_ONSTACK | SA_SIGINFO;
    sigaction(SIGSEGV, &sa, &sa_old);// 此处保存了就监听
}

jint initCrash(JNIEnv *env, jobject jobj, jint arg) {
    LOGI("%s", "initCrash");
    init_stack();
    init_sigaction();
    return 0;
}


jint testCrash(JNIEnv *env, jobject jobj, jint arg) {
    LOGI("%s", "testCrash");

    return 0;
}

jint testStaticCrash(JNIEnv *jniEnv, jclass jcls, int arg) {
    LOGI("%s", "testStaticCrash");
    // 引起crash
    volatile int *a = (int *) (NULL);
    *a = 1;
    return 0;
}

static const char *JNIUTIL_CLASS_NAME = "com/pvr/picotest/NativeInterface";

static JNINativeMethod methods[] = {
        {"testCrash",       "(I)I", (void *) testCrash},
        {"testStaticCrash", "(I)I", (void *) testStaticCrash},
        {"initCrash",       "(I)I", (void *) initCrash}
};

jint registerNativeMethods(JNIEnv *jniEnv, const char *class_name, JNINativeMethod *method,
                           int num_methods) {
    jclass targetClass = jniEnv->FindClass(class_name);
    if (NULL != targetClass) {
        return jniEnv->RegisterNatives(targetClass, method, num_methods);
    }

    return JNI_ERR;
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("%s", "jni init load");
    JNIEnv *jniEnv = NULL;
    if (JNI_OK != vm->GetEnv(reinterpret_cast<void **>(&jniEnv), JNI_VERSION_1_4)) {
        return JNI_ERR;
    }
    int numbers = sizeof(methods) / sizeof(methods[0]);
    LOGI("number=%d", numbers);
    if (JNI_OK != registerNativeMethods(jniEnv, JNIUTIL_CLASS_NAME, methods, numbers)) {
        LOGI("%s", "register error");
        return JNI_ERR;
    }

    LOGI("%s", "jni init success");
    return JNI_VERSION_1_4;

}


}
