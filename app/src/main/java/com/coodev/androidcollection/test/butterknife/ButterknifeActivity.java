package com.coodev.androidcollection.test.butterknife;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.coodev.androidcollection.R;

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
