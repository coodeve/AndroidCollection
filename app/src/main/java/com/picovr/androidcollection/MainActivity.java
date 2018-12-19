package com.picovr.androidcollection;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
<<<<<<< HEAD

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private Button button;
=======
import android.widget.TextView;


public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private String[] pathArr = new String[]{"a","b","c","e","f","g","h","i","j","k"};

    private int index;

    private Button add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        add = findViewById(R.id.add);
    }

    private String randomStr(){
        if(index <10){
            return pathArr[index++];
        }
            return "";
    }

}
