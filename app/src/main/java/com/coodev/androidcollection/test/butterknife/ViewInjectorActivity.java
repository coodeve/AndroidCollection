package com.coodev.androidcollection.test.butterknife;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coodev.androidcollection.R;
import com.coodev.app_apt.annotation.ViewInjector;

// 在支持路由的页面上添加注解(必选)
// 这里的路径需要注意的是至少需要有两级，/xx/xx
@Route(path = "test/ViewInjectorActivity")
public class ViewInjectorActivity extends AppCompatActivity {

    @ViewInjector(R.id.tv_test)
    TextView mTextView;

    @ViewInjector(R.id.btn_test)
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_butterknife);

    }

}
