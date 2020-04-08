// ICallback.aidl
package com.pvr.verify;

// Declare any non-default types here with import statements

interface ICallback {
    // 0 标识验证通过，其他值为错误码
    void callback(int code);
}
