package com.picovr.androidcollection.Utils.io;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

    /**
     * 获取应用名称
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * 向一个文件写入字符串
     * 会覆盖原有数据
     *
     * @param file
     * @param value
     */
    private static void write(File file, String value) {

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(value);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 读取一个文件，以字符串形式返回
     *
     * @param file
     * @return
     */
    public static String read(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            char[] buff = new char[256];
            int len = 0;
            while ((len = fileReader.read(buff)) != -1) {
                stringBuilder.append(buff, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileReader);
        }
        return stringBuilder.toString();
    }
}
