package com.picovr.androidcollection.test;

import com.picovr.androidcollection.entity.DefaultTestAction;
import com.picovr.androidcollection.entity.TestAction;

import java.util.ArrayList;
import java.util.List;

public class TestActionFactory {
    private static List<TestAction> mTestActions = new ArrayList<>();

    static{
        mTestActions.add(new DefaultTestAction("测试", 0));
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
