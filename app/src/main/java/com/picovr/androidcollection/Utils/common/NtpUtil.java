package com.picovr.androidcollection.Utils.common;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author patrick.ding
 * @since 19/11/21
 */
public class NtpUtil {
    public static final String TAG = "NtpUtil";

    public interface ISntpTimeCallback {
        void getNtpTimeState(boolean state);
    }


    private static long ntpTime;
    private static long systemRunTime;
    private static String[] NTP_POOL = new String[]{"cn.ntp.org.cn", "us.ntp.org.cn", "de.ntp.org.cn"};

    /**
     * 先从ntp服务器获取时间（获取到的设备所在时区的标准时间）
     */
    public static void getNTPTimeAsync(final ISntpTimeCallback iSntpTimeCallback) {
        if (ntpTime != 0) {
            if (iSntpTimeCallback != null) {
                iSntpTimeCallback.getNtpTimeState(ntpTime != 0);
            }
            return;
        }

        new NtpAsyncTask(iSntpTimeCallback).execute("");
    }

    public static long getTime() {
        long limit = 1000 * 60 * 6;
        long localDeviceTime = System.currentTimeMillis();
        if (ntpTime != 0 && (Math.abs(ntpTime - localDeviceTime) > limit)) {
            localDeviceTime = ntpTime + SystemClock.elapsedRealtime() - systemRunTime;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS z");
        Date date = new Date(localDeviceTime);
        Log.i(TAG, "Real Time " + dateFormat.format(date));
        return localDeviceTime;
    }


    public static boolean getNTPTime() {
        SntpClient client = new SntpClient();
        for (int i = 0; i < NTP_POOL.length; i++) {
            if (client.requestTime(NTP_POOL[i], 500)) {
                long now = client.getNtpTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS z");
                Date date = new Date(now);
                Log.i(TAG, "NTP: " + dateFormat.format(date));
                long rawOffset = dateFormat.getTimeZone().getRawOffset();
                ntpTime = now;
                systemRunTime = SystemClock.elapsedRealtime();
                Log.i(TAG, "UTC: " + dateFormat.format(new Date(ntpTime - rawOffset)));
                break;
            } else {
                Log.i(TAG, "NTP: false");
            }
        }

        ntpTime = ntpTime <= 0 ? System.currentTimeMillis() : ntpTime;

        return ntpTime != 0;
    }

    private static class NtpAsyncTask extends AsyncTask<String, Void, Long> {

        private final ISntpTimeCallback iSntpTimeCallback;

        public NtpAsyncTask(ISntpTimeCallback iSntpTimeCallback) {
            this.iSntpTimeCallback = iSntpTimeCallback;
        }

        @Override
        protected Long doInBackground(String... strings) {
            SntpClient client = new SntpClient();
            for (int i = 0; i < NTP_POOL.length; i++) {
                if (client.requestTime(NTP_POOL[i], 500)) {
                    long now = client.getNtpTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS z");
                    Date date = new Date(now);
                    Log.i(TAG, "NTP: " + dateFormat.format(date));
                    long rawOffset = dateFormat.getTimeZone().getRawOffset();
                    ntpTime = now;
                    systemRunTime = SystemClock.elapsedRealtime();
                    Log.i(TAG, "UTC: " + dateFormat.format(new Date(ntpTime - rawOffset)));
                    break;
                } else {
                    Log.i(TAG, "NTP: false");
                }
            }

            ntpTime = ntpTime <= 0 ? System.currentTimeMillis() : ntpTime;
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            if (iSntpTimeCallback != null) {
                iSntpTimeCallback.getNtpTimeState(ntpTime != 0);
            }
        }
    }
}
