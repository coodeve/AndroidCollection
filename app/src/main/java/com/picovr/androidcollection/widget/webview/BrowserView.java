package com.picovr.androidcollection.widget.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.picovr.androidcollection.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * webview封装
 */
public class BrowserView extends WebView implements DownloadListener {
    public static final String TAG = BrowserView.class.getSimpleName();
    private static final String WEBVIEW_TAG = "web_bridge";
    private Context mContext;
    private WebSettings mWebSettings;
    private OnWebViewClientStatusListener listener;
    private WebHelper mWebHelper;
    private FrameLayout mFrameLayout;
    private List<String> mListImgSrc;

    public BrowserView(Context context) {
        super(context);
        initView(context);
    }

    public BrowserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public BrowserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    /**
     * 设置网页播放全屏播放的container
     *
     * @param frameLayout
     */
    public void setVideoFullScreen(FrameLayout frameLayout) {
        mFrameLayout = frameLayout;
    }

    @SuppressLint("JavascriptInterface")
    private void initView(Context context) {
        this.mContext = context;

        mWebHelper = new WebHelper(mContext);

        initSetting();

        setWebViewClient(mWebViewClient);
        setWebChromeClient(mWebChromeClient);
        setDownloadListener(this);
        addJavascriptInterface(this, WEBVIEW_TAG);
        resume();

    }

    private void initSetting() {
        //声明WebSettings子类
        mWebSettings = getSettings();

        //如果访问的页面中要与Javascript交互，则webView必须设置支持Javascript
        //若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        //需要在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true
        mWebSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        //将图片调整到适合webView的大小
        mWebSettings.setUseWideViewPort(true);
        //缩放至屏幕的大小
        mWebSettings.setLoadWithOverviewMode(true);

        //缩放操作
        //支持缩放，默认为true
        mWebSettings.setSupportZoom(false);
        //设置内置的缩放控件。若为false，则该WebView不可缩放
        mWebSettings.setBuiltInZoomControls(true);
        //隐藏原生的缩放控件
        mWebSettings.setDisplayZoomControls(true);

        //设置可以访问文件
        mWebSettings.setAllowFileAccess(true);

        //允许在WebView中访问内容URL
        mWebSettings.setAllowContentAccess(true);

        //设置WebView运行中的一个文件方案被允许访问其他文件方案中的内容，默认值true
        mWebSettings.setAllowFileAccessFromFileURLs(true);

        //设置WebView运行中的脚本可以是否访问任何原始起点内容，默认true
        mWebSettings.setAllowUniversalAccessFromFileURLs(true);

        //支持通过JS打开新窗口
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //支持自动加载图片
        mWebSettings.setLoadsImagesAutomatically(true);

        //设置编码格式
        mWebSettings.setDefaultTextEncodingName("utf-8");

        //是否使用缓存
        mWebSettings.setAppCacheEnabled(false);

        //关闭webView中缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        //数据库存储API可用
        mWebSettings.setDatabaseEnabled(true);

        //DOM Storage （优酷视频播放必备）
        mWebSettings.setDomStorageEnabled(true);

        //https
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //代表支持多窗口打开
        mWebSettings.setSupportMultipleWindows(true);


    }

    public void resume() {
        mWebSettings.setJavaScriptEnabled(true);
        onResume();
        resumeTimers();
    }

    public void pause() {
        onPause();
        pauseTimers();
        mWebSettings.setJavaScriptEnabled(false);
    }

    @Override
    public void destroy() {
        //让WebView 加载null内容，然后移除 WebView，再销毁 WebView，最后置空
        loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        clearHistory();

        ((ViewGroup) getParent()).removeView(this);
        removeAllViews();
        super.destroy();
    }


