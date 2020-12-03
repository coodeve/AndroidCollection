package com.coodev.androidcollection.mvvm.databind;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.coodev.androidcollection.Utils.image.ImageUtils;

/**
 * 自定义dataBinding
 */
public class ImageViewBindingAdapter {
    @BindingAdapter("image")
    public static void setImage(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            ImageUtils.loadImage(imageView.getContext(), imageView, url);
        }
    }

    @BindingAdapter(value = {"image", "imageResource"}, requireAll = false)
    public static void setImage(ImageView imageView, String url, int imageRes) {
        if (!TextUtils.isEmpty(url)) {
            ImageUtils.loadImage(imageView.getContext(), imageView, url);
        } else {
            imageView.setImageResource(imageRes);
        }
    }
}
