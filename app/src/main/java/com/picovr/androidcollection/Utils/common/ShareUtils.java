package com.picovr.androidcollection.Utils.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.picovr.androidcollection.R;
import com.picovr.androidcollection.Utils.log.Logs;
import com.picovr.androidcollection.Utils.ui.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import razerdp.basepopup.BasePopupWindow;


public class ShareUtils {
    public static final String PACKAGE_WECHAT = "com.tencent.mm";
    public static final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_SINA = "com.sina.weibo";

    /**
     * 判断是否安装指定app
     *
     * @param context
     * @param app_package
     * @return
     */
    public static boolean isInstallApp(Context context, String app_package) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                if (app_package.equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 解决分享朋友圈报错未找到资源
     *
     * @param context
     * @param imageFile
     * @return
     */
    private static Uri getImageContentUri(Context context, File imageFile) {
        Uri uri;
        try {
            uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), imageFile.getAbsolutePath(), imageFile.getName(), null));
        } catch (FileNotFoundException e) {
            uri = Uri.fromFile(imageFile);
        }
        return uri;
    }

    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    private static String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (!TextUtils.isEmpty(type)) {
            return type;
        }
        return "file/*";
    }

    private static void shareFile(Context context, File file) {
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.picovr.assistantphone.FileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file));
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(Intent.createChooser(share, "分享"));
            } catch (Exception e) {
                Logs.e(e.getMessage());
            }
        } else {
            ToastUtil.showShort(context,"分享文件不存在");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void shareWxFriend(Context context, File file) {
        if (!isInstallApp(context, PACKAGE_WECHAT)) {
            ToastUtil.showShort(context,"分享失败，未安装微信");
            return;
        }
        if (null != file && file.exists()) {
            if (getMimeType(file).contains("image")) {
                Intent share = new Intent(Intent.ACTION_SEND);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, "com.picovr.assistantphone.FileProvider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType(getMimeType(file));
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ComponentName comp = new ComponentName(PACKAGE_WECHAT, "com.tencent.mm.ui.tools.ShareImgUI");
                share.setComponent(comp);
                try {
                    context.startActivity(share);
                } catch (Exception e) {
                    Logs.e(e.getMessage());
                    ToastUtil.showShort(context,context.getResources().getText(R.string.shared_failed));
                }
            } else {
                context.startActivity(
                        context.getPackageManager()
                                .getLaunchIntentForPackage(ShareUtils.PACKAGE_WECHAT));
            }
        } else {
            ToastUtil.showShort(context,"分享文件不存在");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void shareWxFriend(Context context, File file, BasePopupWindow.OnDismissListener onDismissListener) {
        if (!isInstallApp(context, PACKAGE_WECHAT)) {
            ToastUtil.showShort(context,"分享失败，未安装微信");
            return;
        }
        if (null != file && file.exists()) {
            if (getMimeType(file).contains("image")) {
                Intent share = new Intent(Intent.ACTION_SEND);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(context, "com.picovr.assistantphone.FileProvider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType(getMimeType(file));
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ComponentName comp = new ComponentName(PACKAGE_WECHAT, "com.tencent.mm.ui.tools.ShareImgUI");
                share.setComponent(comp);
                try {
                    context.startActivity(share);
                } catch (Exception e) {
                    Logs.e(e.getMessage());
                    ToastUtil.showShort(context,context.getResources().getText(R.string.shared_failed));
                }
            } else {
                context.startActivity(
                        context.getPackageManager()
                                .getLaunchIntentForPackage(ShareUtils.PACKAGE_WECHAT));
            }
        } else {
            ToastUtil.showShort(context,"分享文件不存在");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void shareWxCircle(Context context, File file) {
        if (!isInstallApp(context, PACKAGE_WECHAT)) {
            ToastUtil.showShort(context,"分享失败，未安装微信");
            return;
        }
        if (null != file && file.exists()) {
            if (getMimeType(file).contains("image")) {
                Intent share = new Intent(Intent.ACTION_SEND);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = getImageContentUri(context, file);
                } else {
                    uri = Uri.fromFile(file);
                }
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType(getMimeType(file));
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ComponentName comp = new ComponentName(PACKAGE_WECHAT, "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                share.setComponent(comp);
                try {
                    context.startActivity(share);
                } catch (Exception e) {
                    Logs.e(e.getMessage());
                    ToastUtil.showShort(context,context.getResources().getText(R.string.shared_failed));
                }
            } else {
                context.startActivity(
                        context.getPackageManager()
                                .getLaunchIntentForPackage(ShareUtils.PACKAGE_WECHAT));
            }
        } else {
            ToastUtil.showShort(context,"分享文件不存在");
        }
    }

    public static void shareQQ(Context context, File file) {
        if (!isInstallApp(context, PACKAGE_MOBILE_QQ)) {
            ToastUtil.showShort(context,"分享失败，未安装QQ");
            return;
        }
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.picovr.assistantphone.FileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file));
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ComponentName comp = new ComponentName(PACKAGE_MOBILE_QQ, "com.tencent.mobileqq.activity.JumpActivity");
            share.setComponent(comp);
            try {
                context.startActivity(share);
            } catch (Exception e) {
                Logs.e(e.getMessage());
                ToastUtil.showShort(context,context.getResources().getText(R.string.shared_failed));
            }
        } else {
            ToastUtil.showShort(context,"分享文件不存在");
        }
    }

    public static void shareSina(Context context, File file) {
        if (!isInstallApp(context, PACKAGE_SINA)) {
            ToastUtil.showShort(context,"分享失败，未安装微博");
            return;
        }
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, "com.picovr.assistantphone.FileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.setType(getMimeType(file));
            //share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ComponentName comp = new ComponentName(PACKAGE_SINA, "com.sina.weibo.composerinde.ComposerDispatchActivity");
            share.setComponent(comp);
            try {
                context.startActivity(share);
            } catch (Exception e) {
                Logs.e(e.getMessage());
                ToastUtil.showShort(context,context.getResources().getText(R.string.shared_failed));
            }
        } else {
            ToastUtil.showShort(context,"分享文件不存在");
        }
    }

    public static void shareDy(Context context, String filePath) {
//        TiktokOpenApi tiktokOpenApi = TikTokOpenApiFactory.create(context
//                , TikTokConstants.TARGET_APP.AWEME);
//        ArrayList<String> mUri = new ArrayList<>();
//        mUri.add(filePath);
//        Share.Request request = new Share.Request();
//        // 分享视频
//        TikTokVideoObject videoObject = new TikTokVideoObject();
//        videoObject.mVideoPaths = mUri;
//        TikTokMediaContent content = new TikTokMediaContent();
//        content.mMediaObject = videoObject;
//        request.mMediaContent = content;
//        // 调起分享
//        tiktokOpenApi.share(request);
    }

}
