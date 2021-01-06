package com.coodev.androidcollection.Utils.system;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.coodev.androidcollection.Utils.common.SPUtils;

public class PermissionUtils {
    public static final int PERMISSION_REQUEST_CODE = 10;
    public static final String PERMISSION_KEY = "permission";
    public static final String PERMISSION_ARRAY_KEY = "permission_array";


    public interface PermissionListener {
        /**
         * 权限申请通过
         *
         * @param permission
         */
        void onPermissionGranted(String permission);

        /**
         * 权限申请拒绝,没有点击Never ask again
         *
         * @param permission
         */
        void onPermissionDenied(String permission);

        /**
         * 权限申请拒绝,点击了Never ask again
         *
         * @param permission
         */
        void onPermissionDeniedNeverAskAgain(String permission);
    }

    /**
     * 检查是否拥有该权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.checkPermission(permission, context.getPackageName())
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkSelfPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void requestPermission(FragmentActivity activity, String permission, PermissionListener permissionListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    // 用户在对话框中选择拒绝权限,但是没有点击Never ask again 的选项
                    // 这个情况下就重新请求权限
                    requestPermissionWrap(activity, permission, permissionListener);
                } else {
                    // 用户在对话框中选择拒绝权限,而且点击Never ask again 的选项
                    // 这个情况下,一般有两种情况,1.用户从来没有申请过此权限,2.拒绝 并Never ask again
                    // 需要提示用户申请权限才能继续进行,跳转setting进行手动设置权限
                    // 因为返回false情况比较多,可以通过sp进行辅助判断
                    if (isFirstRequestPermission(activity, permission)) {
                        setFirstRequestPermission(activity, permission);
                        requestPermissionWrap(activity, permission, permissionListener);
                    } else {
                        // 需要提示用户申请权限才能继续进行,跳转setting进行手动设置权限
                        if (permissionListener != null) {
                            permissionListener.onPermissionDeniedNeverAskAgain(permission);
                        }
                    }

                }
            } else {
                if (permissionListener != null) {
                    permissionListener.onPermissionGranted(permission);
                }
            }
        }
    }

    private static boolean isFirstRequestPermission(Context context, String permission) {
        final int value = SPUtils.getValue(context, permission, 0);
        return value == 0;
    }

    private static void setFirstRequestPermission(Context context, String permission) {
        SPUtils.setValue(context, permission, 1);
    }

    private static void requestPermissionWrap(FragmentActivity activity, String permission, PermissionListener permissionListener) {
        final PermissionFragment permissionFragment = new PermissionFragment();
        permissionFragment.setPermissionListener(permissionListener);
        final Bundle bundle = new Bundle();
        bundle.putString(PERMISSION_KEY, permission);
        permissionFragment.setArguments(bundle);
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, permissionFragment)
                .commit();
    }

    private static class PermissionFragment extends Fragment {
        private PermissionListener permissionListener;
        private String mPermission;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final Bundle arguments = getArguments();
            mPermission = null;
            if ((mPermission = arguments.getString(PERMISSION_KEY)) != null) {
                PermissionFragment.this.requestPermissions(new String[]{mPermission}, PERMISSION_REQUEST_CODE);
            }
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (permissionListener != null && mPermission != null && mPermission.equals(permissions[0])) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionListener.onPermissionGranted(mPermission);
                    } else {
                        permissionListener.onPermissionDenied(mPermission);
                    }

                }
            }
            permissionListener = null;
            removeSelf();
        }

        /**
         * 移除自己
         */
        private void removeSelf() {
            final FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            supportFragmentManager
                    .beginTransaction()
                    .remove(this)
                    .commit();
        }

        public void setPermissionListener(PermissionListener permissionListener) {
            this.permissionListener = permissionListener;
        }
    }
}
