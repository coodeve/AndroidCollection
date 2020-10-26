package com.coodev.androidcollection.widget.video;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

public class ListVideo extends TextureView implements TextureView.SurfaceTextureListener {

    public static final String TAG = ListVideo.class.getSimpleName();

    private Surface mSurface;

    private SurfaceTexture mSurfaceTexture;

    public ListVideo(Context context) {
        this(context, null);
    }

    public ListVideo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(this);
    }

    public ListVideoManager get() {
        return ListVideoManager.getInstance();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
        } else {
            setSurfaceTexture(mSurfaceTexture);
        }
        if (mSurface == null) {
            mSurface = new Surface(mSurfaceTexture);
            get().setSurface(mSurface, mSurfaceTexture);
        }
        // 注意，这里将surface放入到listvideomanager中，便与使用同一个mediaplayer的setSurface方法
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;// 返回false，SurfaceTexture不会被销毁
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
