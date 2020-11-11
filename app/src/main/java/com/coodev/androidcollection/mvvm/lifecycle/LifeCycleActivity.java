package com.coodev.androidcollection.mvvm.lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.coodev.androidcollection.R;

public class LifeCycleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_cycle);
        // use lifecycle , same in activity and fragment
        getLifecycle().addObserver(new LocationObserver());
    }
}
