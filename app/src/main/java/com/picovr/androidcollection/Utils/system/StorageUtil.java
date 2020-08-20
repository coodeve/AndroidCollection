package com.picovr.androidcollection.Utils.system;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.storage.StorageEventListener;
//import android.os.storage.StorageManager;
//import android.os.storage.VolumeInfo;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//
public class StorageUtil {
//
//    private StorageEventListener mListener = new StorageEventListener() {
//        public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
//            if (vol == null || vol.getDisk() == null) {
//                return;
//            }
//
//            switch (newState) {
//                case VolumeInfo.STATE_UNMOUNTABLE:
//
//                    break;
//                case VolumeInfo.STATE_MOUNTED:
//
//                    break;
//                case VolumeInfo.STATE_UNMOUNTED:
//
//                    break;
//                default:
//                    break;
//            }
//
//        }
//    };
//
//    /**
//     * 注册监听
//     *
//     * @param context
//     * @param storageEventListener
//     */
//    private void registerListeners(Context context, StorageEventListener storageEventListener) {
//        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
//        try {
//            Method mtd = sm.getClass().getDeclaredMethod("registerListener", StorageEventListener.class);
//            mtd.setAccessible(true);
//            mtd.invoke(sm, storageEventListener);
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
}
