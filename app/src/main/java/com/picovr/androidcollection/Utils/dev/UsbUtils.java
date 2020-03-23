package com.picovr.androidcollection.Utils.dev;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

public class UsbUtils {

    private static final String TAG = UsbUtils.class.getSimpleName();

    /**
     * 权限申请
     */
    private static final String USB_PERMISSION = "";

    private UsbManager usbManager;
    /**
     * 开启后的usbinterface
     */
    private UsbInterface mInterface;
    /**
     * 读取接口
     */
    private UsbEndpoint mUsbEndpoIn;
    /**
     * 写入接口
     */
    private UsbEndpoint mUsbEndpointOut;
    /**
     * 已连接设备
     */
    private UsbDeviceConnection mUsbConnection;

    /**
     * usb接口参数
     */
    public static class USBConfig {
        private int VID;
        private int PID;
    }

    /**
     * 广播监听
     */
    private static class UsbBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive action: " + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.e(TAG, "Mounted usb");

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                Log.e(TAG, " UnMounted usb");
            }
        }
    }


    private static class UsbPermissionBroadcast extends BroadcastReceiver {
        private USBPermissionCallBack mUSBPermissionCallBack;

        public UsbPermissionBroadcast(USBPermissionCallBack USBPermissionCallBack) {
            mUSBPermissionCallBack = USBPermissionCallBack;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (null != device) {
                            Log.d(TAG, "onReceive: Permission GRANTED");
                            if (mUSBPermissionCallBack != null) {
                                mUSBPermissionCallBack.success();
                            }
                        }
                    } else {
                        Log.d(TAG, "onReceive: Permission denied for device");
                        if (mUSBPermissionCallBack != null) {
                            mUSBPermissionCallBack.cancel();
                        }
                    }
                }
            }
        }
    }

    /**
     * usb权限检查
     */
    public interface USBPermissionCallBack {
        void success();

        void cancel();
    }


    private UsbBroadcast mUsbBroadcast = new UsbBroadcast();

    private UsbManager getUsbManager(Context context) {
        if (usbManager == null) {
            usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        }
        return usbManager;
    }

    public HashMap<String, UsbDevice> getUsbList(Context context) {
        return getUsbManager(context).getDeviceList();
    }

    public UsbDevice getTargetUsbDevice(Context context, USBConfig usbConfig) {
        HashMap<String, UsbDevice> deviceList = getUsbList(context);
        Iterator<UsbDevice> iterator = deviceList.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice device = iterator.next();
            if (device.getVendorId() == usbConfig.VID
                    && device.getProductId() == usbConfig.PID) {
                return device;
            }
        }

        return null;
    }

    public boolean isTargetUsbDevice(Context context, USBConfig usbConfig) {
        HashMap<String, UsbDevice> deviceList = getUsbList(context);
        Iterator<UsbDevice> iterator = deviceList.values().iterator();
        while (iterator.hasNext()) {
            UsbDevice device = iterator.next();
            if (device.getVendorId() == usbConfig.VID
                    && device.getProductId() == usbConfig.PID) {
                return true;
            }
        }

        return false;
    }

    /**
     * 检查已连接设备是否有访问权限
     * 有广播异常需要处理
     *
     * @param context
     * @param device
     * @param callBack
     */
    public UsbPermissionBroadcast checkUsbPermission(Context context, UsbDevice device, USBPermissionCallBack callBack) {
        UsbPermissionBroadcast usbPermissionBroadcast = null;
        if (getUsbManager(context).hasPermission(device)) {
            Log.d(TAG, "getUsbPermission: 已获取USB 权限");
            if (callBack != null) {
                callBack.success();
            }
        } else {
            IntentFilter filter = new IntentFilter(USB_PERMISSION);
            usbPermissionBroadcast = new UsbPermissionBroadcast(callBack);
            context.registerReceiver(usbPermissionBroadcast, filter);
            PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(USB_PERMISSION), 0);
            getUsbManager(context).requestPermission(device, intent);
        }

        return usbPermissionBroadcast;

    }

    /**
     * 打开设备
     *
     * @param device
     * @return
     */
    public boolean openUSBDevice(UsbDevice device) {

        if (null == device) {
            Log.d(TAG, "openUSBDevice: null = device");
            return false;
        }

        mInterface = device.getInterface(0);
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            mInterface = device.getInterface(i);
            for (int j = 0; j < mInterface.getEndpointCount(); j++) {
                UsbEndpoint end = mInterface.getEndpoint(j);
                if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                    mUsbEndpoIn = end;
                } else if (end.getDirection() == UsbConstants.USB_DIR_OUT) {
                    mUsbEndpointOut = end;
                }
            }
        }

        mUsbConnection = usbManager.openDevice(device);
        if (null == mUsbConnection) {
            Log.d(TAG, "openUSBDevice: null = UsbConnection");
            return false;
        }

        if (!mUsbConnection.claimInterface(mInterface, true)) {
            mUsbConnection.close();
            Log.d(TAG, "openUSBDevice: not find device");
            return false;
        }
        Log.i(TAG, "openUSBDevice# has open , device:" + mInterface.toString());

        return true;
    }

    /**
     * 关闭链接，释放资源
     */
    public void closeUsbDevice() {
        if (mUsbConnection != null) {
            try {
                mUsbConnection.releaseInterface(mInterface);
                mUsbConnection = null;
                mUsbEndpoIn = null;
                mUsbEndpointOut = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 写入数据
     *
     * @param reportID
     * @param data     吸入数据
     * @param length   写入数据长度
     * @return
     */
    private int setReportControl(int reportID, byte[] data, int length) {
        if (null == mUsbConnection) return -2;
        int requestType = 0x21;
        int request = 0x09;
        int value = 0x0300 | reportID;
        int index = 0x00;
        int timeout = 1000;
        return mUsbConnection.controlTransfer(requestType, request, value, index, data, length, timeout);
    }


    /**
     * 读取数据，调用方式，一般是先写入命令{@link #setReportControl(int, byte[], int)},然后再执行读取命令
     *
     * @param reportID
     * @param dataBuffer 读取到的数据
     * @param length     要读取的长度，一般为dataBuffer长度
     * @return
     */
    private int getReportControl(int reportID, byte[] dataBuffer, int length) {
        if (null == mUsbConnection) return -2;
        int requestType = 0xA1;
        int request = 0x01;
        int value = 0x0300 | reportID;
        int index = 0x00;
        int timeout = 1000;
        return mUsbConnection.controlTransfer(requestType, request, value, index, dataBuffer, length, timeout);
    }

    /**
     * 使用bulkTransfer写入
     *
     * @param writeByte
     * @return
     */
    private int setReportControl(byte[] writeByte) {
        return mUsbConnection.bulkTransfer(mUsbEndpointOut, writeByte, writeByte.length, 20);
    }

    /**
     * 使用bulkTransfer读取
     *
     * @param writeByte
     * @return
     */
    private int getReportControl(byte[] readBytes) {
        return mUsbConnection.bulkTransfer(mUsbEndpoIn, readBytes, readBytes.length, 20);
    }


    public void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(mUsbBroadcast, intentFilter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(mUsbBroadcast);
    }


}
