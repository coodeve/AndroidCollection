package com.picovr.androidcollection;

import android.app.ActivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.donkingliang.labels.LabelsView;
import com.picovr.androidcollection.Utils.ui.ToastUtil;
import com.picovr.androidcollection.entity.TestAction;
import com.picovr.androidcollection.test.TestActionFactory;

public class MainActivity extends FragmentActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    /**
     * 标签
     */
    private LabelsView mLabelsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLabelsView = findViewById(R.id.labels);
        init();
    }


    private void init() {
        ActivityManager am;
        mLabelsView.setLabels(TestActionFactory.getAll(), new LabelsView.LabelTextProvider<TestAction>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, TestAction data) {
                return data.getName();
            }
        });

        mLabelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                if (data instanceof TestAction) {
                    TestAction testAction = (TestAction) data;
                    ToastUtil.showShort(MainActivity.this.getApplicationContext(), testAction.getName());
                    testAction.action();

                }
            }
        });
    }

}
