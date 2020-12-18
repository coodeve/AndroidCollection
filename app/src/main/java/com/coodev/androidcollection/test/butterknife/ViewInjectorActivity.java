package com.coodev.androidcollection.test.butterknife;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.coodev.androidcollection.R;
import com.coodev.app_apt.annotation.ViewInjector;


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
