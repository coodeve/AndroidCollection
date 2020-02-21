package com.picovr.androidcollection;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import java.security.DigestInputStream;


public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Button button;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

}
