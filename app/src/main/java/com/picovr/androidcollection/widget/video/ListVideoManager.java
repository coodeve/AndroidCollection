package com.picovr.androidcollection.widget.video;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 播放控制
 */
public class ListVideoManager implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
    public static final String TAG = ListVideoManager.class.getSimpleName();

    private Stack<Surface> mSurfaceStack = new Stack<>();
    private Stack<SurfaceTexture> mSurfaceTextureStack = new Stack<>();

    private List<OnPlayCallback> mOnPlayCallbackList = new LinkedList<>();

    public interface OnPlayCallback {
        void onPrepare();

        void onStart();

        void onPause();

        void onIdle();
    }

    private OnPlayCallback mOnPlayCallback;

    /**
     * 播放状态
     */
    public final static int STATUS_PREPARE = 1;
    public final static int STATUS_PLAYING = 2;
    public final static int STATUS_PAUSE = 3;
    public final static int STATUS_IDLE = 0;

    private int currStatus = STATUS_IDLE;

    private MediaPlayer mMediaPlayer;

    /**
     * 当前播放地址
     */
    private String mCurrPlayUrl;

    private ListVideoManager() {
        mMediaPlayer = getMediaPlayer();
    }


    private final static class ListVideoManagerHolder {
        private final static ListVideoManager LIST_VIDEO_MANAGER = new ListVideoManager();
    }

    public static ListVideoManager getInstance() {
        return ListVideoManagerHolder.LIST_VIDEO_MANAGER;
    }

    public void setOnPlayCallback(OnPlayCallback onPlayCallback) {
        mOnPlayCallback = onPlayCallback;
    }


    public MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnErrorListener(this);
        }
        return mMediaPlayer;
    }

    public void setSurface(Surface surface, SurfaceTexture surfaceTexture) {
        if (surface == null) {
            throw new IllegalArgumentException("surface is null");
        }
        if (mSurfaceStack.search(surface) == -1) {
            mSurfaceStack.push(surface);
        }

        if (mSurfaceTextureStack.search(surface) == -1) {
            mSurfaceTextureStack.push(surfaceTexture);
        }
    }

    /**
     * 播放入口
     *
     * @param url
     */
    public void play(String url) {
        Log.i(TAG, "play: url = " + url);

        if (currStatus != STATUS_IDLE && currStatus != STATUS_PAUSE) {
            Log.w(TAG, "play: currStatus = " + currStatus);
            return;
        }

        if (url == null || "".equals(url)) {
            return;
        }

        getMediaPlayer().setSurface(mSurfaceStack.peek());

        if (url.equals(mCurrPlayUrl)) {// 用于暂停后继续播放
            start();
            return;
        }

        mCurrPlayUrl = url;
        currStatus = STATUS_PREPARE;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mCurrPlayUrl);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public int getStatus() {
        return currStatus;
    }

    private void start() {
        mMediaPlayer.start();
        currStatus = STATUS_PLAYING;
        notifyMediaStatus(STATUS_PLAYING);
    }

    public void stop() {
        if (mMediaPlayer != null && (currStatus == STATUS_PLAYING || currStatus == STATUS_PAUSE)) {
            mMediaPlayer.stop();
            currStatus = STATUS_IDLE;
        }
    }

    public void pause() {
        if (mMediaPlayer != null && currStatus == STATUS_PLAYING) {
            mMediaPlayer.pause();
            currStatus = STATUS_PAUSE;
            notifyMediaStatus(STATUS_PAUSE);
        }
    }

    public void reset() {
        if (mMediaPlayer != null) {
            mCurrPlayUrl = null;
            currStatus = STATUS_IDLE;
            mMediaPlayer.reset();
        }
    }

    public void release() {
        Log.i(TAG, "release: ");

        mCurrPlayUrl = null;

        currStatus = STATUS_IDLE;

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }
        }
        if (!mSurfaceStack.empty()) {
            mSurfaceStack.pop();
        }
        if (!mSurfaceTextureStack.empty()) {
            mSurfaceTextureStack.pop();
        }
    }

    private int onPausePosition;

    public void onResume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(onPausePosition);
            onPausePosition = 0;
        }
    }

    public void onPause() {
        if (mMediaPlayer != null) {
            pause();
            onPausePosition = mMediaPlayer.getCurrentPosition();
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG, "onPrepared: ");
        mp.start();
        currStatus = STATUS_PLAYING;
        notifyMediaStatus(STATUS_PLAYING);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "onCompletion: ");
        reset();
        notifyMediaStatus(STATUS_IDLE);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onInfo: what=" + what + ",extra=" + extra);
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        notifyMediaStatus(STATUS_PREPARE);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        reset();
        Log.i(TAG, "onError: what=" + what + ",extra=" + extra);
        notifyMediaStatus(STATUS_IDLE);
        return false;
    }

    public void removeListener(OnPlayCallback onPlayCallback) {
        mOnPlayCallbackList.add(onPlayCallback);
    }

    public void addListener(OnPlayCallback onPlayCallback) {
        mOnPlayCallbackList.remove(onPlayCallback);
    }

    private void notifyMediaStatus(int status) {
        for (OnPlayCallback callback : mOnPlayCallbackList) {
            switch (status) {
                case STATUS_IDLE:
                    callback.onIdle();
                    break;
                case STATUS_PREPARE:
                    callback.onPrepare();
                    break;
                case STATUS_PAUSE:
                    callback.onPause();
                    break;
                case STATUS_PLAYING:
                    callback.onStart();
                    break;
            }
        }
    }

}