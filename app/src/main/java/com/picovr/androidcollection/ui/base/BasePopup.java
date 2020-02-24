package com.picovr.androidcollection.ui.base;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import org.greenrobot.eventbus.EventBus;

import razerdp.basepopup.BasePopupWindow;


public abstract class BasePopup extends BasePopupWindow {

    public BasePopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        View view = createPopupById(bindLayoutRes());

        initView(view);

        return view;
    }

    public void show(){
        //如果要使用 EventBus 请将此方法返回 true
        if (useEventBus()) {
            //注册 EventBus
            registerEventBus();
        }
        showPopupWindow();
    }

    @Override
    public void dismiss() {
        super.dismiss();

        //如果要使用了EventBus，要解绑
        if (useEventBus()) {
            //解除注册 EventBus
            unregisterEventBus();
        }
    }

    @Override
    protected Animation onCreateShowAnimation() {
        if (showAnimation() == 0) {
            return super.onCreateShowAnimation();
        }
        Animation showAnimation = AnimationUtils.loadAnimation(getContext(),showAnimation());
        return showAnimation;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        if (dismissAnimation() == 0) {
            return super.onCreateDismissAnimation();
        }
        Animation dismissAnimation = AnimationUtils.loadAnimation(getContext(),dismissAnimation());
        return dismissAnimation;
    }

    /**
     * 注册eventBus
     */
    private void registerEventBus(){
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 解绑eventBus
     */
    private void unregisterEventBus(){
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 绑定布局文件
     * @return 布局文件的ID
     */
    protected abstract int bindLayoutRes();

    /**
     * 子类Fragment是否要使用EventBus
     * @return true 是/false 否
     */
    protected abstract boolean useEventBus();

    /**
     * 初始化视图控件
     * @param contentView 父布局
     */
    protected abstract void initView(View contentView);

    /**
     * 指定显示的动画animation
     * @return
     */
    protected abstract int showAnimation();

    /**
     * 指定退出的动画animation
     * @return
     */
    protected abstract int dismissAnimation();

}
