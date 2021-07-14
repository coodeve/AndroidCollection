package com.coodev.androidcollection;

import com.coodev.androidcollection.Utils.security.AESUtil;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        final AESUtil desUtil = new AESUtil();
        final String source = "Example local unit test, which will execute on the development machine (host)";
        System.out.println("原始的的数据：" + source);
        final byte[] crypt = AESUtil.encrypt(source, desUtil.KEY);
        final String cryptString = AESUtil.byte2HexString(crypt);
        System.out.println("加密后的数据：" + cryptString);
        final byte[] decrypt = AESUtil.decrypt(AESUtil.hexString2Byte(cryptString), desUtil.KEY);
        final String decryptString = new String(decrypt);
        System.out.println("解密后的数据：" + decryptString);

    }
}