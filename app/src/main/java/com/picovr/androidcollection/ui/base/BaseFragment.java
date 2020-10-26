package com.picovr.androidcollection.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.picovr.androidcollection.R;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * @author patrick.ding
 * @since 20/2/24
 */
public abstract class BaseFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    /**---------------------AppFragmentManager 部分参数------------------------**/
    /**
     * fragment模拟回退栈的启动模式
     */
    private int stackMode = AppFragmentManager.STACK_MODE_CLEAR_TOP;
    /**
     * fragment模拟回退栈，当fragment回退时，是否remove掉
     */
    private boolean canRemove = false;
    /**
     * fragment模拟回退栈，id
     */
    public String id;
    /**
     * fragment模拟回退栈，类型
     */
    public int type;

    public void setMode(String id, int type) {
        this.id = id;
        this.type = type;
    }

    /**
     * 重载数据,用于存在的Fragment的界面刷新工作
     *
     * @param args
     */
    public void reload(Bundle args) {

    }

    public void setStackMode(int stackMode) {
        this.stackMode = stackMode;
    }

    public int getStackMode() {
        return stackMode;
    }

    public boolean isCanRemove() {
        return canRemove;
    }

    public void setCanRemove(boolean canRemove) {
        this.canRemove = canRemove;
    }

    /**--------------------AppFragmentManager 部分参数-------------------------**/

    protected Context mContext;


    /**
     * fragment根视图
     */
    private View mRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(attachLayoutRes(), container, false);
        initViews(mRootView);
        return mRootView;
    }

    protected <T extends View> T findViewById(int resId) {
        if (mRootView == null) {
            return null;
        }

        return mRootView.findViewById(resId);
    }

    protected abstract int attachLayoutRes();

    protected abstract void initViews(View rootView);

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static final int REQUEST_PERMISSION_CODE = 123;

    public boolean checkPermission(String permission) {
        return EasyPermissions.hasPermissions(getContext(), permission);
    }

    public void requestPermission(String rationsl, String... permissions) {
        EasyPermissions.requestPermissions(this, rationsl, REQUEST_PERMISSION_CODE, permissions);
    }

    public void requestPermissionDialog(String rationsl, String... permissions) {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, REQUEST_PERMISSION_CODE, permissions)
                        .setRationale(rationsl)
                        .setPositiveButtonText("ok")
                        .setNegativeButtonText("cancel")
                        .setTheme(R.style.AppTheme)
                        .build());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
        // ...

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(getContext(), "has permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
