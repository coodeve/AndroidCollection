package com.picovr.androidcollection.Utils.io;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

    /**
     * 获取uri地址
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数：authority 需要和清单文件中配置的保持完全一致：${applicationId}.xxx
            fileUri = FileProvider.getUriForFile(context, context.getPackageName(), file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    /**
     * 删除文件或文件夹
     * @param file
     */
    public static void delete(File file) {
        if (file.exists() && file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        if (files.length == 0) {
            file.delete();
        } else {
            for (File childFile : files) {
                delete(childFile);
            }
            file.delete();
        }

    }

    /**
     * 文件复制
     * @param source
     * @param target
     */
    public static void copy(File source,File target){
        try{
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(target));
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(buffer,0,len);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
