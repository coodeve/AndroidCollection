package com.coodev.androidcollection.Utils.security;

import com.blankj.utilcode.util.ConvertUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 对称加密工具
 */
public class AESUtil {
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    public static final String CHARSET = "UTF-8";
    public static final int KEY_SIZE = 128;

    static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";
    /*
     * AES/CBC/NoPadding 要求
     * 密钥必须是16字节长度的；Initialization vector (IV) 必须是16字节
     * 待加密内容的字节长度必须是16的倍数，如果不是16的倍数，就会出如下异常：
     * javax.crypto.IllegalBlockSizeException: Input length not multiple of 16 bytes
     *
     *  由于固定了位数，所以对于被加密数据有中文的, 加、解密不完整
     *
     *  可 以看到，在原始数据长度为16的整数n倍时，假如原始数据长度等于16*n，则使用NoPadding时加密后数据长度等于16*n，
     *  其它情况下加密数据长 度等于16*(n+1)。在不足16的整数倍的情况下，假如原始数据长度等于16*n+m[其中m小于16]，
     *  除了NoPadding填充之外的任何方 式，加密数据长度都等于16*(n+1).
     *  如果一定要使用这种padding，而无法保证内容为16字节的倍数，可以考虑自实现一套规则，补全内容长度为16*n
     */
    static final String CIPHER_ALGORITHM_CBC_NoPadding = "AES/CBC/NoPadding";

    /**
     * 对称密钥
     */
    public static String KEY = "1234567890123456";
    /**
     * 随机数
     */
    public static String IV = "1234567890123456";

    public AESUtil() {
    }

    /**
     * 加密,模式 AES/ECB/PKCS5Padding
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return 加密后的byte[]
     */
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom(password.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);// 创建密码器
            byte[] byteContent = content.getBytes(CHARSET);
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            return cipher.doFinal(byteContent);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密,模式 AES/ECB/PKCS5Padding
     *
     * @param content  待解密内容
     * @param password 解密密钥
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom(password.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            return cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 加密，默认模式 AES/CBC/PKCS5Padding
     *
     * @param content 元数据
     * @param key     key
     * @param iv      随机数
     * @return 加密后的数据
     */
    public static byte[] encrypt(String content, String key, String iv) {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(new SecureRandom(key.getBytes()));
            final SecretKey secretKey = keyGenerator.generateKey();
            final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
            cipher.doFinal(content.getBytes(CHARSET));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                | InvalidKeyException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 解密，默认模式 AES/CBC/PKCS5Padding
     *
     * @param content 元数据
     * @param key     key
     * @param iv      随机数,和加密时保持一致
     * @return 加密后的数据
     */
    public static byte[] decrypt(byte[] content, String key, String iv) {
        try {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(new SecureRandom(key.getBytes()));
            final SecretKey secretKey = keyGenerator.generateKey();
            final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes()));
            cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String byte2HexString(byte[] bytes) {
        return ConvertUtils.bytes2HexString(bytes);
    }

    public static byte[] hexString2Byte(String hexString) {
        return ConvertUtils.hexString2Bytes(hexString);
    }

}
