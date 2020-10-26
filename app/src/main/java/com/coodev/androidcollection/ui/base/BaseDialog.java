package com.coodev.androidcollection.ui.base;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.coodev.androidcollection.R;

import org.greenrobot.eventbus.EventBus;


/**
 * 基于DialogFragment的弹窗，需要使用FragmentActivity
 */
public abstract class BaseDialog extends DialogFragment {

    private View mRootView;
    /**
     * 背景昏暗度
     */
    private float mDimAmount = 0.5f;
    /**
     * 是否底部显示
     */
    private boolean mShowBottomEnable = false;
    /**
     * 左边距
     */
    private int mMargin_l = 0;
    /**
     * 右边距
     */
    private int mMargin_r = 0;
    /**
     * 上边距
     */
    private int mMargin_t = 0;
    /**
     * 下边距
     */
    private int mMargin_b = 0;
    /**
     * 进入退出动画
     */
    private int mAnimStyle = 0;
    /**
     * 点击外部可取消
     */
    private boolean mOutCancel = true;

    /**
     * 宽
     */
    private int mWidth = 0;

    /**
     * 高
     */
    private int mHeight = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BaseDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = specifyLayout(savedInstanceState);
        mRootView = inflater.inflate(layoutResId, container, false);
        findViewsById();
        initViews();
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (useEventBus()){
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                //设置昏暗度
                params.dimAmount = mDimAmount;
                //设置边距
                window.getDecorView().setPadding(mMargin_l, mMargin_t, mMargin_r, mMargin_b);
                //设置dialog显示位置
                if (mShowBottomEnable) {
                    params.gravity = Gravity.BOTTOM;
                }else {
                    params.gravity = Gravity.CENTER;
                }
                //设置dialog宽度
                if (mWidth == 0) {
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                } else {
                    params.width = dp2px(getContext(), mWidth);
                }
                //设置dialog高度
                if (mHeight == 0) {
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                } else {
                    params.height = dp2px(getContext(), mHeight);
                }
                //设置dialog动画
                if (mAnimStyle != 0) {
                    window.setWindowAnimations(mAnimStyle);
                }

                window.setAttributes(params);
            }
            setCancelable(mOutCancel);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 找到控件ID
     */
    protected <T extends View> T findViewById(int id) {
        if (mRootView == null) {
            return null;
        }
        return (T) mRootView.findViewById(id);
    }

    /**
     * 设置背景昏暗度
     * @param dimAmount (0-1)
     * @return
     */
    public BaseDialog setDimAmount(float dimAmount) {
        this.mDimAmount = dimAmount;
        return this;
    }

    /**
     * 设置宽高
     * @param width   宽,单位dp
     * @param height  高,单位dp
     * @return
     */
    public BaseDialog setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        return this;
    }

    /**
     * 是否从底部显示，否则是显示在中心
     * @param b
     * @return
     */
    public BaseDialog setShowInBottom(boolean b) {
        this.mShowBottomEnable = b;
        return this;
    }

    /**
     * 设置margin
     * @param l   左边距
     * @param t   上边距
     * @param r   右边距
     * @param b   下边距
     * @return
     */
    public BaseDialog setMargin(int l,int t,int r,int b) {
        this.mMargin_l = l;
        this.mMargin_t = t;
        this.mMargin_r = r;
        this.mMargin_b = b;
        return this;
    }

    /**
     * 设置进入退出动画
     * @param animStyle   style资源
     * @return
     */
    public BaseDialog setAnimStyle(int animStyle) {
        this.mAnimStyle = animStyle;
        return this;
    }

    /**
     * 设置是否点击外部取消
     * @param cancel
     * @return
     */
    public BaseDialog setCanceledOnTouchOutside(boolean cancel) {
        this.mOutCancel = cancel;
        return this;
    }

    /**
     * 显示
     * @param mFragmentActivity
     * @return
     */
    public BaseDialog show(FragmentActivity mFragmentActivity){
        try {
            super.show(mFragmentActivity.getSupportFragmentManager(), getClass().getSimpleName());
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 指定布局id
     * @param savedInstanceState
     * @return
     */
    protected abstract int specifyLayout(Bundle savedInstanceState);

    /**
     * findViewById
     */
    protected abstract void findViewsById();

    /**
     * 初始化视图控件
     */
    protected abstract void initViews();

    /**
     * 是否使用eventBus
     * @return
     */
    protected abstract boolean useEventBus();

    private int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
