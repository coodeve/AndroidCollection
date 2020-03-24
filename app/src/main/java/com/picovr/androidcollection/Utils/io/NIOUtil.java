package com.picovr.androidcollection.Utils.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOUtil {
    public static final String TAG = NIOUtil.class.getSimpleName();

    /**
     * configureBlocking可以设置是否阻塞
     * <p>
     * <p>
     * Bytebuffer {@link ByteBuffer}部分方法说明：
     *      position()：将要读取或者写入的下一个位置
     *      capacity()：容量
     *      limit(): 可访问数据的末尾位置，读写无法超过这个位置
     *      clear(): 将位置设置为0，并将limit设置为最大容量capacity。数据并没有删除，可以是个get方法或者改变位置和限度进行读取。
     *      reset(): 将当前位置设置为mark位置
     *      rewind(): 将位置置为0，不改变limit
     *      flip(): limit设置为当前位置，位置再置为0
     *      remainning(): 返回缓冲区当前位置和限度位置之间的元素个数
     *
     * @param host
     * @param port
     * @param configBlocking
     */
    public void createSampleNIOClient(String host, int port, boolean configBlocking) {
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
            SocketChannel socketChannel = SocketChannel.open(inetSocketAddress);
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
    public void createSampleNIOServer(int port) {

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
                        clientKey.attach(allocate);
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


}
