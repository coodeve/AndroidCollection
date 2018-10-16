package com.picovr.androidcollection.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.picovr.androidcollection.R;


/**
 * Author EnvisionBoundary
 * Created at  2018/10/16.
 */
public class FilePathButton extends Button implements FilePathView.PathStatus {
    private String path;
    private int padding = 10;
    private int textSize = 20;
    private boolean omit;

    public FilePathButton(Context context) {
        this(context, null);
    }

    public FilePathButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilePathButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(padding, 0, padding, 0);
        setTextSize(textSize);
        setBackgroundResource(R.drawable.btn_bg_color_file_path);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void setPath(String path) {
        setText(getShowText(path));
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void showNormal() {
        setBackgroundResource(R.drawable.btn_bg_color_file_path);
        setEnabled(true);
        setText(getShowText(path));
        setTextColor(Color.BLACK);
        omit = false;
    }

    @Override
    public void showCurrent() {
        setBackgroundResource(R.drawable.btn_bg_color_file_path);
        setText(getShowText(path));
        setTextColor(Color.WHITE);
        setEnabled(false);
    }

    @Override
    public void showOmit() {
        setText("...");
        setBackgroundResource(R.drawable.file_path);
        setEnabled(false);
        this.omit = true;

    }

    @Override
    public boolean getOmit() {
        return omit;
    }

    @Override
    public View getView() {
        return this;
    }

    private String getShowText(String path) {
        return path.equals("/") ? "/" : path.substring(path.lastIndexOf("/") + 1);
    }
}
