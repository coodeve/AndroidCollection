package com.picovr.androidcollection.test.butterknife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.picovr.androidcollection.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ButterknifeActivity extends AppCompatActivity {

    @BindView(R.id.tv_test)
    TextView mTextView;

    @BindView(R.id.btn_test)
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_butterknife);
        ButterKnife.bind(this);
    }

}
