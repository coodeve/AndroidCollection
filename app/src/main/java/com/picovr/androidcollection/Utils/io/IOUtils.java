package com.picovr.androidcollection.Utils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class IOUtils {

    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载小文件
     *
     * @param url
     * @throws IOException
     */
    public static void saveBinaryFile(URL url) throws IOException {
        URLConnection urlConnection = url.openConnection();
        String contentType = urlConnection.getContentType();
        int contentLength = urlConnection.getContentLength();
        if (contentType.startsWith("text/") || contentLength == -1) {
            throw new IOException("This is not a binary file");
        }
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = urlConnection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte buffer[] = new byte[contentLength];
            int offset = 0;
            int read = 0;
            while (offset < contentLength) {
                read = bufferedInputStream.read(buffer, offset, buffer.length - offset);
                if (read == -1) {
                    break;
                }
                offset += read;
            }

            if (offset != contentLength) {
                throw new IOException("Only read" + offset + "bytes;Expected " + contentLength + "bytes");
            }

            String fileName = url.getFile();
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            fileOutputStream = new FileOutputStream(fileName);
            fileOutputStream.write(buffer);
            fileOutputStream.flush();
        } catch (Exception e) {
            close(inputStream);
            close(fileOutputStream);
        }

    }

    /**
     * 字节转为文件，比如字节转bitmap
     *
     * @param bytes
     * @param filePath
     * @return
     */
    public static File saveBinaryFile(byte[] bytes, String filePath) {
        File file = new File(filePath);
        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            output = new FileOutputStream(file);
            bufferedOutput = new BufferedOutputStream(output);
            bufferedOutput.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(bufferedOutput);
        }
        return file;
    }
}
