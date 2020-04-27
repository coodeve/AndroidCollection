package com.picovr.androidcollection.Utils.io;

import android.text.TextUtils;

import java.io.File;

public class FileUtil {

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

}
