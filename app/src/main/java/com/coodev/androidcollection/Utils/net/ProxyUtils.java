package com.coodev.androidcollection.Utils.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ProxyUtils {
    /**
     * eg：
     * System.setProperty(http.proxyHost, "192.168.254.12");
     * System.setProperty(http.proxyPort, "9000");
     * <p>
     * System.setProperty(http.nonProxyHosts, "test.server.com|test.coo.com|*.library.global");
     *
     * @param tag 固定的字符串
     *            http:  http.proxyHost , http.proxyPort , http.nonProxyHosts
     *            ftp:   ftp.proxyHost , ftp.proxyPort , ftp.nonProxyHosts
     *            socks: socksProxyHosts , socksProxyPort
     * @param uri uri形式的字符串
     */
    public static void setProxy(String tag, String uri) {
        System.setProperty(tag, uri);
    }


    public static void setDefault(LocalProxySelector localProxySelector) {
        if (localProxySelector == null) {
            throw new IllegalArgumentException("localProxySelector is null");
        }

        ProxySelector.setDefault(localProxySelector);
    }

    /**
     * 每个java虚拟机都有一个ProxySelector，用来确定不同的链接代理服务器
     * 默认的只检查系统属性和url的协议，来决定连接到不同的主机
     * <p>
     * 我们继承子类，自己决定连接主机的方式
     */
    private static class LocalProxySelector extends ProxySelector {

        private List<URI> failed = new ArrayList<>();

        @Override
        public List<Proxy> select(URI uri) {
            List<Proxy> list = new ArrayList<>();
            if (failed.contains(uri)
                    || !"http".equalsIgnoreCase(uri.getScheme())) {
                list.add(Proxy.NO_PROXY);
            } else {
                InetSocketAddress inetSocketAddress = new InetSocketAddress("baidu.com", 8080);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
                list.add(proxy);
            }
            return list;
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            failed.add(uri);
        }
    }

}
