package com.coodev.androidcollection.Utils.io;

import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * 基于MemoryFile
 */
public class MemorySharedUtils {

    private MemoryFile memoryFile;
    private FileDescriptor mFileDescriptor;
    private ParcelFileDescriptor mParcelable;

    public void createMemoryFile(String fileName, int length) {
        try {
            memoryFile = new MemoryFile(fileName, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取文件描述符
     *
     * @return
     */
    public FileDescriptor getFD() {
        if (memoryFile == null) {
            return null;
        }


        try {
            Method getFileDescriptor = MemoryFile.class.getDeclaredMethod("getFileDescriptor");
            mFileDescriptor = (FileDescriptor) getFileDescriptor.invoke(memoryFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mFileDescriptor;
    }

    /**
     * 用于跨进程传递
     *
     * @return
     * @throws IOException
     */
    public Parcelable getParcelable() throws IOException {
        if (memoryFile == null) {
            return null;
        }
        mParcelable = ParcelFileDescriptor.dup(getFD());
        return mParcelable;
    }

    /**
     * 写入数据
     *
     * @param buffer
     * @param srcOffset
     * @param destOffset
     * @param count
     */
    public void write(byte[] buffer, int srcOffset, int destOffset, int count) {
        if (memoryFile == null) {
            return;
        }
        try {
            memoryFile.writeBytes(buffer, srcOffset, destOffset, count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public OutputStream getOutput() {
        return memoryFile.getOutputStream();
    }

    public InputStream getInput() {
        return memoryFile.getInputStream();
    }

    /**
     * 从ParcelFileDescriptor获取文件描述符，并打开读取
     *
     * @param fileDescriptor
     * @return
     */
    public InputStream getInput(ParcelFileDescriptor fileDescriptor) {
        return new FileInputStream(fileDescriptor.getFileDescriptor());
    }


    public void close() {
        if (mParcelable != null) {
            IOUtils.close(mParcelable);
        }

        if (memoryFile != null) {
            memoryFile.close();
            memoryFile = null;
        }
    }
}
