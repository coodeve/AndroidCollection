package com.coodev.androidcollection.Utils.system;

import android.content.Context;
import android.content.pm.PackageManager;

public class PermissionUtils {
    /**
     * 检查是否拥有该权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }


}
