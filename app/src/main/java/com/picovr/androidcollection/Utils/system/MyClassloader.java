package com.picovr.androidcollection.Utils.system;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author patrick.ding
 * @since 19/10/16
 */
public class MyClassloader extends ClassLoader {
    private String path;

    public MyClassloader(String path) {
        this.path = path;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = null;
        byte[] classData = loadClassDate(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        clazz = defineClass(name, classData, 0, classData.length);
        return clazz;
    }

    private byte[] loadClassDate(String name) {
        String fileName = getFileName(name);
        File file = new File(path, fileName);
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[64];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                close(in);
            }
            if (out != null) {
                close(out);
            }
        }
        return new byte[0];
    }

    private String getFileName(String name) {
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return name + ".class";
        }

        return name.substring(index + 1) + ".class";
    }

    private void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
