package com.coodev.androidcollection.Utils.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO使用主要是三块:
 * 1. Channel 可以用于读取和写入 , 相当于一个管道
 * 2. Buffer 主要是用于缓存数据 , 用于读写和移动读写
 * 3. Selector 可以监听多个Channel(概念和IO多路复用中的selector相似)
 * <p>
 * 还有一个超大文件处理 MappedByteBuffer
 */
public class NIOUtil {
    public static final String TAG = NIOUtil.class.getSimpleName();

    /**
     * 读取显示一个文件
     *
     * @param filePath
     */
    public void readPrint(String filePath) {
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(filePath, "r");
            FileChannel fileChannel = accessFile.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int read = -1;
            while ((read = fileChannel.read(buffer)) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.println((char) buffer.get());
                }
                buffer.compact();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(accessFile);
        }
    }

    /**
     * copy文件,使用transferFrom/trasnferTo
     *
     * @param targetFilPath
     * @param fromFilePath
     */
    public void transfer(String targetFilPath, String fromFilePath) {
        RandomAccessFile targetFile = null;
        RandomAccessFile fromFile = null;
        try {
            targetFile = new RandomAccessFile(targetFilPath, "rw");
            fromFile = new RandomAccessFile(fromFilePath, "rw");
            final FileChannel targetFileChannel = targetFile.getChannel();
            final FileChannel fromFileChannel = fromFile.getChannel();
            targetFileChannel.transferFrom(fromFileChannel, 0, fromFileChannel.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(targetFile);
            IOUtils.close(fromFile);

        }
    }


    /**
     * 创建读写管道
     *
     * @return
     */
    public AbstractSelectableChannel[] getPipe() {
        try {
            final Pipe pipe = Pipe.open();
            final Pipe.SinkChannel sink = pipe.sink();// 写管道
            final Pipe.SourceChannel source = pipe.source();// 读管道
            AbstractSelectableChannel[] channels = new AbstractSelectableChannel[2];
            channels[0] = sink;
            channels[1] = source;
            return channels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * configureBlocking可以设置是否阻塞
     * <p>
     * <p>
     * ByteBuffer {@link ByteBuffer}部分方法说明：
     * position()：将要读取或者写入的下一个位置
     * capacity()：容量
     * limit(): 可访问数据的末尾位置，读写无法超过这个位置
     * clear(): 将position设置为0，并将limit设置为最大容量capacity。数据并没有删除，
     * 可以是个get方法或者改变位置和限度进行读取。
     * reset(): 将当前位置设置为mark位置
     * rewind(): 将position置为0，不改变limit
     * flip(): limit设置为当前position位置，position位置再置为0
     * remainning(): 返回缓冲区当前位置和限度位置之间的元素个数
     * compact(): 如果buffer还有未读取的数据,但此时想写入数据,并且读数据不进行覆盖,可以用此方法
     * 此方法表示:将剩余未读数据移动到buffer开始位置,position设置到数据的后一位,limit设置成capacity
     *
     * @param host
     * @param port
     * @param configBlocking 是否阻塞
     */
    public void createTCPSampleNIOClient(String host, int port, boolean configBlocking) {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
            SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
            //socketChannel.connect(inetSocketAddress);
            // read是否是阻塞的，默认是阻塞的
            socketChannel.configureBlocking(configBlocking);
            // 必须使用ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(74);
            WritableByteChannel writableByteChannel = Channels.newChannel(System.out);

            while (true) {
                int readNum = socketChannel.read(byteBuffer);
                if (readNum > 0) {
                    byteBuffer.flip();// 相当于重置指针位置到所读数组的开头
                    writableByteChannel.write(byteBuffer);// 不必关系数据长度，缓冲区会记录
                    byteBuffer.clear();
                } else if (readNum == -1) {
                    System.out.println("read result = -1 , check system");
                    break;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 简易NIO服务端
     *
     * @param port
     */
    public void createTCPSampleNIOServer(int port) {

        ServerSocketChannel serverSocketChannel;
        Selector selector;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            ServerSocket socket = serverSocketChannel.socket();
            socket.bind(new InetSocketAddress(port));

            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();// Selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);//服务端监听新链接
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        while (true) {
            try {
                selector.select();// 会等待
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();//从集合中删除，从而不会处理两次。相当于告诉selector我们已经处理过了
                try {
                    if (next.isAcceptable()) {// 服务通道
                        ServerSocketChannel server = (ServerSocketChannel) next.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted client form : " + client);
                        client.configureBlocking(false);
                        // 加入selector，监听写
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE);
                        ByteBuffer allocate = ByteBuffer.allocate(74);
                        allocate.put((byte) 1);// 放入数据
                        clientKey.attach(allocate);// 挂着buffer,也可以使用其他的数据来进行处理/识别
                    } else if (next.isWritable()) {// 客户端通道
                        SocketChannel client = (SocketChannel) next.channel();
                        ByteBuffer buffer = (ByteBuffer) next.attachment();
                        if (!buffer.hasRemaining()) {//检查是否还有剩余未写的数据
                            buffer.rewind();// 用下一行数据填充缓冲区
                            byte b = buffer.get();
                            buffer.rewind();
                            buffer.put((byte) 1);// 写入数据
                            buffer.flip();
                        }
                        client.write(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    IOUtils.close(next.channel());
                }
            }
        }

    }


    public static void createUDPSampleNIOClient(int port) {
        try {
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.connect(new InetSocketAddress(port));
            //如果是服务端，就用bind

            Selector selector = Selector.open();
            datagramChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);


            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            int n = 0;
            int numbersRead = 0;
            while (true) {
                if (numbersRead == 100) {
                    break;
                }
                // 为一个链接等待一分钟
                selector.select(60 * 1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                if (selectionKeys.isEmpty() && n == 100) {
                    // 所有包已经写入
                    break;
                } else {
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            byteBuffer.clear();
                            datagramChannel.read(byteBuffer);
                            byteBuffer.flip();
                            int echo = byteBuffer.getInt();
                            System.out.println("Read:" + echo);
                            numbersRead++;
                        }

                        if (key.isWritable()) {
                            byteBuffer.clear();
                            byteBuffer.putInt(n);
                            byteBuffer.flip();
                            datagramChannel.write(byteBuffer);
                            System.out.println("Wirte:" + n);
                            n++;
                            if (n == 100) {
                                // 如果所有包已经写入，切换只读模式
                                key.interestOps(SelectionKey.OP_READ);
                            }
                        }
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
