package com.picovr.androidcollection.ui.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDeepLinkRequest;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavOptions;
import androidx.navigation.NavType;
import androidx.navigation.fragment.NavHostFragment;

import com.picovr.androidcollection.R;

public class FrameContainer extends AppCompatActivity {

    private NavController mNavController;
    private NavHostFragment mNavHostFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);// xml处理navigation

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
     *
     * @param url
     */
    private void navigationFragmentDeepLink(String url) {
        NavDeepLinkRequest navDeepLinkRequest = NavDeepLinkRequest.Builder.fromUri(Uri.parse("coodev://com.coodev/fragment")).build();
        NavHostFragment.findNavController(mNavHostFragment).navigate(navDeepLinkRequest);
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
