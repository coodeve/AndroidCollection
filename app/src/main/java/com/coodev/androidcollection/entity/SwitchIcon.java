package com.coodev.androidcollection.entity;

import com.coodev.androidcollection.App;
import com.coodev.androidcollection.MainActivity;
import com.coodev.androidcollection.Utils.common.PackageUtil;
import com.coodev.androidcollection.ui.SplashActivity;

public class SwitchIcon extends TestAction {

    public SwitchIcon(String name, int position) {
        super(name, position);
    }

    @Override
    public void action() {
        PackageUtil.switchIconTask(
                SplashActivity.class.getCanonicalName(),
                App.getContext().getPackageName() + ".MainAliasActivity");
    }
}
