package com.coodev.androidcollection.ui.unity;

import android.content.Context;

import com.unity3d.player.UnityPlayer;

public class MyUnityPlayer extends UnityPlayer {
    public MyUnityPlayer(Context context) {
        super(context);
    }

    @Override
    protected void kill() {

    }
}
