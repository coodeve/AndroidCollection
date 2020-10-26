package com.coodev.androidcollection.test;

import com.coodev.androidcollection.App;
import com.coodev.androidcollection.Utils.common.JNIUtil;
import com.coodev.androidcollection.Utils.crash.NativeCrash;
import com.coodev.androidcollection.entity.DefaultTestAction;
import com.coodev.androidcollection.entity.TestAction;

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
