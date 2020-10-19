package com.picovr.androidcollection.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.picovr.androidcollection.Utils.log.Logs;


/**
 * @author patrick.ding
 * @since 18/11/2
 */
public class LinkTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = LinkTextView.class.getSimpleName();
    private Context mContext;

    public LinkTextView(Context context) {
        this(context, null);
    }

    public LinkTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LinkTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void updateText(String sourceText) {
        // 1.设置参数
        setText(sourceText);
        setMovementMethod(LinkMovementMethod.getInstance());
        // 2.获取参数处理
        CharSequence text = this.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) text;
            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
            if (urlSpans.length == 0) {
                Logs.w(TAG, "updateText# no url");
                return;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            // 循环遍历并拦截 所有http://开头的链接
            for (URLSpan uri : urlSpans) {
                String url = uri.getURL();
                if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
                    CustomUrlSpan customUrlSpan = new CustomUrlSpan(mContext, url);
                    spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
                            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
            setText(spannableStringBuilder);
        }
    }


    public class CustomUrlSpan extends ClickableSpan {

        private Context context;
        private String url;

        public CustomUrlSpan(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            Log.i(TAG, "onClick# " + url);

        }
    }
}