    /**
     * 设置webView监听
     *
     * @param listener
     */
    public void setOnWebViewClientStatusListener(OnWebViewClientStatusListener listener) {
        this.listener = listener;
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        /**
         *
         * @param view      WebView
         * @param request   WebResourceRequest
         * @return 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (url == null) {
                return false;
            }
            Log.i(TAG, "shouldOverrideUrlLoading: " + url);
            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(intent);
                    return true;
                }
            } catch (Exception e) {
                //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return false;
            }
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            //super.doUpdateVisitedHistory(view, url, isReload);
            view.clearHistory();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (listener != null) {
                listener.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (listener != null) {
                listener.onPageFinished(view, url);
            }

            parseHTML(view);
            addImageClickListener();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (listener != null) {
                listener.onReceivedError(view, request, error);
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (listener != null) {
                listener.onReceivedHttpError(view, request, errorResponse);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (listener != null) {
                listener.onReceivedSslError(view, handler, error);
            }
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            Log.i(TAG, "onCreateWindow: " + isDialog + "," + isUserGesture + "，" + resultMsg.toString());
            WebActivity.sMessage = resultMsg;
            Intent intent = new Intent(mContext, WebActivity.class);
            mContext.startActivity(intent);
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (listener != null) {
                listener.onProgressChanged(view, newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            if (listener != null) {
                listener.onReceivedTitle(view, title);
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if (mFrameLayout != null) {
                BrowserView.this.setVisibility(GONE);
                mFrameLayout.addView(view);
                mFrameLayout.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mFrameLayout != null) {
                BrowserView.this.setVisibility(VISIBLE);
                mFrameLayout.removeAllViews();
                mFrameLayout.setVisibility(GONE);
            }
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(mContext)
                    .setTitle("JsAlert")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(mContext)
                    .setTitle("JsConfirm")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
            final EditText et = new EditText(mContext);
            et.setText(defaultValue);
            new AlertDialog.Builder(mContext)
                    .setTitle(message)
                    .setView(et)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm(et.getText().toString());
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    })
                    .setCancelable(false)
                    .show();
            return true;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            // super.onPermissionRequest(request);//必须要注视掉
            request.grant(request.getResources());
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mWebHelper.setUploadFile(uploadMsg);
            mWebHelper.showOptions();
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mWebHelper.setUploadFile(uploadMsg);
            mWebHelper.showOptions();
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mWebHelper.setUploadFile(uploadMsg);
            mWebHelper.showOptions();
        }

        // For Android  >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            mWebHelper.setUploadFiles(filePathCallback);
            mWebHelper.setFileChooserParams(fileChooserParams);
            mWebHelper.showOptions();
            return true;
        }

    };


    /**
     * webview下载
     *
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength
     */
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mWebHelper != null) {
            mWebHelper.onActivityForResult(requestCode, resultCode, data);
        }
    }

    @JavascriptInterface
    public void showImage(String url) {
        Log.i(TAG, "showImage: " + url);
        Intent intent = new Intent();
        intent.putExtra("image", url);
        //listImgSrc 该参数为页面所有图片对应的 url
        intent.putStringArrayListExtra("url_all", (ArrayList) mListImgSrc);
        intent.setClass(mContext, WebPageScannerActivity.class);
        mContext.startActivity(intent);
    }

    @JavascriptInterface
    public void parseSource(String html) {
        Log.i(TAG, "parseSource: " + html);
        getImageUrlsFromHtml(html);
    }

    private void parseHTML(WebView view) {
        //这段 js 代码是解析获取到了 HTML 文本文件，然后调用本地定义的 Java 代码返回
        //解析出来的 HTML 文本文件
        view.loadUrl("javascript:window." + WEBVIEW_TAG + ".parseSource(''+"
                + "document.getElementsByTagName('html')[0].innerHTML+'');");
    }


    public  List<String> getImageUrlsFromHtml(String htmlCode) {
        mListImgSrc = new ArrayList<String>();
        Pattern p = Pattern.compile("<img\\b[^>]*\\bsrc\\b\\s*=\\s*('|\")?([^'\"\n\r\f>]+(\\.jpg|\\.bmp|\\.eps|\\.gif|\\.mif|\\.miff|\\.png|\\.tif|\\.tiff|\\.svg|\\.wmf|\\.jpe|\\.jpeg|\\.dib|\\.ico|\\.tga|\\.cut|\\.pic|\\b)\\b)[^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlCode);
        String quote = null;
        String src = null;
        while (m.find()) {
            quote = m.group(1);
            src = (quote == null || quote.trim().length() == 0) ? m.group(2).split("//s+")[0] : m.group(2);
            mListImgSrc.add(src);
        }
        if (mListImgSrc.size() == 0) {
            Log.e("imageSrcList", "资讯中未匹配到图片链接");
            return null;
        }
        return mListImgSrc;
    }

    private void addImageClickListener() {
        this.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)" +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window." + WEBVIEW_TAG + ".showImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    /**
     * 状态监听
     */
    public interface OnWebViewClientStatusListener {

        /**
         * 页面的title
         *
         * @param view
         * @param title title内容
         */
        void onReceivedTitle(WebView view, String title);

        /**
         * 页面开始
         *
         * @param view
         * @param url
         * @param favicon
         */
        void onPageStarted(WebView view, String url, Bitmap favicon);

        /**
         * 加载进度变化
         *
         * @param view
         * @param newProgress
         */
        void onProgressChanged(WebView view, int newProgress);

        /**
         * 页面结束
         *
         * @param view
         * @param url
         */
        void onPageFinished(WebView view, String url);

        /**
         * 收到错误
         *
         * @param view
         * @param request
         * @param error
         */
        void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);

        /**
         * http错误
         *
         * @param view
         * @param request
         * @param errorResponse
         */
        void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse);

        /**
         * ssl错误
         *
         * @param view
         * @param handler
         * @param error
         */
        void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error);

        /**
         * 下载监听
         *
         * @param url
         * @param userAgent
         * @param contentDisposition
         * @param mimetype
         * @param contentLength
         */
        void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);

    }

}
