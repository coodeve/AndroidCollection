package com.picovr.androidcollection.widget.x5;

import android.net.Uri;

/**
 * @author patrick.ding
 * @since 18/11/20
 */
public abstract class Action {
    public static final String ORIENTATION = "orientation";
    public static final String VERTICAL = "vertical";

    protected final Uri uri;

    public Action(Uri uri) {
        this.uri = uri;
    }

    abstract boolean action(String key, String value);

    abstract String getOritation();

    abstract String getUrl();
}
