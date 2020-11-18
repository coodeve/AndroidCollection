package com.coodev.androidcollection.Utils.system;

import android.app.PendingIntent;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author patrick.ding
 * @since 20/1/2
 * 静默安装工具
 */
public class InstallUtil {

    public interface InstallCallback {
        void process(String packageName, int process);

        void install(String packageName, boolean success, String msg);
    }

    public interface IInstall {
        void install(Context context, String packageName, String filePath, InstallCallback callback);
    }

    /**
     * Android9以下，使用,同步方法
     * <uses-permission android:name="android.permission.DELETE_PACKAGES" />
     * <uses-permission android:name="android.permission.INSTALL_PACKAGES" />
     * <p>
     * <uses-permission android:name="android.permission.REPLACE_EXISTING_PACKAGE" />
     * <p>
     * 需要系统权限
     */
    private static class ShellInstallSync implements IInstall {

        @Override
        public void install(Context context, String packageName, String filePath, InstallCallback callback) {
            String[] cmds;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                cmds = new String[]{"pm", "install", "-r", "-d", "--dont-kill", filePath};
            } else {
                // 此处packageName为安装者的包名
                String[] split = ("pm install -r -d --dont-kill -i " + packageName + " --user 0 ").split(" ");
                int l = split.length;
                cmds = new String[split.length + 1];
                System.arraycopy(split, 0, cmds, 0, split.length);
                cmds[l] = filePath;
            }

            List<String> strings = Arrays.asList(cmds);
            ProcessBuilder processBuilder = new ProcessBuilder(strings);
            try {
                Process process = processBuilder.start();
                InputStream errorStream = process.getErrorStream();
                InputStream inputStream = process.getInputStream();
                String errorString = getStringFromStream(errorStream);
                String normalString = getStringFromStream(inputStream);
                process.destroy();
                if (errorString == null && normalString != null) {
                    if (callback != null) {
                        callback.install(packageName, true, normalString);
                    }
                    return;
                }

                if (callback != null) {
                    callback.install(packageName, false, errorString);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * android9及以上，使用,这是一个异步方法
     * <!-- 应用卸载权限 -->
     * <uses-permission android:name="android.permission.DELETE_PACKAGES" />
     * <!-- 应用安装权限 -->
     * <uses-permission android:name="android.permission.INSTALL_PACKAGES" />
     * <p>
     * <uses-permission android:name="android.permission.REPLACE_EXISTING_PACKAGE" />
     * <p>
     * 需要系统权限
     * <p>
     * 主要通过系统类{@link android.content.pm.PackageInstaller}来进行静默安装
     */
    private static class PackageInstallAsync implements IInstall {

        private static PackageInstaller sPackageInstaller;

        private static Map<String, List<InstallCallback>> sInstallCallbacks = new HashMap<>();


        /**
         * 安装时是否kill掉app
         */
        private boolean dontKillApp;

        @Override
        public void install(Context context, String packageName, String filePath, InstallCallback callback) {
            if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(packageName)) {
                throw new IllegalArgumentException("file params is null");
            }
            PackageInfo packageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.MATCH_UNINSTALLED_PACKAGES);

            File file = new File(filePath);
            String apkName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
            addCallback(packageArchiveInfo.packageName, callback);
            // 设置参数
            PackageInstaller packageInstaller = getPackageInstaller(context);
            PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            sessionParams.setAppPackageName(apkName);
            setDontKillApp(sessionParams, dontKillApp);
            // 安装

            PackageInstaller.Session session = null;
            OutputStream outputStream = null;
            FileInputStream inputStream = null;
            try {
                //创建Session
                int sessionId = packageInstaller.createSession(sessionParams);
                //开启Session
                session = packageInstaller.openSession(sessionId);
                //获取输出流，用于将apk写入session
                outputStream = session.openWrite(apkName, 0, -1);
                inputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int n;
                //读取apk文件写入session
                while ((n = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, n);
                }
                //写完需要关闭流，否则会抛异常“files still open”
                inputStream.close();
                inputStream = null;
                outputStream.flush();
                outputStream.close();
                outputStream = null;
                //配置安装完成后发起的intent，通常是打开activity
                Intent intent = new Intent();
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                IntentSender intentSender = pendingIntent.getIntentSender();
                //提交启动安装
                session.commit(intentSender);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
                if (session != null) {
                    session.abandon();
                }
            } finally {
                close(inputStream);
                close(outputStream);
            }
        }

        private void addCallback(String packageName, InstallCallback callback) {

            List<InstallCallback> callbackList = null;
            if ((callbackList = sInstallCallbacks.get(packageName)) == null) {
                callbackList = new ArrayList<>();
                callbackList.add(callback);
            }

            sInstallCallbacks.put(packageName, callbackList);
        }

        private static PackageInstaller getPackageInstaller(Context context) {
            if (sPackageInstaller == null) {
                sPackageInstaller = context.getPackageManager().getPackageInstaller();
                sPackageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
                    @Override
                    public void onCreated(int sessionId) {

                    }

                    @Override
                    public void onBadgingChanged(int sessionId) {

                    }

                    @Override
                    public void onActiveChanged(int sessionId, boolean active) {

                    }

                    @Override
                    public void onProgressChanged(int sessionId, float progress) {
                        PackageInstaller.SessionInfo sessionInfo = sPackageInstaller.getSessionInfo(sessionId);
                        String appPackageName = sessionInfo.getAppPackageName();
                        List<InstallCallback> callbackList = sInstallCallbacks.get(appPackageName);
                        for (InstallCallback callback : callbackList) {
                            callback.process(appPackageName, (int) (progress * 100));
                        }
                    }

                    @Override
                    public void onFinished(int sessionId, boolean success) {
                        PackageInstaller.SessionInfo sessionInfo = sPackageInstaller.getSessionInfo(sessionId);
                        String appPackageName = sessionInfo.getAppPackageName();
                        List<InstallCallback> callbackList = sInstallCallbacks.get(appPackageName);
                        for (InstallCallback callback : callbackList) {
                            callback.install(appPackageName, success, null);
                        }
                    }
                });
            }

