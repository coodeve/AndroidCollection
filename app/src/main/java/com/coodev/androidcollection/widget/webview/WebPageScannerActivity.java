package com.coodev.androidcollection.widget.webview;

import android.app.Activity;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.coodev.androidcollection.R;

import java.util.List;

public class WebPageScannerActivity extends Activity {
    private ViewPager vpImageBrowser;
    private TextView tvImageIndex;//显示滑动页数
    private ImageBrowserAdapter adapter;
    private List<String> imgUrls;//WebView 页面所有图片 URL
    private String url;//WebView 页面所有图片中被点击图片对应 URL
    private int currentIndex;//标记被滑动图片在所有图片中的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_from_web);
        initView();
        initData();
    }

    private void initView() {
        vpImageBrowser =  findViewById(R.id.vp_image_browser);
        tvImageIndex =  findViewById(R.id.tv_image_index);
    }

    private void initData() {
        imgUrls = getIntent().getStringArrayListExtra("url_all");
        url = getIntent().getStringExtra("image");
        //获取被点击图片在所有图片中的位置
        int position = imgUrls.indexOf(url);
        adapter = new ImageBrowserAdapter(this, imgUrls);
        vpImageBrowser.setAdapter(adapter);
        final int size = imgUrls.size();
        if (size > 1) {
            tvImageIndex.setVisibility(View.VISIBLE);
            tvImageIndex.setText((position + 1) + "/" + size);
        } else {
            tvImageIndex.setVisibility(View.GONE);
        }
        vpImageBrowser.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                currentIndex = arg0;
                int index = arg0 % size;
                tvImageIndex.setText((index + 1) + "/" + size);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
        vpImageBrowser.setCurrentItem(position);
    }

}
