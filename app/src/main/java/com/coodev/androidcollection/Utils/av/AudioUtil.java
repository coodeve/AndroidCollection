package com.coodev.androidcollection.Utils.av;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class AudioUtil {

    /**
     * 播放系统提醒铃音
     *
     * @param context
     * @return
     */
    public static Ringtone playRingtone(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, 0);
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context, defaultRingtoneUri);
        ringtone.play();
        return ringtone;
    }
}
