package com.picovr.androidcollection.widget.webview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;

public class WebHelper {
    public static final String TAG = WebHelper.class.getSimpleName();

    private Context mContext;
    // 相册
    private static final int CHOOSER_GALLERY_REQUEST_CODE = 10;
    // 相机
    private static final int CHOOSER_CAMERA_REQUEST_CODE = 11;
    //拍摄
    private static final int CHOOSER_VIDEO_REQUEST_CODE = 11;


    /**
     * <5.0低版本处理
     */
    private ValueCallback<Uri> uploadFile;
    /**
     * >5.0 处理方式
     */
    private ValueCallback<Uri[]> uploadFiles;
    // >5.0
    private WebChromeClient.FileChooserParams mFileChooserParams;
    private Uri mUri;

    public WebHelper(Context context) {
        mContext = context;
    }

    public void setUploadFile(ValueCallback<Uri> uploadFile) {
        this.uploadFile = uploadFile;
    }

    public void setUploadFiles(ValueCallback<Uri[]> uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public void setFileChooserParams(WebChromeClient.FileChooserParams fileChooserParams) {
        mFileChooserParams = fileChooserParams;
    }


    /**
     * 处理相册或相机操作
     */
    public void showOptions() {
        if (mContext instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) mContext;
            FileChooserDialog fileChooserDialog = new FileChooserDialog();
            fileChooserDialog.setWebHelper(this);
            fileChooserDialog.show(activity);
            return;
        }

        throw new IllegalArgumentException("context must be FragmentActivity");
    }


    void openGallery() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            realOpenCamera();
        } else {
            Toast.makeText(mContext, "您没有授权", Toast.LENGTH_SHORT).show();
        }

    }

    private void realOpenCamera() {
        Intent intent = null;
        Intent chooser = null;
        if (mFileChooserParams != null) {
            intent = mFileChooserParams.createIntent();
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            chooser = Intent.createChooser(intent, mFileChooserParams.getTitle());
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            chooser = Intent.createChooser(intent, "");
        }

        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent, CHOOSER_GALLERY_REQUEST_CODE);
        } else {
            mContext.startActivity(chooser);
        }
    }

    void openCamera() {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        String imgName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                .format(new Date());
        File imgFile = new File(externalFilesDir.getAbsolutePath() + File.separator
                + "IMG_" + imgName + ".jpg");
        mUri = getUriForFile(mContext, imgFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent, CHOOSER_CAMERA_REQUEST_CODE);
            return;
        }

        throw new IllegalArgumentException("context must be activity");
    }


    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //限制时长
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        //开启摄像机
        if (mContext instanceof Activity) {
            ((Activity) mContext).startActivityForResult(intent,CHOOSER_VIDEO_REQUEST_CODE);
            return;
        }

        throw new IllegalArgumentException("context must be activity");

    }

    public void onActivityForResult(int requestCode, int resultCode, Intent intent) {
        Log.i(TAG, "onActivityForResult: ");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSER_GALLERY_REQUEST_CODE) {
                if (uploadFile != null) {
                    uploadFile.onReceiveValue(intent.getData());
                    uploadFile = null;
                }
                if (uploadFiles != null) {
                    uploadFiles.onReceiveValue(new Uri[]{intent.getData()});
                    uploadFiles = null;
                }
            }
            if (requestCode == CHOOSER_CAMERA_REQUEST_CODE) {
                if (uploadFile != null) {
                    uploadFile.onReceiveValue(mUri);
                    uploadFile = null;
                }
                if (uploadFiles != null) {
                    uploadFiles.onReceiveValue(new Uri[]{mUri});
                    uploadFiles = null;
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (uploadFile != null) {
                uploadFile.onReceiveValue(null);
                uploadFile = null;
            }
            if (uploadFiles != null) {
                uploadFiles.onReceiveValue(null);
                uploadFiles = null;
            }
        }
    }

    private Uri getUriForFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数：authority 需要和清单文件中配置的保持完全一致：${applicationId}.xxx
            fileUri = FileProvider.getUriForFile(context, context.getPackageName(), file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }
}
