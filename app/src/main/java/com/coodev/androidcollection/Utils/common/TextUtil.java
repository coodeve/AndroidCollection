package com.coodev.androidcollection.Utils.common;

import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
     * @param textView
     * @param fontPath
     */
    public void setFontFromAssets(TextView textView, String fontPath) {
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

    /**
     * 关键字高亮
     *
     * @param color
     * @param text
     * @param keyword
     * @return
     */
    public static SpannableString matcherTextHighLight(int color, String text, String[] keyword) {
        SpannableString s = new SpannableString(text);
        for (int i = 0, len = keyword.length; i < len; i++) {
            Pattern compile = Pattern.compile(keyword[i]);
            Matcher matcher = compile.matcher(s);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                s.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return s;
    }

    /**
     * 匹配0.0 ，0，0.00字符串
     *
     * @param total
     * @return
     */
    public static boolean matchZero(String total) {
        if (TextUtils.isEmpty(total)) {
            return false;
        }
        String pattern = "^0$|^0.0+$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(total);
        return m.find();
    }

    /**
     * 匹配数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 语言翻译，将标准语言字符（比如，zh，zh-HK）换成系统对应翻译（多语言）
     *
     * @param lang
     * @return
     */
    public static String translateLanguage(List<String> lang) {
        String defaultStr = "—— ——";
        if (lang == null || lang.size() <= 0) {
            return defaultStr;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, len = lang.size(); i < len; i++) {
            String language = lang.get(i);
            Locale locale = Locale.forLanguageTag(language);
            builder.append(locale.getDisplayName());
            if (i < len - 1) {
                builder.append(",");
            }
        }

        return builder.toString();
    }

    public static String translateLanguage(String lang) {
        return Locale.forLanguageTag(lang).getDisplayName();
    }

    /**
     * 格式化json
     *
     * @param json 字符串
     * @return
     */
    public static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 格式化xml
     *
     * @param xml 字符串
     * @return
     */
    public static String formatXml(String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            final StreamResult xmlOut = new StreamResult(new StringWriter());
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amout", "4");
            transformer.transform(xmlInput, xmlOut);
            xml = xmlOut.getWriter().toString().replaceFirst(">", ">" + System.lineSeparator());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }
}
