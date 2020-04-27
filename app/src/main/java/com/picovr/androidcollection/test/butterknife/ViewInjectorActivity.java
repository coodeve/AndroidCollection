package com.picovr.androidcollection.test.butterknife;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.annotation.anno.ViewInjector;
import com.picovr.androidcollection.R;

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
