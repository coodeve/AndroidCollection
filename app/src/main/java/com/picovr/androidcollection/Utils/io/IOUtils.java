package com.picovr.androidcollection.Utils.io;

import java.io.Closeable;

public class IOUtils {

    public static void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
