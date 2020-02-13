package com.picovr.androidcollection.widget.x5;

import android.net.Uri;

import java.util.Iterator;
import java.util.Set;

/**
 * @author patrick.ding
 * @since 19/9/17
 */
public class HttpAction extends Action {
    public HttpAction(Uri uri) {
        super(uri);
    }

    @Override
    public boolean action(String key, String value) {
        return false;
    }

    public String getOritation() {
        Set<String> queryParameterNames = this.uri.getQueryParameterNames();
        Iterator<String> iterator = queryParameterNames.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = this.uri.getQueryParameter(key);
            if (ORIENTATION.equals(key)) {
                return value;
            }
        }
        return null;
    }

    @Override
    String getUrl() {
        return uri.toString();
    }


}
