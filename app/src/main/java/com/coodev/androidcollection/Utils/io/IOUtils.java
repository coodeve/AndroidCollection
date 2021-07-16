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
import java.util.zip.ZipOutputStream;

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
     * 将目标文件夹中的所有文件（不包含子文件），压缩到目标文件中
     *
     * @param folderPath  要压缩的文件夹绝对路径
     * @param zipFilePath 压缩后的文件绝对路径
     * @throws IOException IOException
     */
    public static void compress(String folderPath, String zipFilePath) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("Folder " + folderPath + " does't exist or isn't a directory");
        }

        File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            File zipFolder = zipFile.getParentFile();
            if (!zipFolder.exists()) {
                if (!zipFolder.mkdirs()) {
                    throw new IOException("Zip folder " + zipFolder.getAbsolutePath() + " not created");
                }
            }
            if (!zipFile.createNewFile()) {
                throw new IOException("Zip file " + zipFilePath + " not created");
            }
        }

        BufferedInputStream bis;
        ZipOutputStream zos = new ZipOutputStream(
                new BufferedOutputStream(new FileOutputStream(zipFile)));
        try {
            final int BUFFER_SIZE = 8 * 1024; // 8K
            byte[] buffer = new byte[BUFFER_SIZE];
            for (String fileName : folder.list()) {
                if (fileName.equals(".") || fileName.equals("..")) {
                    continue;
                }

                File file = new File(folder, fileName);
                if (!file.isFile()) {
                    continue;
                }

                FileInputStream fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(fileName);
                    zos.putNextEntry(entry);
                    int count;
                    while ((count = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        zos.write(buffer, 0, count);
                    }
                } finally {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    /**
     * 下载小文件
     *
     * @param url 下载路径
     * @throws IOException IOException
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
            byte[] buffer = new byte[contentLength];
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
     * @param bytes    字节
     * @param filePath 文件路径
     * @return File文件
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
     * @param sourceFile 原文件
     * @param targetDir  目标文件
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
     * @param sourceFile 原文件
     * @param targetDir  目标文件
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
            Enumeration<? extends ZipEntry> entries =  zipFile.entries();
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
