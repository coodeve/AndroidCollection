package com.picovr.androidcollection.widget.webview;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.picovr.androidcollection.R;
import com.picovr.androidcollection.ui.dialog.BaseBottomDialog;

public class FileChooserDialog extends BaseBottomDialog implements View.OnClickListener {
    private TextView mPhoto;
    private TextView mCamera;
    private TextView mCancel;
    private WebHelper webHelper;

    private boolean hasOptions;

    @Override
    protected int specifyLayout(Bundle savedInstanceState) {
        return R.layout.webview_filechooser;
    }

    @Override
    protected void initView(View root) {
        mPhoto = root.findViewById(R.id.tv_photo);
        mCamera = root.findViewById(R.id.tv_camera);
        mCancel = root.findViewById(R.id.tv_cancel);
        mPhoto.setOnClickListener(this);
        mCamera.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!hasOptions && webHelper != null) {
                    webHelper.onActivityForResult(0,Activity.RESULT_CANCELED,null);
                }
            }
        });
    }

    @Override
    protected boolean canceledOnTouchOutside() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                if (webHelper != null) {
                    webHelper.openCamera();
                }
                break;
            case R.id.tv_photo:
                if (webHelper != null) {
                    webHelper.openGallery();
                }
                break;
            case R.id.tv_cancel:
                if (webHelper != null) {
                    webHelper.onActivityForResult(0, Activity.RESULT_CANCELED, null);
                }
                break;
            default:
                break;
        }
        hasOptions = true;
        dismiss();
    }


    public void setWebHelper(WebHelper webHelper) {
        this.webHelper = webHelper;
    }

}
