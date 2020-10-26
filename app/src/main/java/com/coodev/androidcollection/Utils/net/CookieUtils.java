package com.coodev.androidcollection.Utils.net;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;

public class CookieUtils {

    /**
     * 适用于java
     * Android web需要查看{@link android.webkit.CookieManager}
     */
    public static void setCookieDefault() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);// 只接受第一方的cookie
        CookieHandler.setDefault(cookieManager);
    }

    /**
     * 自定义过滤cookie，比如过滤百度的cookie
     */
    private static class MyCookiePolicy implements CookiePolicy{

        @Override
        public boolean shouldAccept(URI uri, HttpCookie cookie) {
            if(uri.getAuthority().toLowerCase().endsWith("baidu.com")
                    || cookie.getDomain().toLowerCase().endsWith("baidu.com")){
                return false;
            }
            return true;
        }
    }

}
