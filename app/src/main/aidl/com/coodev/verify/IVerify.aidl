// IVerify.aidl
package com.coodev.verify;
// Declare any non-default types here with import statements

import com.coodev.verify.ICallback;

interface IVerify {
    // 0 标识验证通过，其他值为错误码
   int verify(String appid,String packageName,String publicKey);

   void verifyAsync(String appid,String packageName,String publicKey,ICallback callback);
}
