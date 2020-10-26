package com.picovr.androidcollection.ui.base;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author patrick.ding
 * @since 19/4/4
 */
public final class AppFragmentManager {

    public static final String TAG = AppFragmentManager.class.getSimpleName();
    /**
     * 会清除栈顶除了自己的其他fragment
     */
    public static final int STACK_MODE_CLEAR_TOP = 1;
    /**
     * 正常加入
     */
    public static final int STACK_MODE_STANDARD = 2;

    /**
     * 基于显示隐藏的fragment栈控制
     */
    private List<BaseFragment> mFragmeBackStack = new LinkedList<>();
    /**
     * 最顶层的fragment
     */
    private BaseFragment mCurrentFragment;


    private HashMap<String, BaseFragment> mFragmentHashMap = new HashMap<>(8);

    private int mLayoutID;

    public static AppFragmentManager getInstance() {
        return Holder.instance;
    }

    private FragmentManager fragmentManager;

    private static class Holder {
        private static final AppFragmentManager instance = new AppFragmentManager();
    }

    public void init(FragmentManager fragmentManager, @IdRes int id) {
        this.mLayoutID = id;
        this.fragmentManager = fragmentManager;
        check();
    }


    public void onSaveInstanceState(Bundle outState) {
        if (mCurrentFragment != null && mCurrentFragment.isAdded()) {
            outState.putString("id", mCurrentFragment.id);
            outState.putInt("type", mCurrentFragment.type);
            outState.putBoolean("canRemove", mCurrentFragment.isCanRemove());
            outState.putInt("stackMode", mCurrentFragment.getStackMode());
            outState.putBundle("args", mCurrentFragment.getArguments());
        }
    }

    public void onRestoreInstanceState(Bundle outState) {
        String id = outState.getString("id");
        int type = outState.getInt("type");
        boolean canRemove = outState.getBoolean("canRemove");
        int stackMode = outState.getInt("stackMode");
        Bundle args = outState.getBundle("args");
        showFragment(id, type, stackMode, canRemove, false, args);
    }


    public BaseFragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public void showFragment(String id, int type, Bundle args) {
        BaseFragment baseFragment = get(id, type, args);
        showFragment(id, baseFragment, STACK_MODE_CLEAR_TOP, false, false, args);
    }

    public void showFragment(String id, int type, int stackMode, Bundle args) {
        BaseFragment baseFragment = get(id, type, args);
        if (baseFragment.getArguments() == null) {
            baseFragment.setArguments(args);
        }
        showFragment(id, baseFragment, stackMode, false, false, args);
    }

    public void showFragment(String id, int type, int stackMode, boolean canRemove, Bundle args) {
        BaseFragment baseFragment = get(id, type, args);
        if (baseFragment.getArguments() == null) {
            baseFragment.setArguments(args);
        }
        showFragment(id, baseFragment, stackMode, canRemove, false, args);
    }

    public void showFragment(String id, int type, int stackMode, boolean canRemove, boolean reload, Bundle args) {
        BaseFragment baseFragment = get(id, type, args);
        if (baseFragment.getArguments() == null) {
            baseFragment.setArguments(args);
        }
        showFragment(id, baseFragment, stackMode, canRemove, reload, args);
    }

    private BaseFragment get(String id, int type, Bundle args) {
        BaseFragment baseFragment = mFragmentHashMap.get(id);
        if (baseFragment == null) {
            baseFragment = FragmentFactory.createFragment(type);
            baseFragment.setArguments(args);
            baseFragment.setMode(id, type);
            mFragmentHashMap.put(id, baseFragment);
            Log.i(TAG, "get: 创建Fragment ：" + baseFragment.getClass().getSimpleName());
        }

        return baseFragment;
    }

