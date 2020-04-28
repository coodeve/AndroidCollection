package com.picovr.androidcollection.Utils.common;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.graphics.TypefaceCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class TextUtil {
    /**
     * 是否为空
     *
     * @param string
     * @return
     */
    public boolean isEmpty(String string) {
        return null == string || "".equals(string);
    }

    /**
     * 将转义后的html文本变成正常字符串
     * html文本，就是html编码形式，比如 < 会变成 &lt;
     *
     * @param html
     * @return
     */
    public Spanned html(String html) {
        return Html.fromHtml(html, FROM_HTML_MODE_LEGACY);
    }

    /**
     * 以html编码形式的字符串
     *
     * @param source
     * @return
     */
    public String htmlEncode(String source) {
        return TextUtils.htmlEncode(source);
    }

    /**
     * 从assest中的字体进行设置
     *
     * @param context
     * @param fontPath
     */
    public void setFontFromAssest(TextView textView, String fontPath) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), fontPath));
    }

    /**
     * 从一个文件路径的字体进行设置
     *
     * @param textView
     * @param fontPath
     */
    public void setFontFromFile(TextView textView, String fontPath) {
        textView.setTypeface(Typeface.createFromFile(fontPath));
    }


}
