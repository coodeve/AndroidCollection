package com.picovr.androidcollection.Utils.av;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.picovr.androidcollection.Utils.image.ImageUtils;
import com.picovr.androidcollection.Utils.log.Logs;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class CameraUtil {
    public static final String TAG = CameraUtil.class.getSimpleName();
    private Activity mActivity;
    /**
     * 相册请求码
     */
    private static final int ALBUM_REQUEST_CODE = 1;
    /**
     * 相机请求码
     */
    private static final int CAMERA_REQUEST_CODE = 2;
    /**
     * 剪裁请求码
     */
    private static final int CROP_REQUEST_CODE = 3;

    private File tempFile;

    private Uri uriCutImg;

    /**
     * 从相机获取图片
     */
    private void getPicFromCamera(Activity activity) {
        //用于保存调用相机拍照后所生成的文件
        String imgName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
                .format(new Date());
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), imgName + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本，如果在Android7.0以上,使用FileProvider获取Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, "com.picovr.androidcollection.fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            Logs.i(TAG, contentUri.toString());
        } else {
            //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm(Activity activity) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //intent.putExtra("outputX", 300);
        //intent.putExtra("outputY", 300);
        //intent.putExtra("return-data", true);
        uriCutImg = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        //将裁剪好图片，存进该URI中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCutImg);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /**
     * 需要在activity中调用此方法
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Logs.i("onActivityResult requestCode = " + requestCode);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mActivity, "com.picovr.androidcollection.fileProvider", tempFile);
                        cropPhoto(mActivity, contentUri);
                    } else {
                        cropPhoto(mActivity, Uri.fromFile(tempFile));
                    }
                }
                break;
            case ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK && intent != null) {
                    Uri uri = intent.getData();
                    cropPhoto(mActivity, uri);
                }
                break;
            case CROP_REQUEST_CODE:
                if (intent != null) {
                    Bitmap image = null;
                    //通过URI获得的图片
                    try {
                        image = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(uriCutImg));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Logs.i(TAG, "image为空？" + (image == null));

                    handleBitmap(image);

                    File file = ImageUtils.compressImage(image);
                    handleImageFile(file);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理图片文件
     *
     * @param file
     */
    private void handleImageFile(File file) {

    }

    /**
     * 处理bitmap
     *
     * @param image
     */
    private void handleBitmap(Bitmap image) {

    }
}