            return sPackageInstaller;
        }


        private static void setDontKillApp(PackageInstaller.SessionParams sessionParams, boolean dontKillApp) {
            if (!dontKillApp) {
                return;
            }
            try {
                Method dontkillApp = sessionParams.getClass().getMethod("setDontKillApp", boolean.class);
                dontkillApp.invoke(sessionParams, dontKillApp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * android9及以上，使用,这是一个同步的方法
     * <!-- 应用卸载权限 -->
     * <uses-permission android:name="android.permission.DELETE_PACKAGES" />
     * <!-- 应用安装权限 -->
     * <uses-permission android:name="android.permission.INSTALL_PACKAGES" />
     * <p>
     * <uses-permission android:name="android.permission.REPLACE_EXISTING_PACKAGE" />
     * <p>
     * 需要系统权限
     * <p>
     * 主要通过系统类{@link android.content.pm.PackageInstaller}来进行静默安装
     */
    private static class PackageInstallSync implements IInstall {

        private boolean dontKillApp;

        @Override
        public void install(Context context, String packageName, String apkPath, InstallCallback callback) {
            if (TextUtils.isEmpty(apkPath) || TextUtils.isEmpty(packageName)) {
                throw new IllegalArgumentException("file params is null");
            }
            // 包参数
            PackageInfo packageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.MATCH_UNINSTALLED_PACKAGES);
            if (packageArchiveInfo == null) {
                String msg = "installQ: Failed to parse APK file : " + apkPath;
                if (callback != null) {
                    callback.install(packageName, false, msg);
                }
                return;
            }
            String targetPackageName = packageArchiveInfo.packageName;
            File file = new File(apkPath);
            if (!file.isFile()) {
                final String msg = "installQ: File is not a file : " + apkPath;
                if (callback != null) {
                    callback.install(packageName, false, msg);
                }
                return;
            }
            // 安装设置参数
            final PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
            final PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            sessionParams.setAppPackageName(targetPackageName);
            setDontKillApp(sessionParams, dontKillApp);
            // 开始安装
            PackageInstaller.Session session = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                //创建Session
                int sessionId = packageInstaller.createSession(sessionParams);
                //开启Session
                session = packageInstaller.openSession(sessionId);
                //获取输出流，用于将apk写入session
                outputStream = session.openWrite(targetPackageName, 0, -1);
                inputStream = new FileInputStream(file);
                byte[] buffer = new byte[8192];
                int n;
                //读取apk文件写入session
                while ((n = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, n);
                }

                session.fsync(outputStream);
                //写完需要关闭流，否则会抛异常“files still open”
                inputStream.close();
                inputStream = null;
                outputStream.close();
                outputStream = null;

                LocalIntentReceiver receiver = new LocalIntentReceiver();
                session.commit(receiver.getIntentSender());

                final Intent result = receiver.getResult();
                final int status = result.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
                String statusMessage = result.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
                if (status == PackageInstaller.STATUS_SUCCESS) {
                    if (callback != null) {
                        callback.install(packageName, true, null);
                    }
                } else {
                    if (callback != null) {
                        callback.install(packageName, false, statusMessage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.install(packageName, false, e.getMessage());
                }
            } finally {
                if (session != null) {
                    try {
                        session.abandon();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                close(inputStream);
                close(outputStream);
            }

        }

        private static void setDontKillApp(PackageInstaller.SessionParams sessionParams, boolean dontKillApp) {
            if (!dontKillApp) {
                return;
            }
            try {
                Method dontkillApp = sessionParams.getClass().getMethod("setDontKillApp", boolean.class);
                dontkillApp.invoke(sessionParams, dontKillApp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static class LocalIntentReceiver {
        private final SynchronousQueue<Intent> mResult = new SynchronousQueue<>();

        private IIntentSender.Stub mLocalSender = new IIntentSender.Stub() {
            @Override
            public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken,
                             IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
                try {
                    mResult.offer(intent, 5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        public IntentSender getIntentSender() {
            final Constructor<IntentSender> constructor;
            try {
                constructor = IntentSender.class.getConstructor(IBinder.class);
                constructor.setAccessible(true);
                final IntentSender intentSender = constructor.newInstance((IIntentSender) mLocalSender);
                return intentSender;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public Intent getResult() {
            try {
                return mResult.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 用于 {@link ShellInstallSync}
     *
     * @param inputStream
     * @return
     */
    private static String getStringFromStream(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[16];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            byteArrayOutputStream.flush();

            return byteArrayOutputStream.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(byteArrayOutputStream);
            close(inputStream);
        }

        return null;
    }

    private static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
