package com.coodev.androidcollection.Utils.security;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * @author patrick.ding
 * @since 19/12/18
 */
public class RSAUtil {


    public static final String RSA = "RSA";// 非对称加密密钥算法
    public static final String ECB_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
    /**
     * 长度是512~INT.MAX，但一般都是使用2048
     */
    public static int KEY_SIZE = 2048;// 密钥位数
    /**
     * RSA算法规定：
     * 待加密的字节数不能超过密钥的长度值除以8再减去11。
     * 而加密后得到密文的字节数，正好是密钥的长度值除以 8。
     */
    public static int RESERVE_BYTES = 11;
    public static int DECRYPT_BLOCK = KEY_SIZE / 8;
    public static int ENCRYPT_BLOCK = DECRYPT_BLOCK - RESERVE_BYTES;
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    /**
     * 随机生成RSA密钥对
     * KeyPair中，可以通过getPrivate和getPublic获取和公钥和私钥
     *
     * @param password 生成密钥对的密码
     */
    public static KeyPair generateKeyPair(String password) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            if (password == null) {
                kpg.initialize(KEY_SIZE);
            } else {
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
                secureRandom.setSeed(password.getBytes(CHARSET));
                kpg.initialize(KEY_SIZE, secureRandom);
            }
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 注意，使用base64，Android和java的方式不一样
     * 获取base64编码的私钥字符串
     *
     * @param privateKey 私钥
     * @return base64编码的私钥字符串
     */
    public static String getPrivateKeyStr(PrivateKey privateKey) {
        return new String(Base64.getEncoder().encode(privateKey.getEncoded()));
    }

    /**
     * 获取base64编码的公钥字符串
     *
     * @param publicKey 公钥
     * @return base64编码的公钥字符串
     */
    public static String getPublicKyeStr(PublicKey publicKey) {
        return new String(Base64.getEncoder().encode(publicKey.getEncoded()));
    }

    /**
     * @param data 原始数据
     * @param key  base64的公钥key
     * @return byte数组
     * @throws Exception exception
     */
    public static byte[] encryptWithPublicKey(byte[] data, byte[] key) throws Exception {
        Cipher cp = Cipher.getInstance(ECB_PADDING);
        cp.init(Cipher.ENCRYPT_MODE, getPublicKey(key));
        return cp.doFinal(data);
    }

