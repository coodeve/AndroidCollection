package com.picovr.androidcollection.Utils.common;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author patrick.ding
 * @since 18/7/29
 */

public class TimeUtils {
    public static final String TAG = TimeUtils.class.getSimpleName();

    private static final String TODAY = "今天";
    private static final String TOMORROW = "明天";
    private static final String COMING = "即将到来";
    private static final String GOING = "进行中";
    private static final String END = "已结束";

    public static String formatData(Context context, long times) {
        // php服务器传来的字符串时间是10位
        String localFormat = "yyyy年MM月";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localFormat, Locale.getDefault());
        long time = (long) times * 1000;
        Timestamp timestamp = new Timestamp(time);
        String sd = simpleDateFormat.format(timestamp);
        return sd;
    }

    public static String formatTime(Context context, long times) {
        String localFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localFormat, Locale.getDefault());
        String sd = simpleDateFormat.format(new Date(times));
        return sd;
    }

    /**
     * 1.如果是今天 则返回 <上午/下午 08:00>
     * 2.如果是明天 则返回 <明天上午/明天下午 08：00>
     * 3.如果是后天或者几天后，返回 <日期 + 时间>
     *
     * @return 格式好的时间
     */
    public static String formatData(Context context, String dataStr, String format) {
        String time = "";
        String localFormat = "yyyy-MM-dd HH:mm:ss";
        if (!TextUtils.isEmpty(format)) {
            localFormat = format;
        }

        // 获取手机当前时间，是那一年的那一天
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);

        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localFormat);
            Date date = simpleDateFormat.parse(dataStr);
            long timeStamp = date.getTime();
            // 格式化上午还是下午
            String apTime = new SimpleDateFormat("hh:mm").format(new Date(timeStamp));
            String mothDay = new SimpleDateFormat("M月dd日").format(new Date(timeStamp));
            Calendar activityCalendar = Calendar.getInstance();
            activityCalendar.setTime(date);
            String am_pm = null;
            int hour = activityCalendar.get(Calendar.HOUR_OF_DAY);
            if (hour >= 0 && hour < 6) {
                am_pm = "凌晨";
                if (hour == 0) {
                    // 对24：00做特殊处理
                    apTime = apTime.replace("12:", "00:");
                }
            } else if (hour >= 6 && hour < 12) {
                am_pm = "早上";
            } else if (hour == 12) {
                am_pm = "中午";
            } else if (hour > 12 && hour < 18) {
                am_pm = "下午";
            } else if (hour >= 18) {
                am_pm = "晚上";
            }
            apTime = am_pm + " " + apTime;
            int activityDayOfYear = activityCalendar.get(Calendar.DAY_OF_YEAR);
            int activityYear = activityCalendar.get(Calendar.YEAR);
            String suffix;
            if (activityDayOfYear == dayOfYear) {
                // 今天
                suffix = TODAY;
            } else if (activityDayOfYear - dayOfYear == 1) {
                // 明天拿
                suffix = TOMORROW;
            } else {
                suffix = mothDay;
            }
            // TODO 取消日期显示
            suffix = mothDay;
            time = suffix + " " + apTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getDay(Context context, String dataStr, String format) {
        String time = "";
        String localFormat = "yyyy-MM-dd HH:mm:ss";
        if (!TextUtils.isEmpty(format)) {
            localFormat = format;
        }

        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localFormat);
            Date date = simpleDateFormat.parse(dataStr);
            long timeStamp = date.getTime();
            // 格式化上午还是下午
            String mothDay = new SimpleDateFormat("M月dd日").format(new Date(timeStamp));
            return mothDay;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static boolean timeIsExpires(Context context, String dataStr, String format) {
        String localFormat = "yyyy-MM-dd HH:mm:ss";
        if (!TextUtils.isEmpty(format)) {
            localFormat = format;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localFormat);
            Date date = simpleDateFormat.parse(dataStr);
            long timeStamp = date.getTime();
            long currentTimeStamp = System.currentTimeMillis();
            return timeStamp < currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long dataFormatToMillis(Context context, String dataStr, String format) {
        String localFormat = "yyyy-MM-dd HH:mm:ss";
        if (!TextUtils.isEmpty(format)) {
            localFormat = format;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localFormat);
            Date date = simpleDateFormat.parse(dataStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * return 即将开始，进行中，以结束
     */
    public static String getActivityStatus(Context context, String dataBegin, String dataEnd, String format, String end) {
        String separator = " • ";
        String status = null;
        long activityStartTime = TimeUtils.dataFormatToMillis(context, dataBegin, null);
        long activityEndTime = TimeUtils.dataFormatToMillis(context, dataEnd, null);
        long currentTime = System.currentTimeMillis();
        long startHalfHort = activityStartTime - 1000 * 60 * 30;
        if (currentTime < startHalfHort) {// 活动半小时之前
            status = "";
        } else if (currentTime > startHalfHort && currentTime < activityStartTime) {// 活动半小时~活动开始
            status = separator + COMING;
        } else if (currentTime > activityStartTime && currentTime < activityEndTime) {// 活动中
            status = separator + GOING;
        } else {
            status = null == end ? separator + END : end;
        }

        return status;
    }

    /**
     * 是否时同一天
     *
     * @param lastTime
     * @param currTime
     * @return
     */
    public static boolean isSameDay(long lastTime, long currTime) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();

        date.setTime(currTime);
        calendar.setTime(date);
        int curr_dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int curr_year = calendar.get(Calendar.YEAR);
        Log.i(TAG, "curr:" + curr_year + "," + curr_dayOfYear);

        date.setTime(lastTime);
        calendar.setTime(date);
        int last_dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int last_year = calendar.get(Calendar.YEAR);
        Log.i(TAG, "curr:" + last_year + "," + last_dayOfYear);

        return curr_year == last_year && curr_dayOfYear == last_dayOfYear;
    }

    /**
     * 格式化
     *
     * @param seconds
     * @return
     */
    public static String formatSeconds(long seconds) {
        String standardTime;
        if (seconds <= 0) {
            standardTime = "00:00";
        } else if (seconds < 60) {
            standardTime = String.format(Locale.getDefault(), "00:%02d", seconds % 60);
        } else if (seconds < 3600) {
            standardTime = String.format(Locale.getDefault(), "00:%02d:%02d", seconds / 60, seconds % 60);
        } else {
            standardTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", seconds / 3600, seconds % 3600 / 60, seconds % 60);
        }
        return standardTime;
    }


    /**
     * 将标准时间转换为当地时间：
     * 标准时间（一般是GMT时间） ：Sun, 27 Sep 2020 06:52:57 GMT
     * 本地时间（比如CST时间）   ：Sun, 27 Sep 2020 14:52:57 CST
     *
     * @param date     标准时间格式
     * @param timeZone 选择要转换的时区，默认是本地时区
     * @return 转换后的时间, 异常返回-1
     * @throws ParseException
     */
    public static long translateDate(String date, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
        if (timeZone != null) {
            greenwichDate.setTimeZone(timeZone);
        }
        try {
            Date targetDate = greenwichDate.parse(date);
            cal.setTime(targetDate);
            System.out.println(String.format("时区 %s, 时间 %s",
                    cal.getTimeZone().getDisplayName(),
                    greenwichDate.format(targetDate)));
            return cal.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
