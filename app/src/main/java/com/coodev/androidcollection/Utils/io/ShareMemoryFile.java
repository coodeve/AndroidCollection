package com.coodev.androidcollection.Utils.io;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Properties;

/**
 * 基于Java,文件映射到内存
 */
public class ShareMemoryFile {
    private int mFileLen = 1024 * 1204 * 4;                    //开辟共享内存大小
    private int mFileSize = 0;                          //文件的实际大小
    private String mShareFileName;                   //共享内存文件名
    private String mSharePath;                       //共享内存路径
    private MappedByteBuffer mapBuf = null;         //定义共享内存缓冲区
    private FileChannel mFileChannel = null;                  //定义相应的文件通道
    private FileLock mFileLock = null;                     //定义文件区域锁定的标记。
    private Properties mProperties = null;
    private RandomAccessFile mRandomAccessFile = null;         //定义一个随机存取文件对象

    public ShareMemoryFile(String sharePath, String shareFileName) {
        if (!StringUtils.isEmpty(sharePath)) {
            FileUtils.createOrExistsDir(sharePath);
            this.mSharePath = sharePath + File.separator;
        } else {
            this.mSharePath = sharePath;
        }

        this.mShareFileName = shareFileName;

        try {
            mRandomAccessFile = new RandomAccessFile(this.mSharePath + this.mShareFileName, "rw");
            mFileChannel = mRandomAccessFile.getChannel();
            mFileSize = (int) mFileChannel.size();//获取实际文件的大小
            if (mFileSize < mFileLen) {
                byte[] byteRemain = new byte[mFileLen - mFileSize]; //计算剩余的内存大小
                final ByteBuffer buffer = ByteBuffer.wrap(byteRemain);
                buffer.clear();
                mFileChannel.position(mFileSize);//设置此通道的文件位置。
                mFileChannel.write(buffer);  //将字节序列从给定的缓冲区写入此通道。
                mFileChannel.force(false);
                mFileSize = mFileLen;
            }
            //将此通道的文件区域直接映射到内存中。
            mapBuf = mFileChannel.map(FileChannel.MapMode.READ_WRITE, 0, mFileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ps   锁定区域开始的位置；必须为非负数
     * @param len  锁定区域的大小；必须为非负数
     * @param buff 写入的数据
     * @return len
     */
    public synchronized int write(int ps, int len, byte[] buff) {
        //fsize为文件的实际大小，在这里为0
        if (ps >= mFileSize || ps + len >= mFileSize) {
            return 0;
        }
        //定义文件区域锁定的标记。
        FileLock fl = null;
        try {
            //获取此通道的文件给定区域上的锁定。
            //position：锁定文件中的开始位置
            //size: 锁定文件中的内容长度
            //shared: 是否使用共享锁。true为共享锁定；false为独占锁定
            fl = mFileChannel.lock(ps, len, false);
            if (fl != null) {
                //mapBuf定义共享内存缓冲区，.position(ps)设置当前操作位置
                mapBuf.position(ps);
                //创建字节缓冲区
                ByteBuffer byteBuffer = ByteBuffer.wrap(buff);
                //此方法将给定源缓冲区中的剩余字节传输到此缓冲区中
                mapBuf.put(byteBuffer);
                //释放此锁定。
                fl.release();

                return len;
            }
        } catch (Exception e) {
            if (fl != null) {
                try {
                    fl.release();
                } catch (IOException e1) {
                    System.out.println(e1.toString());
                }
            }
        }

        return 0;
    }

    /**
     * @param ps   锁定区域开始的位置；必须为非负数
     * @param len  锁定区域的大小；必须为非负数
     * @param buff 要取的数据
     * @return len
     */
    public synchronized int read(int ps, int len, byte[] buff) {
        if (ps >= mFileSize) {
            return 0;
        }
        //定义文件区域锁定的标记。
        FileLock fl = null;
        try {
            fl = mFileChannel.lock(ps, len, false);
            if (fl != null) {
                //System.out.println( "ps="+ps );
                mapBuf.position(ps);
                if (mapBuf.remaining() < len) {
                    len = mapBuf.remaining();
                }

                if (len > 0) {
                    mapBuf.get(buff, 0, len);
                }

                fl.release();

                return len;
            }
        } catch (Exception e) {
            if (fl != null) {
                try {
                    fl.release();
                } catch (IOException e1) {
                    System.out.println(e1.toString());
                }
            }
            return 0;
        }

        return 0;
    }

    /**
     * 完成，关闭相关操作
     */
    protected void finalize() throws Throwable {
        if (mFileChannel != null) {
            try {
                mFileChannel.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            mFileChannel = null;
        }

        if (mRandomAccessFile != null) {
            try {
                mRandomAccessFile.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            mRandomAccessFile = null;
        }
        mapBuf = null;
    }

    /**
     * 关闭共享内存操作
     */
    public synchronized void closeSMFile() {
        if (mFileChannel != null) {
            try {
                mFileChannel.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            mFileChannel = null;
        }

        if (mRandomAccessFile != null) {
            try {
                mRandomAccessFile.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            mRandomAccessFile = null;
        }
        mapBuf = null;
    }

    /**
     * 检查退出
     *
     * @return true-成功，false-失败
     */
    public synchronized boolean checkToExit() {
        byte[] buffer = new byte[1];

        if (read(1, 1, buffer) > 0) {
            if (buffer[0] == 1) {
                return true;

            }
        }

        return false;
    }

    /**
     * 复位退出
     */
    public synchronized void resetExit() {
        byte[] buffer = new byte[1];

        buffer[0] = 0;
        write(1, 1, buffer);

    }

    /**
     * 退出
     */
    public synchronized void toExit() {
        byte[] buff = new byte[1];

        buff[0] = 1;
        write(1, 1, buff);

    }
}
