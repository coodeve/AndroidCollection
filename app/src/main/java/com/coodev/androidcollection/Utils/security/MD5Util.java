package com.coodev.androidcollection.Utils.security;

import java.security.MessageDigest;

public class MD5Util {
    private static final String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
    private static final String CHARSET_DEFAULT = "utf-8";

    public static String md5(String origin) {
        return md5(origin, CHARSET_DEFAULT);
    }

    /**
     * create md5
     *
     * @param origin source string
     * @return value of md5
     */
    public static String md5(String origin, String charsetName) {
        String resultString = origin;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetName == null || "".equals(charsetName)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetName)));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return resultString;
    }

    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder resultSb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
            resultSb.append(byteToHexString(bytes[i]));
        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }


}
