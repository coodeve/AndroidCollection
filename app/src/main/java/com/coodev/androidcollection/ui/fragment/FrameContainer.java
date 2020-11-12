package com.coodev.androidcollection.ui.fragment;

import android.app.PendingIntent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkRequest;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.coodev.androidcollection.R;


/**
 * Navigation 使用
 * App bar支持：ActionBar，Toolbar，CollapsingToolbarLayout
 * 导航菜单支持：DrawLayout+NavigationView，BottomNavigationView
 * 支持深层连接：DeepLink
 */
public class FrameContainer extends AppCompatActivity {

    private NavController mNavController;
    private NavHostFragment mNavHostFragment;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);// xml处理navigation

    }

    /**
     * 处理activity的appbar
     */
    private void initAppBar(AppCompatActivity activity) {
        mAppBarConfiguration = new AppBarConfiguration.Builder(mNavController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(activity, mNavController, mAppBarConfiguration);
    }

    /**
     * 动态加载navigation的fragment
     */
    private void dynamicGenerateNavigationFragment() {
        mNavHostFragment = NavHostFragment.create(R.navigation.jetpack_navigation);
        mNavController = mNavHostFragment.getNavController();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host, mNavHostFragment)
                .setPrimaryNavigationFragment(mNavHostFragment) // this is the equivalent to app:defaultNavHost="true"
                .commit();
    }

    /**
     * 导航控制跳转 id 方式跳转
     *
     * @param id
     */
    private void navigationFragmentID(int id) {
        mNavController.navigate(id, new Bundle());
    }


    /**
     * 导航控制跳转 id 方式跳转
     *
     * @param id
     */
    private void navigationFragmentNavOptions(int id) {
        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(android.R.anim.slide_in_left)
                .setExitAnim(android.R.anim.slide_out_right)
                .setPopEnterAnim(android.R.anim.slide_in_left)
                .setPopExitAnim(android.R.anim.slide_out_right)
                .build();
        mNavController.navigate(id, new Bundle(), navOptions);
    }

    private void addActivityNavigator() {
        ActivityNavigator activityNavigator = new ActivityNavigator(getApplicationContext());
        mNavController.getNavigatorProvider().addNavigator(activityNavigator);
    }


    /**
     * 导航控制跳转 ，深层连接跳转
     * 起始就是url匹配跳转
     * 此种方式需要在xml中设置 <deepLink/>标签,同时Activity中设置<nav-graph/>标签
     *
     * @param url
     */
    private void navigationFragmentDeepLink(String url) {
        NavDeepLinkRequest navDeepLinkRequest = NavDeepLinkRequest.Builder.fromUri(Uri.parse("coodev://com.coodev/fragment")).build();
        NavHostFragment.findNavController(mNavHostFragment).navigate(navDeepLinkRequest);
    }

    /**
     * 创建DeepLink的PendingIntent
     * 此种方式不需要在xml中设置 <deepLink/>标签
     *
     * @param graphID
     * @param destinationID
     * @return
     */
    private PendingIntent createDeepLinkPendingIntent(int graphID, int destinationID) {
        return mNavController.createDeepLink()
                .setArguments(null)
                .setGraph(graphID)
                .setDestination(destinationID)
                .createPendingIntent();
    }

    /**
     * 前进
     */
    private void forward() {
        mNavController.navigateUp();
    }

    /**
     * 后退
     */
    private void back() {
        if (!mNavController.popBackStack()) {
            finish();
        }
    }

}
