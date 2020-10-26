package com.coodev.androidcollection.widget.webview;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.List;

public class ImageBrowserAdapter extends PagerAdapter {
    private Context context;
    private List<String> picUrls;

    public ImageBrowserAdapter(Context context, List<String> picUrls) {
        this.context = context;
        this.picUrls = picUrls;
    }

    @Override
    public int getCount() {
        return picUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
       PhotoView photoView = new PhotoView(context);
       photoView.enable();
       photoView.setScaleType(ImageView.ScaleType.CENTER);
       ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
       layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
       layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        Log.i("TAG", "instantiateItem: "+picUrls.get(position));
        //显示图片
        Glide.with(context)
                .load(picUrls.get(position))
                .into(photoView);
        container.addView(photoView,layoutParams);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}