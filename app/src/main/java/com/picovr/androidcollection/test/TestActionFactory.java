package com.picovr.androidcollection.test;

import android.util.Log;

import com.picovr.androidcollection.App;
import com.picovr.androidcollection.Utils.common.JNIUtil;
import com.picovr.androidcollection.Utils.crash.NativeCrash;
import com.picovr.androidcollection.entity.DefaultTestAction;
import com.picovr.androidcollection.entity.TestAction;

import java.util.ArrayList;
import java.util.List;

public class TestActionFactory {
    public static final String TAG = TestActionFactory.class.getSimpleName();
    private static List<TestAction> mTestActions = new ArrayList<>();

    private static class NativeCrashTest extends TestAction {

        public NativeCrashTest(String name, int position) {
            super(name, position);
        }

        @Override
        public void action() {
            JNIUtil.init();
            NativeCrash.init(App.getContext());
            NativeCrash.nativeCrashTest();
        }
    }


    static {
        mTestActions.add(new DefaultTestAction("测试", 0));
        mTestActions.add(new NativeCrashTest("nativeCrash", 1));

    }

    public static void add(TestAction testAction) {
        mTestActions.add(testAction);
    }

    public static TestAction get(int index) {
        return mTestActions.get(index);
    }

    public static List<TestAction> getAll() {
        return mTestActions;
    }

}
