package com.picovr.androidcollection.Utils.io;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class FileUtil {
    public static final String TAG = FileUtil.class.getSimpleName();

    /**
     * 获取应用名称
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }

    /**
     * 向一个文件写入字符串
     * 会覆盖原有数据
     *
     * @param file
     * @param value
     */
    private static void write(File file, String value) {

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(value);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 读取一个文件，以字符串形式返回
     *
     * @param file
     * @return
     */
    public static String read(File file) {
        StringBuilder stringBuilder = new StringBuilder();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            char[] buff = new char[256];
            int len = 0;
            while ((len = fileReader.read(buff)) != -1) {
                stringBuilder.append(buff, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileReader);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取uri地址
     *
     * @param context
     * @param file
     * @return
     */
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //参数：authority 需要和清单文件中配置的保持完全一致：${applicationId}.xxx
            fileUri = FileProvider.getUriForFile(context, context.getPackageName(), file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    /**
     * 删除文件或文件夹
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.exists() && file.isFile()) {
            file.delete();
            return;
        }
        File[] files = file.listFiles();
        if (files.length == 0) {
            file.delete();
        } else {
            for (File childFile : files) {
                delete(childFile);
            }
            file.delete();
        }

    }

    /**
     * 文件复制
     *
     * @param source
     * @param target
     */
    public static void copy(File source, File target) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(target));
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(buffer, 0, len);
            }
            bufferedInputStream.close();
            bufferedOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件复制
     *
     * @param sourceFile
     * @param targetFile
     */
    public static void copyFile(String sourceFile, String targetFile) {
        File file = new File(sourceFile);
        if (!file.exists()) {
            Log.w(TAG, "copyFile: sourceFile: " + sourceFile + " is not exists");
            return;
        }

        copyFile(file, new File(targetFile));
    }

    /**
     * 复制文件
     *
     * @param in
     * @param out
     */
    public static void copyFile(File in, File out) {
        try {
            copyFile(new FileInputStream(in), new FileOutputStream(out));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件
     *
     * @param in
     * @param out
     * @return
     */
    public static void copyFile(InputStream in, OutputStream out) {
        byte[] buffer = new byte[4096];
        int read;
        try {
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
    }


    /**
     * Uri to file.
     * from blankj
     *
     * @param uri The uri.
     * @return file
     */
    public static File uri2File(@NonNull final Uri uri) {
        Log.d("UriUtils", uri.toString());
        String authority = uri.getAuthority();
        String scheme = uri.getScheme();
        String path = uri.getPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && path != null && path.startsWith("/external/")) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + path.replace("/external", ""));
            if (file.exists()) {
                Log.d("UriUtils", uri.toString() + " -> /external");
                return file;
            }
        }
        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            if (path != null) return new File(path);
            Log.d("UriUtils", uri.toString() + " parse failed. -> 0");
            return null;
        }// end 0
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(Utils.getApp(), uri)) {
            if ("com.android.externalstorage.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return new File(Environment.getExternalStorageDirectory() + "/" + split[1]);
                } else {
                    // Below logic is how External Storage provider build URI for documents
                    // http://stackoverflow.com/questions/28605278/android-5-sd-card-label
                    StorageManager mStorageManager = (StorageManager) Utils.getApp().getSystemService(Context.STORAGE_SERVICE);
                    try {
                        Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
                        Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
                        Method getUuid = storageVolumeClazz.getMethod("getUuid");
                        Method getState = storageVolumeClazz.getMethod("getState");
                        Method getPath = storageVolumeClazz.getMethod("getPath");
                        Method isPrimary = storageVolumeClazz.getMethod("isPrimary");
                        Method isEmulated = storageVolumeClazz.getMethod("isEmulated");

                        Object result = getVolumeList.invoke(mStorageManager);

                        final int length = Array.getLength(result);
                        for (int i = 0; i < length; i++) {
                            Object storageVolumeElement = Array.get(result, i);
                            //String uuid = (String) getUuid.invoke(storageVolumeElement);

                            final boolean mounted = Environment.MEDIA_MOUNTED.equals(getState.invoke(storageVolumeElement))
                                    || Environment.MEDIA_MOUNTED_READ_ONLY.equals(getState.invoke(storageVolumeElement));

                            //if the media is not mounted, we need not get the volume details
                            if (!mounted) continue;

                            //Primary storage is already handled.
                            if ((Boolean) isPrimary.invoke(storageVolumeElement)
                                    && (Boolean) isEmulated.invoke(storageVolumeElement)) {
                                continue;
                            }

                            String uuid = (String) getUuid.invoke(storageVolumeElement);

                            if (uuid != null && uuid.equals(type)) {
                                return new File(getPath.invoke(storageVolumeElement) + "/" + split[1]);
                            }
                        }
                    } catch (Exception ex) {
                        Log.d("UriUtils", uri.toString() + " parse failed. " + ex.toString() + " -> 1_0");
                    }
                }
                Log.d("UriUtils", uri.toString() + " parse failed. -> 1_0");
                return null;
            }// end 1_0
            else if ("com.android.providers.downloads.documents".equals(authority)) {
                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                Long.valueOf(id)
                        );
                        return getFileFromUri(contentUri, "1_1");
                    } catch (NumberFormatException e) {
                        if (id.startsWith("raw:")) {
                            return new File(id.substring(4));
                        }
                    }
                }
                Log.d("UriUtils", uri.toString() + " parse failed. -> 1_1");
                return null;
            }// end 1_1
            else if ("com.android.providers.media.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                } else {
                    Log.d("UriUtils", uri.toString() + " parse failed. -> 1_2");
                    return null;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getFileFromUri(contentUri, selection, selectionArgs, "1_2");
            }// end 1_2
            else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                return getFileFromUri(uri, "1_3");
            }// end 1_3
            else {
                Log.d("UriUtils", uri.toString() + " parse failed. -> 1_4");
                return null;
            }// end 1_4
        }// end 1
        else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            return getFileFromUri(uri, "2");
        }// end 2
        else {
            Log.d("UriUtils", uri.toString() + " parse failed. -> 3");
            return null;
        }// end 3
    }

    private static File getFileFromUri(final Uri uri, final String code) {
        return getFileFromUri(uri, null, null, code);
    }

    private static File getFileFromUri(final Uri uri,
                                       final String selection,
                                       final String[] selectionArgs,
                                       final String code) {
        final Cursor cursor = Utils.getApp().getContentResolver().query(
                uri, new String[]{"_data"}, selection, selectionArgs, null);
        if (cursor == null) {
            Log.d("UriUtils", uri.toString() + " parse failed(cursor is null). -> " + code);
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndex("_data");
                if (columnIndex > -1) {
                    return new File(cursor.getString(columnIndex));
                } else {
                    Log.d("UriUtils", uri.toString() + " parse failed(columnIndex: " + columnIndex + " is wrong). -> " + code);
                    return null;
                }
            } else {
                Log.d("UriUtils", uri.toString() + " parse failed(moveToFirst return false). -> " + code);
                return null;
            }
        } catch (Exception e) {
            Log.d("UriUtils", uri.toString() + " parse failed. -> " + code);
            return null;
        } finally {
            cursor.close();
        }
    }
}
