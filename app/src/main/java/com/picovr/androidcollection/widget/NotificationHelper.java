package com.picovr.androidcollection.widget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.picovr.androidcollection.MainActivity;
import com.picovr.androidcollection.R;

/**
 * @author patrick.ding
 * @date 18/7/16
 */

public class NotificationHelper {
    private static final String TAG = NotificationHelper.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID = "12138";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "Picovr";
    private int requestCode = 0;
    private int notificationID = 1;
    private final NotificationManager mNotificationManager;
    private final Context mContext;
    private NotificationCompat.Builder builder;

    public NotificationHelper(Context context) {
        this.mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_MAX;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        initNotification();
    }

    private void initNotification() {
        builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        builder.setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

    }


    public void showNotificationBigText(String title, String content, int activity_id) {
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(content)
                .setBigContentTitle(title);
        builder.setStyle(bigTextStyle);
        builder.setContentTitle(title);
        builder.setContentText(content);
        show(activity_id);
    }


    public void showNotificationBigPicture(String title, String content, int activity_id, Bitmap bitmap) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.bigLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .bigPicture(bitmap == null ? BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher) : bitmap)
                .setBigContentTitle(title);
        builder.setStyle(bigPictureStyle)
                .setContentText(content);
        show(activity_id);
    }

    private void showNotification(String title, String content, int activity_id) {
        builder.setContentTitle(title);
        builder.setContentText(content);
        show(activity_id);
    }

    public Notification showNotificationForeground(String title, String content) {
        builder.setContentTitle(title);
        builder.setContentText(content);
        return builder.build();
    }

    private void show(int activity_id) {
        if (activity_id > 0) {
//            Intent intent = new Intent(mContext, EventDetailActivity.class);
//            intent.putExtra(EventConstant.SKIP_TYPE, EventConstant.NET_LOAD);
//            intent.putExtra(EventConstant.ACTIVITY_ID, activity_id);
//            PendingIntent pendingintent = PendingIntent.getActivity(
//                    mContext,
//                    requestCode,
//                    intent,
//                    PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentIntent(taskStackBuilder(activity_id));
        }
        Notification notification = builder.build();
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    private PendingIntent taskStackBuilder(int activity_id) {
        Intent resultIntent = new Intent(mContext, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // 添加返回栈
        stackBuilder.addParentStack(MainActivity.class);
        // 添加Intent到栈顶
        stackBuilder.addNextIntent(resultIntent);
        // 创建包含返回栈的pendingIntent
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }
}