    /**
     * 显示fragment,可以单独使用，基于显示和隐藏的回退栈控制
     *
     * @param fragment  fragment的类
     * @param stackMode 回退栈的模式{@link #STACK_MODE_CLEAR_TOP#STACK_MODE_STANDARD}
     * @param canRemove 是否回退时移除，再次加入时，会造成重新加载
     * @param reload    是否强制刷新，重新设置fragemnt的bundle参数，并调用{@link BaseFragment#retry()}
     * @param args      传入fragment的参数
     */
    private void showFragment(String tag, Fragment fragment, int stackMode, boolean canRemove, boolean reload, Bundle args) {
        if (!fragment.getClass().getSuperclass().isAssignableFrom(BaseFragment.class)) {
            return;
        }

        if (mCurrentFragment != null) {
            if (mCurrentFragment.getTag() != null && mCurrentFragment.getTag().equals(tag) && mCurrentFragment.isAdded()) {
                Log.i(TAG, "showFragment: reload = " + reload);
                if (reload) {
                    mCurrentFragment.reload(args);
                }
                return;
            }
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (mCurrentFragment != null) {
            fragmentTransaction.hide(mCurrentFragment);
        }

        BaseFragment target = (BaseFragment) fragment;
        Log.i(TAG, "showFragment: add=" + target.isAdded() + ",tag=" + tag + ",object=" + fragmentManager.findFragmentByTag(tag));

        if (!target.isAdded()) {
            if (null == fragmentManager.findFragmentByTag(tag)) {
                fragmentTransaction.add(mLayoutID, target, tag).show(target);
            }

            if (null != fragmentManager.findFragmentByTag(tag)) {
                target = (BaseFragment) fragmentManager.findFragmentByTag(tag);
                BaseFragment source = (BaseFragment) fragment;
                target.id = source.id;
                target.type = source.type;
                mFragmentHashMap.put(tag, target);
                fragmentTransaction.show(target);

            }

        } else {
            fragmentTransaction.show(target);
        }


        // statckmode 处理
        target.setStackMode(stackMode);
        target.setCanRemove(canRemove);

        // 模拟栈清理
        if (stackMode == STACK_MODE_CLEAR_TOP) {
            for (BaseFragment baseFragment : mFragmeBackStack) {
                if (baseFragment == target) {
                    continue;
                }
                if (baseFragment.isCanRemove()) {
                    fragmentTransaction.remove(baseFragment);
                    mFragmentHashMap.remove(baseFragment.id);// 移除缓存，进行重建
                }
            }
            mFragmeBackStack.clear();
        }

        // 添加到最顶部位置
        if (!mFragmeBackStack.contains(target)) {
            mFragmeBackStack.add(0, target);
        } else {
            int i = mFragmeBackStack.indexOf(target);
            if (i != 0) {
                mFragmeBackStack.add(0, mFragmeBackStack.remove(i));
            }
        }

        mCurrentFragment = target;
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    public Map<String, BaseFragment> getFragments() {
        return mFragmentHashMap;
    }

    /**
     * 使用显示隐藏，模拟回退栈
     *
     * @return
     */
    public boolean back() {
        if (mFragmeBackStack.size() <= 1) {// 小于两个时不可回退
            return false;
        }
        BaseFragment top = mFragmeBackStack.remove(0);
        if (top.isCanRemove()) {
            // 释放引用
            mCurrentFragment = null;
            mFragmentHashMap.remove(top.id);
            fragmentManager.beginTransaction().remove(top).commit();
        }
        BaseFragment next = mFragmeBackStack.remove(0);
        showFragment(next.id, next.type, next.getStackMode(), next.getArguments());
        return true;
    }

    private void check() {
        if (fragmentManager == null) {
            throw new NullPointerException(String.format("%s", "FragmentManager is null"));
        }
    }

    /*-------------------------------------------------------------------------------------------*/


    /**
     * fragment回退栈进行回退
     * 显示之前一个fragment
     */
    public void backStack() {
        fragmentManager.popBackStack();
    }

    /**
     * 立即回退到某一个fragment，并清除之上所有的fragment
     *
     * @param fragment        回退到某个fragment
     * @param includeFragment 是否包含这个fragment，如果包含，则此fragment和其上面的fragment都会被弹出
     *                        如果不包含，则弹出此fragment以上的fragment
     */
    public void back(Fragment fragment, boolean includeFragment) {
        fragmentManager.popBackStack(fragment.getClass().getSimpleName(),
                includeFragment ? FragmentManager.POP_BACK_STACK_INCLUSIVE : 0);
    }


    public void showFragment(Fragment fragment, int container) {
        showFragment(fragment, container, false);
    }

    /**
     * 添加一个fragment，并显示
     *
     * @param fragment
     * @param container
     * @param addBackStack
     */
    public void showFragment(Fragment fragment, int container, boolean addBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (!fragment.isAdded()) {
            if (addBackStack) {
                fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
            }
            fragmentTransaction.replace(container, fragment, fragment.getClass().getSimpleName());

        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    /**
     * fragment工厂
     */
    public static class FragmentFactory {

        public static final int TYPE_FEATURE = 1;
        public static final int TYPE_COMMON = 2;
        public static final int TYPE_LOCALMANAGER = 3;
        public static final int TYPE_EMPLORE = 4;
        public static final int TYPE_DETAIL = 5;
        public static final int TYPE_BUNDLE_DETAIL = 6;

        public static BaseFragment createFragment(int type) {
            BaseFragment baseFragment = null;
            switch (type) {
                case TYPE_FEATURE:
//                    baseFragment = new FeatureFragment();
                    break;
                case TYPE_COMMON:
//                    baseFragment = new GameFragment();
                    break;
                case TYPE_LOCALMANAGER:
//                    baseFragment = new LocalManagerFragment();
                    break;
                case TYPE_EMPLORE:
//                    baseFragment = new ExploreFragment();
                    break;
                case TYPE_DETAIL:
//                    baseFragment = new DetailFragment();
                    break;
                case TYPE_BUNDLE_DETAIL:
//                    baseFragment = new BundleFragment();
                    break;

            }

            return baseFragment;
        }
    }

}