    /**
     * 用公钥对字符串进行加密
     *
     * @param data      原文
     * @param publicKey base64编码的公钥
     * @return base64编码的加密字符串
     * @throws Exception exception
     */
    public static String encryptWithPublicKey(String data, String publicKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(publicKey.getBytes(CHARSET));
        byte[] encrypt = encryptWithPublicKey(data.getBytes(CHARSET), key);
        return new String(Base64.getEncoder().encode(encrypt));
    }


    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  密钥
     */
    public static byte[] decryptWithPublicKey(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(key));
        return cipher.doFinal(data);
    }


    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 密钥
     */
    public static String decryptWithPublicKey(String data, String publicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey.getBytes(CHARSET));
        byte[] dataBytes = Base64.getDecoder().decode(data.getBytes(CHARSET));
        byte[] decrypt = decryptWithPublicKey(dataBytes, publicKeyBytes);
        return new String(decrypt, CHARSET);
    }

    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key  密钥
     */
    public static byte[] encryptWithPrivateKey(byte[] data, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data       原文
     * @param privateKey base64编码的私钥
     * @return base64编码的字符串
     * @throws Exception
     */
    public static String encryptWithPrivateKey(String data, String privateKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(privateKey.getBytes(CHARSET));
        byte[] encrypt = encryptWithPrivateKey(data.getBytes(CHARSET), key);
        return new String(Base64.getEncoder().encode(encrypt));
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  base64编码的密钥
     */
    public static byte[] decryptWithPrivateKey(byte[] data, byte[] key) throws Exception {
        Cipher cp = Cipher.getInstance(ECB_PADDING);
        cp.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
        return cp.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data       base64格式的字符串
     * @param privateKey base64格式的私钥
     * @return 原始字符串
     * @throws Exception exception
     */
    public static String decryptWithPrivateKey(String data, String privateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey.getBytes(CHARSET));
        byte[] dataBytes = Base64.getDecoder().decode(data.getBytes(CHARSET));
        byte[] decrypt = decryptWithPrivateKey(dataBytes, privateKeyBytes);
        return new String(decrypt, CHARSET);
    }

    /**
     * 获取公钥key
     *
     * @param key base64编码的key
     * @return PublicKey 一般是{@link java.security.interfaces.RSAPublicKey}
     * @throws Exception exception
     */
    private static PublicKey getPublicKey(byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 获取私钥
     *
     * @param key base64编码的key
     * @return PrivateKey 一般是{@link java.security.interfaces.RSAPrivateKey}
     * @throws Exception exception
     */
    private static PrivateKey getPrivateKey(byte[] key) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 分块加密，公钥加密
     *
     * @param data 原始数据
     * @param key  base64的密钥
     */
    public static byte[] encryptWithPublicKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / ENCRYPT_BLOCK);

        if ((data.length % ENCRYPT_BLOCK) != 0) {
            blockCount += 1;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * ENCRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(key));

        for (int offset = 0; offset < data.length; offset += ENCRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > ENCRYPT_BLOCK) {
                inputLen = ENCRYPT_BLOCK;
            }
            byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(encryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块加密，私钥加密
     *
     * @param data 原始数据
     * @param key  base64的密钥
     */
    public static byte[] encryptWithPrivateKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / ENCRYPT_BLOCK);

        if ((data.length % ENCRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * ENCRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));

        for (int offset = 0; offset < data.length; offset += ENCRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > ENCRYPT_BLOCK) {
                inputLen = ENCRYPT_BLOCK;
            }
            byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(encryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块解密，公钥解密
     *
     * @param data 原始数据
     * @param key  base64的密钥
     */
    public static byte[] decryptWithPublicKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / DECRYPT_BLOCK);
        if ((data.length % DECRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * DECRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(key));
        for (int offset = 0; offset < data.length; offset += DECRYPT_BLOCK) {
            int inputLen = (data.length - offset);
            if (inputLen > DECRYPT_BLOCK) {
                inputLen = DECRYPT_BLOCK;
            }
            byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(decryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }

    /**
     * 分块解密，私钥解密
     *
     * @param data 原始数据
     * @param key  base64的密钥
     */
    public static byte[] decryptWithPrivateKeyBlock(byte[] data, byte[] key) throws Exception {
        int blockCount = (data.length / DECRYPT_BLOCK);
        if ((data.length % DECRYPT_BLOCK) != 0) {
            blockCount += 1;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(blockCount * DECRYPT_BLOCK);
        Cipher cipher = Cipher.getInstance(ECB_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
        for (int offset = 0; offset < data.length; offset += DECRYPT_BLOCK) {
            int inputLen = (data.length - offset);

            if (inputLen > DECRYPT_BLOCK) {
                inputLen = DECRYPT_BLOCK;
            }

            byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
            bos.write(decryptedBlock);
        }

        bos.close();
        return bos.toByteArray();
    }


    /**
     * 使用N、e值还原公钥
     *
     * @param modulus        字符串N see {@link RSAPublicKey#getModulus()}
     * @param publicExponent 字符串e see{@link RSAPublicKey#getPublicExponent()}
     * @return 构造好的公钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  NoSuchAlgorithmException
     */
    public static PublicKey getPublicKey(String modulus, String publicExponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 使用N、d值还原私钥
     *
     * @param modulus         字符串N see {@link RSAPrivateKey#getModulus()}
     * @param privateExponent 字符串d see {@link RSAPrivateKey#getPrivateExponent()}
     * @return 构造好的私钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException  NoSuchAlgorithmException
     */
    public static PrivateKey getPrivateKey(String modulus, String privateExponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger bigIntModulus = new BigInteger(modulus);
        BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

}
