package com.coodev.androidcollection.Utils.io;

import org.xutils.common.util.IOUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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

    /**
     * 解压压缩文件
     * 压缩大量小文件时，建议使用ZipInputStream
     *
     * @param sourceFile
     * @param targetDir
     */
    public static void zipReadByZipInput(File sourceFile, File targetDir) {
        FileInputStream fis = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fis = new FileInputStream(sourceFile);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fis));
            bufferedInputStream = new BufferedInputStream(zipInputStream);
            byte[] buffer = new byte[8192];
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File zipFile = new File(targetDir, zipEntry.getName());
                FileOutputStream fos = new FileOutputStream(zipFile);
                int count = 0;
                while ((count = bufferedInputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
            IOUtils.close(bufferedInputStream);
        }

    }

    /**
     * 加压压缩文件
     *
     * @param sourceFile
     * @param targetDir
     */
    public static void zipReadByZipFile(File sourceFile, File targetDir) {
        ZipFile zipFile = null;
        InputStream in = null;
        FileOutputStream fileOutputStream = null;
        try {
            zipFile = new ZipFile(sourceFile);
            byte[] bytes = new byte[8192];
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry zipEntry;
            while (entries.hasMoreElements()) {
                zipEntry = entries.nextElement();
                fileOutputStream = new FileOutputStream(new File(targetDir, zipEntry.getName()));
                // 低压缩率，比如文本
                if (zipEntry.getName().endsWith(".txt")) {
                    in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    int count = 0;
                    while ((count = in.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, count);
                    }
                } else {// 高压缩率比如，图片
                    in = zipFile.getInputStream(zipEntry);
                    int count = 0;
                    while ((count = in.read(bytes)) != -1) {
                        fileOutputStream.write(bytes, 0, count);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean unzip(File file, File outDir) throws IOException {
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            return false;
        }
        if (!outDir.exists()) {
            boolean mkResult = outDir.mkdirs();
            if (!mkResult) {
                return false;
            }
        }

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(file);
            int len = 0;
            byte[] bytes = new byte[1024];
            ZipEntry entry = null;
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();

                File out = new File(outDir, entry.getName());
                if (entry.isDirectory()) {
                    if (!out.exists()) {
                        out.mkdirs();
                    }
                } else {
                    InputStream in = zipFile.getInputStream(entry);
                    if (!out.exists()) {
                        out.getParentFile().mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));

                    while ((len = in.read(bytes)) > 0) {
                        bos.write(bytes, 0, len);
                    }
                    bos.flush();
                    bos.close();
                    in.close();
                }
            }
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 压缩字节码
     *
     * @param source 原数据
     * @return 压缩后的数据
     */
    public static byte[] decompress(byte[] source) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            IOUtil.copy(new GZIPInputStream(new ByteArrayInputStream(source)), byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    /**
     * 压缩字节码
     *
     * @param source 元数据
     * @return 压缩后的数据
     */
    public static byte[] compress(byte[] source) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source)) {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IOUtil.copy(byteArrayInputStream, new GZIPOutputStream(byteArrayOutputStream));
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
